package com.example.cloroapp.data

import android.content.Context
import com.example.cloroapp.data.local.AppDatabase
import com.example.cloroapp.data.local.PuntoDao
import com.example.cloroapp.data.model.Granja
import com.example.cloroapp.data.model.PuntoEntity
import com.example.cloroapp.data.model.PuntoMedicion
import com.example.cloroapp.data.model.Usuario
import com.example.cloroapp.data.network.GranjaDto
import com.example.cloroapp.data.network.LoginRequest
import com.example.cloroapp.data.network.PuntoDto
import com.example.cloroapp.data.network.RetrofitClient
import com.example.cloroapp.data.network.aModelo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import retrofit2.HttpException
import android.content.SharedPreferences
import com.example.cloroapp.data.local.PendienteDao
import com.example.cloroapp.data.model.Alerta
import com.example.cloroapp.data.model.EstadoCloro
import com.example.cloroapp.data.model.PendienteEntity
import com.example.cloroapp.data.model.RegistroAccion
import com.example.cloroapp.data.model.SesionActual
import com.example.cloroapp.data.model.TipoDesviacion
import com.example.cloroapp.data.network.AccionRequest
import com.example.cloroapp.data.network.CierreRequest
import com.example.cloroapp.data.network.MedicionRequest
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.io.IOException


data class EstadoRed (
    val cargando: Boolean = false,
    val error: String? = null,
    val ultimaActualizacion: String? = null
)
data class ResultadoEnvio(
    val estado: EstadoCloro?,
    val hayAlerta: Boolean,
    val pendiente: Boolean = false
)

object Repositorio {
    suspend fun login(correo: String, clave: String): Result<Usuario> {
        return try {
            val dto = RetrofitClient.apiService.login(LoginRequest(correo.trim(), clave))
            val usuario = dto.aModelo()
            guardarSesion(usuario)
            Result.success(usuario)
        } catch (e: HttpException) {
            Result.failure(Exception("Correo o contraseña incorrectos"))
        } catch (e: IOException) {
            val local = Usuarios.autenticar(correo, clave)
            if (local != null) {
                guardarSesion(local)
                Result.success(local)
            } else {
                Result.failure(Exception("Sin conexión con el servidor"))
            }
        }
    }

    suspend fun registrarMedicion(idPunto: String, nivel: Double): Result<ResultadoEnvio> {
        return try {
            val resp = RetrofitClient.apiService
                .registrarMedicion(MedicionRequest(idPunto, nivel, null))
            cargarGranjas()
            val estado = EstadoCloro.entries
                .firstOrNull { it.name.equals(resp.datos.estado, ignoreCase = true) }
            Result.success(ResultadoEnvio(estado, resp.alerta != null))
        } catch (e: HttpException) {
            Result.failure(Exception(mensajeDe(e)))
        } catch (e: IOException) {
            val dao = pendienteDao
                ?: return Result.failure(Exception("No se pudo guardar la medición en el celular"))
            val ahora = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(Date())
            dao.guardar(PendienteEntity(idPunto = idPunto, nivelCloro = nivel, fechaHora = ahora))
            actualizarContador()
            Result.success(ResultadoEnvio(null, false, pendiente = true))
        }
    }

    private val _granjas = MutableStateFlow<List<Granja>>(emptyList())
    val granjas: StateFlow<List<Granja>> = _granjas
    private var puntoDao: PuntoDao? = null
    private val _estado = MutableStateFlow(EstadoRed())
    private val cacheHistorial = mutableMapOf<String, List<Double>>()
    val estado: StateFlow<EstadoRed> = _estado

    private var prefs: SharedPreferences? = null
    private val _alertas = MutableStateFlow<List<Alerta>>(emptyList())
    val alertas: StateFlow<List<Alerta>> = _alertas

    private var pendienteDao: PendienteDao? = null
    private val _pendientes = MutableStateFlow(0)
    val pendientes: StateFlow<Int> = _pendientes

    private fun registrarError(mensaje: String) {
        _estado.value = _estado.value.copy(cargando = false, error = mensaje)
    }

