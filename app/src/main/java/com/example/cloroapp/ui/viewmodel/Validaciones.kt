package com.example.cloroapp.ui.viewmodel

// Reglas de validacion centralizadas: no dependen de ninguna pantalla.
// Devuelven el mensaje de error, o null si el valor es válido.
object Validaciones {
    private val REGEX_CORREO = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")

    fun correo(valor: String): String? = when {
        valor.isBlank() -> "Ingresa tu correo"
        !REGEX_CORREO.matches(valor.trim()) -> "El correo no tiene un formato válido"
        else -> null
    }

    fun clave(valor: String): String? =
        if (valor.isEmpty()) "Ingresa tu contraseña" else null

    fun cloro(texto: String): String? {
        val valor = texto.replace(',', '.').toDoubleOrNull()
        return when {
            texto.isBlank() -> "Ingresa el valor medido"
            valor == null -> "Escribe un número, por ejemplo 3.50"
            valor < 0 || valor > 20 -> "El valor debe estar entre 0 y 20 mg/L"
            else -> null
        }
    }

    fun observaciones(valor: String): String? =
        if (valor.trim().length < 3) "Escribe al menos 3 caracteres" else null
}