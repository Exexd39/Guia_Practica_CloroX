package com.example.cloroapp.data

import androidx.compose.runtime.mutableStateListOf
import com.example.cloroapp.data.model.RegistroAccion

// Guarda las acciones en memoria. Se pierden al cerrar la app;
// Room lo reemplazará más adelante.
object RegistroAcciones {
    val lista = mutableStateListOf<RegistroAccion>()

    fun de(puntoId: String): List<RegistroAccion> =
        lista.filter { it.puntoId == puntoId }.reversed()
}