package com.example.cloroapp.data.network

import java.util.concurrent.TimeUnit
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object RetrofitClient {
    // Aqui ya se ocupa Render para la pagina y todo eso
    const val BASE_URL = "https://cloroapp-api.onrender.com/"

    // Cliente HTTP con paciencia de 60s para cuando Render despierta
    val cliente = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    val apiService: CloroApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(cliente)
            // Convierte el JSON de la API a objetos Kotlin
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(CloroApiService::class.java)
    }
}