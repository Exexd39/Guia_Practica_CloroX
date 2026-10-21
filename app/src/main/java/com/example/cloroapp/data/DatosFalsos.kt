package com.example.cloroapp.data

import com.example.cloroapp.data.model.EstadoCloro
import com.example.cloroapp.data.model.Granja
import com.example.cloroapp.data.model.PuntoMedicion

object DatosFalsos {
    val granjas = listOf(
        Granja(
            id = "g1",
            nombre = "Granja Norte",
            puntos = listOf(
                PuntoMedicion("p1", "Galpón 1", 1.2, EstadoCloro.NORMAL, "10:15"),
                PuntoMedicion("p2", "Galpón 2", 0.6, EstadoCloro.ADVERTENCIA, "10:20")
            )
        ),
        Granja(
            id = "g2",
            nombre = "Granja Sur",
            puntos = listOf(
                PuntoMedicion("p3", "Galpón 1", 1.0, EstadoCloro.NORMAL, "10:05"),
                PuntoMedicion("p4", "Galpón 2", 1.1, EstadoCloro.NORMAL, "10:08")
            )
        ),
        Granja(
            id = "g3",
            nombre = "Granja Melipilla",
            puntos = listOf(
                PuntoMedicion("p5", "Galpón 1", 0.1, EstadoCloro.CRITICO, "10:30")
            )
        )
    )
}