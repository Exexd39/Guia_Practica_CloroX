package com.example.cloroapp.data.model

enum class EstadoCloro { NORMAL, ADVERTENCIA, CRITICO }

data class PuntoMedicion(
    val id: String,
    val nombre: String,
    val cloroLibre: Double,
    val estado: EstadoCloro,
    val actualizado: String
)

data class Granja(
    val id: String,
    val nombre: String,
    val puntos: List<PuntoMedicion>
) {
    // El estado de la granja es el peor estado de sus puntos
    val estadoGeneral: EstadoCloro
        get() = puntos.maxByOrNull { it.estado.ordinal }?.estado ?: EstadoCloro.NORMAL
}