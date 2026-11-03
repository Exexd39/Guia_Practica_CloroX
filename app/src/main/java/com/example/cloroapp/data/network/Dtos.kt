package com.example.cloroapp.data.network

import com.example.cloroapp.data.model.Alerta
import com.example.cloroapp.data.model.EstadoCloro
import com.example.cloroapp.data.model.Granja
import com.example.cloroapp.data.model.PuntoMedicion
import com.example.cloroapp.data.model.Rol
import com.example.cloroapp.data.model.TipoDesviacion
import com.example.cloroapp.data.model.TipoPunto
import com.example.cloroapp.data.model.Usuario
import com.google.gson.annotations.SerializedName


// Esto lo mandamos al servidor
data class LoginRequest(
    val correo: String,
    val clave: String
)

// Se recibe sin clave
data class UsuarioDto(
    val id: String,
    val nombre: String,
    val correo: String,
    val rol: String,
    val granjasAsignadas: List<String>
)

data class AlertaDto(
    val id: String,
    @SerializedName("id_punto") val idPunto: String,
    @SerializedName("id_granja") val idGranja: String,
    val gravedad: String,
    val desviacion: String,
    @SerializedName("nivel_cloro") val nivelCloro: Double,
    val estado: String,
    @SerializedName("fecha_hora") val fechaHora: String
)

data class AccionDto(
    val id: String,
    @SerializedName("id_punto") val idPunto: String,
    @SerializedName("id_alerta") val idAlerta: String?,
    val tipo: String,
    val observaciones: String,
    val responsable: String,
    val rol: String,
    val fecha: String,
    val hora: String
)

data class AccionRequest(
    @SerializedName("id_punto") val idPunto: String,
    @SerializedName("id_usuario") val idUsuario: String,
    val tipo: String,
    val observaciones: String
)

data class CierreRequest(
    @SerializedName("id_usuario") val idUsuario: String
)

fun AlertaDto.aModelo(tieneAccion: Boolean) = Alerta(
    id = id,
    idPunto = idPunto,
    idGranja = idGranja,
    gravedad = EstadoCloro.entries.firstOrNull { it.name.equals(gravedad, ignoreCase = true) }
        ?: EstadoCloro.ADVERTENCIA,
    desviacion = TipoDesviacion.entries.firstOrNull {
        it.name.equals(
            desviacion,
            ignoreCase = true
        )
    }
        ?: TipoDesviacion.NINGUNA,
    nivelCloro = nivelCloro,
    fechaHora = fechaHora,
    tieneAccion = tieneAccion
)

// Convierte el DTO al model que usamos en pantalla
fun UsuarioDto.aModelo() = Usuario(
    id = id,
    nombre = nombre,
    correo = correo,
    clave = "",
    rol = Rol.valueOf(rol),
    granjasAsignadas = granjasAsignadas
)

data class PuntoDto(
    val id: String,
    val codigo: String,
    val nombre: String,
    val tipo: String,
    val ubicacion: String,
    val cloroLibre: Double,
    val fecha: String,
    val hora: String
)

data class MedicionDto(
    @SerializedName("nivel_cloro") val nivelCloro: Double,
    @SerializedName("fecha_hora") val fechaHora: String
)

data class GranjaDto(
    val id: String,
    val nombre: String,
    val puntos: List<PuntoDto>
)

fun PuntoDto.aModelo() = PuntoMedicion(
    id = id,
    codigo = codigo,
    nombre = nombre,
    tipo = TipoPunto.entries.firstOrNull { it.name.equals(tipo, ignoreCase = true) }
        ?: TipoPunto.LINEA,
    ubicacion = ubicacion,
    cloroLibre = cloroLibre,
    fecha = fecha,
    hora = hora
)

fun GranjaDto.aModelo() = Granja(id, nombre, puntos.map { it.aModelo() })