    fun iniciar(context: Context) {
        if (prefs != null) return
        val db = AppDatabase.getDatabase(context)
        puntoDao = db.puntoDao()
        pendienteDao = db.pendienteDao()
        prefs = context.getSharedPreferences("cloro_prefs", Context.MODE_PRIVATE)
        _estado.value = EstadoRed(ultimaActualizacion = prefs?.getString("ultima_sync", null))
    }

    private suspend fun leerLocal(): List<Granja> {
        val filas = puntoDao?.obtenerTodos() ?: return emptyList()
        return filas.groupBy { it.idGranja }.map { (id, puntos) ->
            Granja(
                id = id,
                nombre = puntos.first().nombreGranja,
                puntos = puntos.map {
                    PuntoDto(
                        it.id, it.codigo, it.nombre, it.tipo, it.ubicacion,
                        it.cloroLibre, it.fecha, it.hora
                    ).aModelo()
                }
            )
        }
    }

    suspend fun cargarHistorial(idPunto: String, periodo: String): Result<List<Double>> {
        val clave = "$idPunto|$periodo"
        return try {
            val valores = RetrofitClient.apiService
                .obtenerMediciones(idPunto, periodo)
                .map { it.nivelCloro }
            cacheHistorial[clave] = valores
            Result.success(valores)
        } catch (e: HttpException) {
            Result.failure(Exception("El servidor respondió con error (${e.code()})"))
        } catch (e: IOException) {
            val guardado = cacheHistorial[clave]
            if (guardado != null) Result.success(guardado)
            else Result.failure(Exception("Sin conexión: este historial aún no se ha descargado"))
        }
    }

    private suspend fun guardarLocal(remotas: List<GranjaDto>) {
        val filas = remotas.flatMap { g ->
            g.puntos.map { p ->
                PuntoEntity(
                    p.id, g.id, g.nombre, p.codigo, p.nombre, p.tipo,
                    p.ubicacion, p.cloroLibre, p.fecha, p.hora
                )
            }
        }
        puntoDao?.guardar(filas)
    }

