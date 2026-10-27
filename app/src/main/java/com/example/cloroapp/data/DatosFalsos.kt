package com.example.cloroapp.data

import com.example.cloroapp.data.model.Granja
import com.example.cloroapp.data.model.PuntoMedicion
import com.example.cloroapp.data.model.TipoPunto

object DatosFalsos {
    private const val FECHA = "21-10-2026"

    private fun punto(
        id: String,
        codigo: String,
        nombre: String,
        tipo: TipoPunto,
        ubicacion: String,
        valor: Double,
        hora: String
    ) = PuntoMedicion(id, codigo, nombre, tipo, ubicacion, valor, FECHA, hora)

    val granjas = listOf(
        Granja(
            "g1", "Granja Norte", listOf(
                punto("p1", "N-TQ-01", "Tanque principal", TipoPunto.TANQUE, "Sala de bombas", 4.20, "10:05"),
                punto("p2", "N-LI-01", "Línea galpón 1", TipoPunto.LINEA, "Galpón 1", 3.60, "10:10"),
                punto("p3", "N-BE-02", "Bebedero galpón 2", TipoPunto.BEBEDERO, "Galpón 2", 2.50, "10:15"),
                punto("p4", "N-BE-03", "Bebedero galpón 3", TipoPunto.BEBEDERO, "Galpón 3", 5.40, "10:20")
            )
        ),
        Granja(
            "g2", "Granja Sur", listOf(
                punto("p5", "S-TQ-01", "Tanque principal", TipoPunto.TANQUE, "Sala de bombas", 4.80, "10:02"),
                punto("p6", "S-LI-01", "Línea galpón 1", TipoPunto.LINEA, "Galpón 1", 3.10, "10:07"),
                punto("p7", "S-BE-02", "Bebedero galpón 2", TipoPunto.BEBEDERO, "Galpón 2", 4.00, "10:12"),
                punto("p8", "S-BE-03", "Bebedero galpón 3", TipoPunto.BEBEDERO, "Galpón 3", 1.80, "10:18")
            )
        ),
        Granja(
            "g3", "Granja Melipilla", listOf(
                punto("p9", "M-TQ-01", "Tanque principal", TipoPunto.TANQUE, "Sala de bombas", 6.30, "10:03"),
                punto("p10", "M-LI-01", "Línea galpón 1", TipoPunto.LINEA, "Galpón 1", 4.50, "10:09"),
                punto("p11", "M-BE-02", "Bebedero galpón 2", TipoPunto.BEBEDERO, "Galpón 2", 3.80, "10:14"),
                punto("p12", "M-BE-03", "Bebedero galpón 3", TipoPunto.BEBEDERO, "Galpón 3", 2.10, "10:21")
            )
        )
    )

    fun buscarPunto(id: String): Pair<Granja, PuntoMedicion>? {
        for (granja in granjas) {
            val punto = granja.puntos.find { it.id == id }
            if (punto != null) return granja to punto
        }
        return null
    }

    // Histórico simulado de 8 mediciones que termina en el valor actual.
    // Más adelante se reemplaza por filtros de 24 horas, 7 días y 30 días.
    fun historial(valorActual: Double): List<Double> =
        listOf(3.9, 4.1, 3.7, 3.4, 3.0, 2.8, 2.6, valorActual)
}