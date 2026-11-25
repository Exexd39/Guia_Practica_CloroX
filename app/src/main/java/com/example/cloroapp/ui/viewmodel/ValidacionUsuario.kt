package com.example.cloroapp.ui.viewmodel

object ValidacionUsuario {
    fun nombre(valor: String): String? =
        if (valor.isBlank()) "Ingresa el nombre" else null

    fun clave(valor: String, esNuevo: Boolean): String? =
        if (esNuevo && valor.isBlank()) "La clave es obligatoria" else null
}