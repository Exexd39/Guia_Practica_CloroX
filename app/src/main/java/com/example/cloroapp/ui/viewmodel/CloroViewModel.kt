package com.example.cloroapp.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cloroapp.data.local.GranjaDao
import com.example.cloroapp.data.model.Granja
import com.example.cloroapp.data.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


class CloroViewModel(private val dao: GranjaDao): ViewModel(){
    // Aqui se guardan las listas de granjas que nos llegara de internet
    private val _granjas = MutableStateFlow<List<Granja>>(emptyList())
    val granjas: StateFlow<List<Granja>> = _granjas

    // Se instancia el viewmodel y se llama a la funcion de descarga (esta abajo)
    init {
        descargarGranjas()
    }

    private fun descargarGranjas(){
        // Creamos un tunel paralelo para no trabar la UI
        viewModelScope.launch {
            // Con esto mostraremos de inmediato lo LOCAL
            val datosLocales = dao.obtenerTodas()
            _granjas.value = datosLocales

            // Aca sera la busqueda de datos nuevos
            try {
                val listaDesdePython = RetrofitClient.apiService.obtenerGranjas()
                dao.guardarGranjas(listaDesdePython)
                // Guardamos datos para que los lea la pantalla
                _granjas.value = listaDesdePython
                Log.d("API_REST", "Exito, las granjas descargadas son: ${listaDesdePython}")
            } catch (e: Exception) {
                Log.e("API_TEST", "Error al conectar con FastAPI / Despertando Render: ${e.message}")
            }
        }
    }
}