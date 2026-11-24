package com.example.cloroapp.ui.viewmodel

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Test

class LoginViewModelTest {

    private val viewModel = LoginViewModel()

    @Test
    fun ingresarConCamposVaciosMuestraErroresYNoAvanza() {
        var exito = false

        viewModel.ingresar { exito = true }

        val estado = viewModel.estado.value
        assertEquals("Ingresa tu correo", estado.errorCorreo)
        assertEquals("Ingresa tu contraseña", estado.errorClave)
        assertFalse(exito)
    }

    @Test
    fun correoConFormatoInvalidoMuestraErrorDeFormato() {
        viewModel.onCorreoChange("operario@cloro")
        viewModel.onClaveChange("1234")

        viewModel.ingresar { }

        assertEquals("El correo no tiene un formato válido", viewModel.estado.value.errorCorreo)
        assertNull(viewModel.estado.value.errorClave)
    }

    @Test
    fun claveVaciaConCorreoValidoSoloMarcaLaClave() {
        viewModel.onCorreoChange("operario@cloro.cl")

        viewModel.ingresar { }

        assertNull(viewModel.estado.value.errorCorreo)
        assertEquals("Ingresa tu contraseña", viewModel.estado.value.errorClave)
    }

    @Test
    fun escribirEnElCampoLimpiaSuError() {
        viewModel.ingresar { }

        viewModel.onCorreoChange("a")

        assertNull(viewModel.estado.value.errorCorreo)
        assertNull(viewModel.estado.value.errorGeneral)
    }
}