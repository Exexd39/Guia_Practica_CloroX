package com.example.cloroapp.ui.viewmodel

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cloroapp.data.Evidencias
import com.example.cloroapp.data.Repositorio
import com.example.cloroapp.data.model.RegistroAccion
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException

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
    val historial: List<RegistroAccion> = emptyList(),
    val evidencias: List<File> = emptyList(),
    val mensajeEvidencia: String? = null
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

    // ---------- Evidencia fotografica (recurso nativo) ----------

    fun cargarEvidencias(contexto: Context, idPunto: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val lista = Evidencias.listar(contexto, idPunto)
            _estado.update { it.copy(evidencias = lista) }
        }
    }

    fun guardarEvidencia(contexto: Context, idPunto: String, bitmap: Bitmap) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                Evidencias.guardar(contexto, idPunto, bitmap)
                _estado.update { it.copy(mensajeEvidencia = "✓ Foto guardada como evidencia") }
            } catch (e: IOException) {
                _estado.update { it.copy(mensajeEvidencia = "No se pudo guardar la foto") }
            }
            cargarEvidencias(contexto, idPunto)
        }
    }

    fun guardarEvidenciaDesdeUri(contexto: Context, idPunto: String, uri: Uri) {
        viewModelScope.launch(Dispatchers.IO) {
            val bitmap = Evidencias.desdeUri(contexto, uri)
            if (bitmap == null) {
                _estado.update { it.copy(mensajeEvidencia = "No se pudo leer la imagen seleccionada") }
            } else {
                guardarEvidencia(contexto, idPunto, bitmap)
            }
        }
    }

    fun eliminarEvidencia(contexto: Context, idPunto: String, archivo: File) {
        viewModelScope.launch(Dispatchers.IO) {
            Evidencias.eliminar(archivo)
            cargarEvidencias(contexto, idPunto)
        }
    }

    fun avisarEvidencia(mensaje: String) {
        _estado.update { it.copy(mensajeEvidencia = mensaje) }
    }
}