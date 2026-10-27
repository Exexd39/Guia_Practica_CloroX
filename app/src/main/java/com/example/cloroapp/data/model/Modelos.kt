package com.example.cloroapp.data.model

enum class EstadoCloro { NORMAL, ADVERTENCIA, CRITICO }

enum class TipoDesviacion { NINGUNA, INSUFICIENTE, EXCESIVO }

enum class TipoPunto(val nombre: String) {
    TANQUE("Tanque"),
    LINEA("Línea de distribución"),
    BEBEDERO("Bebedero")
}

// Rangos de cloro libre en ppm (mg/L). Son configurables por punto.
data class RangoCloro(
    val minNormal: Double = 3.0,
    val maxNormal: Double = 5.0,
    val minAdvertencia: Double = 2.0,
    val maxAdvertencia: Double = 6.0
) {
    fun clasificar(valor: Double): EstadoCloro = when {
        valor in minNormal..maxNormal -> EstadoCloro.NORMAL
        valor in minAdvertencia..maxAdvertencia -> EstadoCloro.ADVERTENCIA
        else -> EstadoCloro.CRITICO
    }

    fun desviacion(valor: Double): TipoDesviacion = when {
        valor in minNormal..maxNormal -> TipoDesviacion.NINGUNA
        valor < minNormal -> TipoDesviacion.INSUFICIENTE
        else -> TipoDesviacion.EXCESIVO
    }
}

data class PuntoMedicion(
    val id: String,
    val codigo: String,
    val nombre: String,
    val tipo: TipoPunto,
    val ubicacion: String,
    val cloroLibre: Double,
    val fecha: String,
    val hora: String,
    val rango: RangoCloro = RangoCloro()
) {
    val estado: EstadoCloro get() = rango.clasificar(cloroLibre)
    val desviacion: TipoDesviacion get() = rango.desviacion(cloroLibre)

    // Se mantiene para no romper las pantallas existentes
    val actualizado: String get() = hora
}

data class Granja(
    val id: String,
    val nombre: String,
    val puntos: List<PuntoMedicion>
) {
    // El estado de la granja es el peor estado de sus puntos
    val estadoGeneral: EstadoCloro
        get() = puntos.maxByOrNull { it.estado.ordinal }?.estado ?: EstadoCloro.NORMAL
}

enum class Rol(val nombre: String, val descripcion: String) {
    OPERARIO("Operario", "Consulta mediciones y registra controles"),
    SUPERVISOR("Supervisor", "Revisa alertas, gestiona desviaciones y cierra alertas"),
    JEFATURA("Jefatura", "Consulta indicadores, históricos y reportes")
}

// Guarda el perfil que inició sesión (simulado)
data class Usuario(
    val id: String,
    val nombre: String,
    val correo: String,
    val clave: String,
    val rol: Rol,
    val granjasAsignadas: List<String>
)

object SesionActual {
    var usuario: Usuario? = null
    val rol: Rol get() = usuario?.rol ?: Rol.OPERARIO
}

data class RegistroAccion(
    val puntoId: String,
    val tipo: String,
    val observaciones: String,
    val fecha: String,
    val rol: String
)