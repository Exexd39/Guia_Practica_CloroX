package com.example.cloroapp.data.network

import com.example.cloroapp.data.model.GranjaEntity
import retrofit2.http.GET
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query


interface CloroApiService {
    // GET
    @GET("granjas")
    suspend fun obtenerGranjas(): List<GranjaEntity>

    @GET("granjas/resumen")
    suspend fun obtenerResumenGranjas(): List<GranjaDto>

    @GET("puntos/{id}/mediciones")
    suspend fun obtenerMediciones(
        @Path("id") idPunto: String,
        @Query("periodo") periodo: String
    ): List<MedicionDto>

    // POST
    @POST("login")
    suspend fun login(@Body datos: LoginRequest): UsuarioDto

}