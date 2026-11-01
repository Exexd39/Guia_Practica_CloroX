package com.example.cloroapp.data

import com.example.cloroapp.data.model.Usuario
import com.example.cloroapp.data.network.LoginRequest
import com.example.cloroapp.data.network.RetrofitClient
import com.example.cloroapp.data.network.aModelo
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

}