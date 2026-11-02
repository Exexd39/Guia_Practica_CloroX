package com.example.cloroapp.data

import com.example.cloroapp.data.model.Granja
import com.example.cloroapp.data.model.PuntoMedicion
import com.example.cloroapp.data.model.Usuario
import com.example.cloroapp.data.network.LoginRequest
import com.example.cloroapp.data.network.RetrofitClient
import com.example.cloroapp.data.network.aModelo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import retrofit2.HttpException
import java.io.IOException


object Repositorio {
    suspend fun login(correo: String, clave: String): Result<Usuario>{
        return try {
            val dto = RetrofitClient.apiService.login(LoginRequest(correo.trim(), clave))
            Result.success(dto.aModelo())
        } catch (e: HttpException) {
            Result.failure(Exception("Correo o contraseña incorrectos"))
        } catch (e: IOException) {
            Result.failure(Exception("Sin conexion con el servidor"))
        }
    }

    private val _granjas = MutableStateFlow<List<Granja>>(emptyList())
    val granjas: StateFlow<List<Granja>> = _granjas

    suspend fun cargarGranjas(): Result<Unit> {
        return try {
            _granjas.value = RetrofitClient.apiService
                .obtenerResumenGranjas()
                .map { it.aModelo() }
            Result.success(Unit)
        } catch (e: HttpException) {
            Result.failure(Exception("El servidor respondio con error (${e.code()})"))
        } catch (e: IOException) {
            Result.failure(Exception("Sin conexion con el servidor"))
        }
    }
    fun buscarPunto(id: String, todas: List<Granja>): Pair<Granja, PuntoMedicion>? {
        for (granja in todas) {
            val punto = granja.puntos.find { it.id == id }
            if (punto != null)
                return granja to punto
            }
        return null
    }
}
