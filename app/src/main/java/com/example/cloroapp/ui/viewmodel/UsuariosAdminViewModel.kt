package com.example.cloroapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cloroapp.data.network.UsuarioDto
import com.example.cloroapp.data.usuarios.UsuarioRequest
import com.example.cloroapp.data.usuarios.UsuariosAdminRepositorio
import com.example.cloroapp.data.usuarios.mensajeUsuario
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class UsuariosAdminUiState(
    val usuarios: List<UsuarioDto> = emptyList(),
    val cargando: Boolean = false,
    val error: String? = null
)

class UsuariosAdminViewModel : ViewModel() {
    private val _estado = MutableStateFlow(UsuariosAdminUiState())
    val estado: StateFlow<UsuariosAdminUiState> = _estado

    init {
        cargar()
    }

    fun cargar() {
        viewModelScope.launch {
            _estado.update { it.copy(cargando = true, error = null) }
            UsuariosAdminRepositorio.listar()
                .onSuccess { lista -> _estado.update { it.copy(usuarios = lista) } }
                .onFailure { e -> _estado.update { it.copy(error = e.mensajeUsuario()) } }
            _estado.update { it.copy(cargando = false) }
        }
    }

    // id == null significa crear; si trae id, es editar
    fun guardar(id: String?, datos: UsuarioRequest, alGuardar: () -> Unit) {
        viewModelScope.launch {
            _estado.update { it.copy(cargando = true, error = null) }
            val resultado = if (id == null) {
                UsuariosAdminRepositorio.crear(datos)
            } else {
                UsuariosAdminRepositorio.actualizar(id, datos)
            }
            resultado
                .onSuccess {
                    alGuardar()
                    cargar()
                }
                .onFailure { e ->
                    _estado.update { it.copy(cargando = false, error = e.mensajeUsuario()) }
                }
        }
    }

    fun eliminar(id: String) {
        viewModelScope.launch {
            UsuariosAdminRepositorio.eliminar(id)
                .onSuccess { cargar() }
                .onFailure { e -> _estado.update { it.copy(error = e.mensajeUsuario()) } }
        }
    }
}