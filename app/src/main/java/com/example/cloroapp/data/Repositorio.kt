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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.io.IOException


data class EstadoRed (
    val cargando: Boolean = false,
    val error: String? = null,
    val ultimaActualizacion: String? = null
)
object Repositorio {
    suspend fun login(correo: String, clave: String): Result<Usuario>{
        return try {
            val dto = RetrofitClient.apiService.login(LoginRequest(correo.trim(), clave))
            Result.success(dto.aModelo())
        } catch (e: HttpException) {
            Result.failure(Exception("Correo o contraseña incorrectos"))
        } catch (e: IOException) {
            val local = Usuarios.autenticar(correo, clave)
            if (local != null) Result.success(local)
            else Result.failure(Exception("Sin conexión con el servidor"))
        }
    }

    private val _granjas = MutableStateFlow<List<Granja>>(emptyList())
    val granjas: StateFlow<List<Granja>> = _granjas
    private var puntoDao: PuntoDao? = null
    private val _estado = MutableStateFlow(EstadoRed())
    private val cacheHistorial = mutableMapOf<String, List<Double>>()
    val estado: StateFlow<EstadoRed> = _estado

    private var prefs: SharedPreferences? = null

    private fun registrarError(mensaje: String) {
        _estado.value = _estado.value.copy(cargando = false, error = mensaje)
    }
    fun iniciar(context: Context) {
        puntoDao = AppDatabase.getDatabase(context).puntoDao()
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

    suspend fun cargarGranjas(): Result<Unit> {
        _estado.value = _estado.value.copy(cargando = true, error = null)

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
}
