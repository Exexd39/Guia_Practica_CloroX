package com.example.cloroapp.data

import androidx.compose.runtime.mutableStateListOf
import com.example.cloroapp.data.model.RegistroAccion

// Guarda acciones y cierres en memoria. Se pierden al cerrar la app;
// Room lo reemplazará más adelante.
object RegistroAcciones {
    val lista = mutableStateListOf<RegistroAccion>()
    val cerradas = mutableStateListOf<String>()

    fun de(puntoId: String): List<RegistroAccion> =
        lista.filter { it.puntoId == puntoId }.reversed()

    fun tieneAcciones(puntoId: String): Boolean =
        lista.any { it.puntoId == puntoId }

    fun estaCerrada(puntoId: String): Boolean = puntoId in cerradas

    fun cerrar(puntoId: String) {
        if (!estaCerrada(puntoId)) cerradas.add(puntoId)
    }
}