package com.example.cloroapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cloroapp.data.Repositorio
import com.example.cloroapp.data.ResultadoEnvio
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class MedicionUiState(
    val texto: String = "",
    val error: String? = null,
    val resultado: ResultadoEnvio? = null,
    val enviando: Boolean = false
)

class MedicionViewModel : ViewModel() {
    private val _estado = MutableStateFlow(MedicionUiState())
    val estado: StateFlow<MedicionUiState> = _estado

    fun onTextoChange(valor: String) {
        _estado.update { it.copy(texto = valor, error = null, resultado = null) }
    }

    fun guardar(idPunto: String) {
        val error = Validaciones.cloro(_estado.value.texto)
        if (error != null) {
            _estado.update { it.copy(error = error) }
            return
        }
        val valor = _estado.value.texto.replace(',', '.').toDouble()
        viewModelScope.launch {
            _estado.update { it.copy(enviando = true) }
            Repositorio.registrarMedicion(idPunto, valor)
                .onSuccess { r -> _estado.update { it.copy(texto = "", resultado = r) } }
                .onFailure { e -> _estado.update { it.copy(error = e.message) } }
            _estado.update { it.copy(enviando = false) }
        }
    }
}