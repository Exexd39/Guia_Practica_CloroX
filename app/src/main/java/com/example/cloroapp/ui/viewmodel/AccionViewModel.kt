package com.example.cloroapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cloroapp.data.Repositorio
import com.example.cloroapp.data.model.RegistroAccion
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

val TIPOS_ACCION = listOf(
    "Ajuste de dosificación",
    "Revisión de equipo",
    "Limpieza de red",
    "Otra"
)

data class AccionUiState(
    val tipo: String = TIPOS_ACCION.first(),
    val observaciones: String = "",
    val errorObservaciones: String? = null,
    val errorGeneral: String? = null,
    val guardado: Boolean = false,
    val enviando: Boolean = false,
    val historial: List<RegistroAccion> = emptyList()
)

class AccionViewModel : ViewModel() {
    private val _estado = MutableStateFlow(AccionUiState())
    val estado: StateFlow<AccionUiState> = _estado

    fun onTipoChange(tipo: String) {
        _estado.update { it.copy(tipo = tipo) }
    }

    fun onObservacionesChange(valor: String) {
        _estado.update {
            it.copy(observaciones = valor, errorObservaciones = null, errorGeneral = null, guardado = false)
        }
    }

    fun cargarHistorial(idPunto: String) {
        viewModelScope.launch {
            Repositorio.cargarAcciones(idPunto)
                .onSuccess { lista -> _estado.update { it.copy(historial = lista) } }
        }
    }

    fun guardar(idPunto: String) {
        val actual = _estado.value
        val error = Validaciones.observaciones(actual.observaciones)
        if (error != null) {
            _estado.update { it.copy(errorObservaciones = error, guardado = false) }
            return
        }
        viewModelScope.launch {
            _estado.update { it.copy(enviando = true) }
            Repositorio.registrarAccion(idPunto, actual.tipo, actual.observaciones.trim())
                .onSuccess {
                    _estado.update { it.copy(observaciones = "", guardado = true, errorGeneral = null) }
                    cargarHistorial(idPunto)
                }
                .onFailure { e ->
                    _estado.update { it.copy(errorGeneral = e.message, guardado = false) }
                }
            _estado.update { it.copy(enviando = false) }
        }
    }
}