package com.example.cloroapp.data.network

import com.example.cloroapp.data.model.GranjaEntity
import retrofit2.http.GET
import retrofit2.http.Body
import retrofit2.http.POST

interface CloroApiService {
    // GET
    @GET("granjas")
    suspend fun obtenerGranjas(): List<GranjaEntity>

    // POST
    @POST("login")
    suspend fun login(@Body datos: LoginRequest): UsuarioDto

}