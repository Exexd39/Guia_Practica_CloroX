package com.example.cloroapp.data.model

data class Alerta(
    val id: String,
    val idPunto: String,
    val idGranja: String,
    val gravedad: EstadoCloro,
    val desviacion: TipoDesviacion,
    val nivelCloro: Double,
    val fechaHora: String,
    val tieneAccion: Boolean
)