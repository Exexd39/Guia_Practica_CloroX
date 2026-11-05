package com.example.cloroapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cloroapp.data.Repositorio
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

val PERIODOS_HISTORIAL = listOf("24h" to "24 horas", "7d" to "7 días", "30d" to "30 días")

data class DetallePuntoUiState(
    val periodo: String = "24h",
    val historial: List<Double> = emptyList(),
    val cargando: Boolean = true,
    val error: String? = null
) {
    // Estadísticas calculadas fuera de la pantalla
    val promedio: Double get() = historial.average()
    val maximo: Double get() = historial.maxOrNull() ?: 0.0
    val minimo: Double get() = historial.minOrNull() ?: 0.0
}

class DetallePuntoViewModel : ViewModel() {
    private val _estado = MutableStateFlow(DetallePuntoUiState())
    val estado: StateFlow<DetallePuntoUiState> = _estado

    private var trabajo: Job? = null

    fun cargar(idPunto: String) {
        trabajo?.cancel() // si se cambia de período rápido, solo vale la última petición
        trabajo = viewModelScope.launch {
            _estado.update { it.copy(cargando = true, error = null) }
            Repositorio.cargarHistorial(idPunto, _estado.value.periodo)
                .onSuccess { lista -> _estado.update { it.copy(historial = lista) } }
                .onFailure { e -> _estado.update { it.copy(historial = emptyList(), error = e.message) } }
            _estado.update { it.copy(cargando = false) }
        }
    }

    fun cambiarPeriodo(idPunto: String, periodo: String) {
        _estado.update { it.copy(periodo = periodo) }
        cargar(idPunto)
    }
}