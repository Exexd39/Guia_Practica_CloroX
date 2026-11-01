package com.example.cloroapp.data.network

import com.example.cloroapp.data.model.GranjaEntity
import retrofit2.http.GET

interface CloroApiService {
    @GET("granjas")
    suspend fun obtenerGranjas(): List<GranjaEntity>
}