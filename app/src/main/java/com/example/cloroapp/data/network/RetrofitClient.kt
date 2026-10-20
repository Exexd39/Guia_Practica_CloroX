package com.example.cloroapp.data.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

object RetrofitClient {
    // Esto es el tunel hacia el localhost que se emule en el PC
    private const val BASE_URL = "http://127.0.0.1:8000/"
    val apiService: CloroApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            // LO DE ABAJO ES EL PROCESO A JSON
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(CloroApiService::class.java)
    }
}