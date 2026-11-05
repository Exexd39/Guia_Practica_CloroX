package com.example.cloroapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cloroapp.data.Repositorio
import com.example.cloroapp.data.model.Usuario
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// Todo lo que la pantalla necesita para dibujarse
data class LoginUiState(
    val correo: String = "",
    val clave: String = "",
    val errorCorreo: String? = null,
    val errorClave: String? = null,
    val errorGeneral: String? = null,
    val cargando: Boolean = false
)

class LoginViewModel : ViewModel() {
    private val _estado = MutableStateFlow(LoginUiState())
    val estado: StateFlow<LoginUiState> = _estado

    fun onCorreoChange(valor: String) {
        _estado.update { it.copy(correo = valor, errorCorreo = null, errorGeneral = null) }
    }

    fun onClaveChange(valor: String) {
        _estado.update { it.copy(clave = valor, errorClave = null, errorGeneral = null) }
    }

    fun ingresar(onExito: (Usuario) -> Unit) {
        val actual = _estado.value
        val errorCorreo = Validaciones.correo(actual.correo)
        val errorClave = Validaciones.clave(actual.clave)
        if (errorCorreo != null || errorClave != null) {
            _estado.update { it.copy(errorCorreo = errorCorreo, errorClave = errorClave) }
            return
        }
        viewModelScope.launch {
            _estado.update { it.copy(cargando = true, errorGeneral = null) }
            Repositorio.login(actual.correo, actual.clave)
                .onSuccess { onExito(it) }
                .onFailure { e -> _estado.update { it.copy(errorGeneral = e.message) } }
            _estado.update { it.copy(cargando = false) }
        }
    }
}