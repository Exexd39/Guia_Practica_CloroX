package com.example.cloroapp.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cloroapp.data.EstadoRed
import com.example.cloroapp.data.Repositorio
import com.example.cloroapp.data.Usuarios
import com.example.cloroapp.data.model.Granja
import com.example.cloroapp.data.model.SesionActual
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class DashboardViewModel : ViewModel() {

    // Solo las granjas que el usuario con sesion puede ver
    val granjas: StateFlow<List<Granja>> = Repositorio.granjas
        .map { Usuarios.granjasVisibles(SesionActual.usuario, it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val estadoRed: StateFlow<EstadoRed> = Repositorio.estado
    val pendientes: StateFlow<Int> = Repositorio.pendientes

    fun cargar(contexto: Context) {
        viewModelScope.launch {
            Repositorio.cargarGranjas()
            Repositorio.revisarAlertasCriticas(contexto)
        }
    }

    fun reintentar() {
        viewModelScope.launch { Repositorio.cargarGranjas() }
    }

    fun cerrarSesion() {
        SesionActual.usuario = null
        Repositorio.cerrarSesion()
    }
}