    private suspend fun actualizarContador() {
        _pendientes.value = pendienteDao?.obtenerTodas()?.size ?: 0
    }
    suspend fun sincronizarPendientes() {
        val dao = pendienteDao ?: return
        for (p in dao.obtenerTodas()) {
            try {
                RetrofitClient.apiService.registrarMedicion(
                    MedicionRequest(p.idPunto, p.nivelCloro, p.fechaHora)
                )
                dao.borrar(p.id)
            } catch (e: HttpException) {
                dao.borrar(p.id)   // el servidor la rechazó: reintentar no sirve
            } catch (e: IOException) {
                break              // sigue sin conexión: se intenta otro día
            }
        }
        actualizarContador()
    }
    suspend fun cargarGranjas(): Result<Unit> {
        _estado.value = _estado.value.copy(cargando = true, error = null)
        sincronizarPendientes()

        if (_granjas.value.isEmpty()) {
            _granjas.value = leerLocal()
        }
        return try {
            val remotas = RetrofitClient.apiService.obtenerResumenGranjas()
            _granjas.value = remotas.map { it.aModelo() }
            guardarLocal(remotas)

            val ahora = SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault()).format(Date())
            prefs?.edit()?.putString("ultima_sync", ahora)?.apply()
            _estado.value = EstadoRed(ultimaActualizacion = ahora)
            Result.success(Unit)
        } catch (e: HttpException) {
            val msg = "El servidor respondió con error (${e.code()})"
            registrarError(msg)
            Result.failure(Exception(msg))
        } catch (e: IOException) {
            val msg = "Sin conexión con el servidor"
            registrarError(msg)
            Result.failure(Exception(msg))
        }
    }

    fun buscarPunto(id: String, todas: List<Granja>): Pair<Granja, PuntoMedicion>? {
        for (granja in todas) {
            val punto = granja.puntos.find { it.id == id }
            if (punto != null)
                return granja to punto
        }
        return null
    }

    private fun mensajeDe(e: HttpException): String {
        return try {
            val cuerpo = e.response()?.errorBody()?.string() ?: ""
            JSONObject(cuerpo).getString("detail")
        } catch (ex: Exception) {
            "El servidor respondió con error (${e.code()})"
        }
    }

    suspend fun cargarAlertas(): Result<Unit> {
        return try {
            val conAccion = RetrofitClient.apiService.obtenerAcciones(null)
                .mapNotNull { it.idAlerta }
                .toSet()
            _alertas.value = RetrofitClient.apiService.obtenerAlertas("ABIERTA")
                .map { it.aModelo(it.id in conAccion) }
            Result.success(Unit)
        } catch (e: HttpException) {
            Result.failure(Exception(mensajeDe(e)))
        } catch (e: IOException) {
            Result.failure(Exception("Sin conexión con el servidor"))
        }
    }

    suspend fun cargarAcciones(idPunto: String): Result<List<RegistroAccion>> {
        return try {
            val lista = RetrofitClient.apiService.obtenerAcciones(idPunto)
                .map {
                    RegistroAccion(
                        it.idPunto,
                        it.tipo,
                        it.observaciones,
                        "${it.fecha} ${it.hora}",
                        it.rol
                    )
                }
                .reversed()
            Result.success(lista)
        } catch (e: HttpException) {
            Result.failure(Exception(mensajeDe(e)))
        } catch (e: IOException) {
            Result.failure(Exception("Sin conexión con el servidor"))
        }
    }

    suspend fun registrarAccion(
        idPunto: String,
        tipo: String,
        observaciones: String
    ): Result<Unit> {
        val usuario = SesionActual.usuario
            ?: return Result.failure(Exception("No hay sesión iniciada"))
        return try {
            RetrofitClient.apiService.registrarAccion(
                AccionRequest(idPunto, usuario.id, tipo, observaciones)
            )
            cargarAlertas()
            Result.success(Unit)
        } catch (e: HttpException) {
            Result.failure(Exception(mensajeDe(e)))
        } catch (e: IOException) {
            Result.failure(Exception("Sin conexión: la acción no se guardó"))
        }
    }

    suspend fun cerrarAlerta(idAlerta: String): Result<Unit> {
        val usuario = SesionActual.usuario
            ?: return Result.failure(Exception("No hay sesión iniciada"))
        return try {
            RetrofitClient.apiService.cerrarAlerta(idAlerta, CierreRequest(usuario.id))
            cargarAlertas()
            Result.success(Unit)
        } catch (e: HttpException) {
            Result.failure(Exception(mensajeDe(e)))
        } catch (e: IOException) {
            Result.failure(Exception("Sin conexión: no se pudo cerrar la alerta"))
        }
    }

    // Recordar quién inició sesión, para que el Worker sepa a quién avisar
    private fun guardarSesion(u: Usuario) {
        prefs?.edit()
            ?.putString("rol", u.rol.name)
            ?.putStringSet("granjas", u.granjasAsignadas.toSet())
            ?.apply()
    }

    fun cerrarSesion() {
        prefs?.edit()?.remove("rol")?.remove("granjas")?.apply()
    }

    suspend fun revisarAlertasCriticas(context: Context) {
        val rol = prefs?.getString("rol", null) ?: return
        if (rol == "JEFATURA") return
        val misGranjas = prefs?.getStringSet("granjas", emptySet()) ?: emptySet()
        if (cargarAlertas().isFailure) return

        val conocidas = _granjas.value.ifEmpty { leerLocal() }
        val yaAvisadas = prefs?.getStringSet("notificadas", emptySet())?.toMutableSet()
            ?: mutableSetOf()

        val nuevas = _alertas.value.filter {
            it.gravedad == EstadoCloro.CRITICO && it.idGranja in misGranjas && it.id !in yaAvisadas
        }

        for (a in nuevas) {
            val granja = conocidas.find { it.id == a.idGranja }
            val punto = granja?.puntos?.find { it.id == a.idPunto }
            val lugar = if (granja != null && punto != null) "${granja.nombre} · ${punto.nombre}" else a.idPunto
            val tipo = if (a.desviacion == TipoDesviacion.INSUFICIENTE) "cloro insuficiente" else "cloro excesivo"

            Notificador.mostrar(
                context,
                a.id.hashCode(),
                "Alerta crítica de cloro",
                "$lugar: ${"%.2f".format(a.nivelCloro)} mg/L ($tipo)"
            )
            yaAvisadas.add(a.id)
        }
        prefs?.edit()?.putStringSet("notificadas", yaAvisadas)?.apply()
    }
}