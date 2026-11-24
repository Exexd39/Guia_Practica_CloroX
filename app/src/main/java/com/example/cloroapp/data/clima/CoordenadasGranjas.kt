package com.example.cloroapp.data.clima

data class Coordenada(val latitud: Double, val longitud: Double)

object CoordenadasGranjas {
    private val melipilla = Coordenada(-33.6869, -71.2143)

    fun de(idGranja: String): Coordenada = melipilla
}