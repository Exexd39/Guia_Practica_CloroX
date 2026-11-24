package com.example.cloroapp.data.clima

import com.google.gson.annotations.SerializedName
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

data class RespuestaClima(val current: ClimaActual?)

data class ClimaActual(
    @SerializedName("temperature_2m") val temperatura: Double,
    @SerializedName("relative_humidity_2m") val humedad: Double,
    @SerializedName("wind_speed_10m") val viento: Double
)

interface OpenMeteoApi {
    @GET("v1/forecast")
    suspend fun actual(
        @Query("latitude") latitud: Double,
        @Query("longitude") longitud: Double,
        @Query("current") campos: String = "temperature_2m,relative_humidity_2m,wind_speed_10m",
        @Query("wind_speed_unit") unidadViento: String = "kmh"
    ): RespuestaClima
}

object OpenMeteoClient {
    val api: OpenMeteoApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.open-meteo.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(OpenMeteoApi::class.java)
    }
}

object ClimaRepositorio {
    suspend fun obtener(latitud: Double, longitud: Double): Result<ClimaActual> =
        try {
            val actual = OpenMeteoClient.api.actual(latitud, longitud).current
            if (actual != null) Result.success(actual)
            else Result.failure(IllegalStateException("Sin datos de clima"))
        } catch (e: Exception) {
            Result.failure(e)
        }
}