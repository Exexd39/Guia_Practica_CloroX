package com.example.cloroapp.ui.viewmodel

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ValidacionUsuarioTest {

    @Test
    fun nombreVacioDaError() {
        assertEquals("Ingresa el nombre", ValidacionUsuario.nombre("  "))
    }

    @Test
    fun nombreValidoNoDaError() {
        assertNull(ValidacionUsuario.nombre("Ana"))
    }

    @Test
    fun claveObligatoriaAlCrear() {
        assertEquals("La clave es obligatoria", ValidacionUsuario.clave("", esNuevo = true))
    }

    @Test
    fun claveVaciaAlEditarNoDaError() {
        assertNull(ValidacionUsuario.clave("", esNuevo = false))
    }
}