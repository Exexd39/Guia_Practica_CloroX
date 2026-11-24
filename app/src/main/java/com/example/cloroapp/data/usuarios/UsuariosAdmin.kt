package com.example.cloroapp.data.usuarios

import com.example.cloroapp.data.model.SesionActual
import com.example.cloroapp.data.network.UsuarioDto
import com.google.gson.annotations.SerializedName
import java.io.IOException
import org.json.JSONObject
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.Response
import java.util.concurrent.TimeUnit
import okhttp3.OkHttpClient

// Lo que enviamos al crear o editar (la clave puede ir null al editar)
data class UsuarioRequest(
    val nombre: String,
    val correo: String,
    val clave: String?,
    val rol: String,
    val granjasAsignadas: List<String>
)

interface UsuariosAdminApi {
    @GET("usuarios")
    suspend fun listar(@Header("X-Usuario-Id") idSolicitante: String): List<UsuarioDto>

    @POST("usuarios")
    suspend fun crear(
        @Header("X-Usuario-Id") idSolicitante: String,
        @Body datos: UsuarioRequest
    ): UsuarioDto

    @PUT("usuarios/{id}")
    suspend fun actualizar(
        @Header("X-Usuario-Id") idSolicitante: String,
        @Path("id") id: String,
        @Body datos: UsuarioRequest
    ): UsuarioDto

    @DELETE("usuarios/{id}")
    suspend fun eliminar(
        @Header("X-Usuario-Id") idSolicitante: String,
        @Path("id") id: String
    ): Response<Unit>
}

object UsuariosAdminRepositorio {
    // usuario-service corre en la PC, puerto 8081. Desde el emulador la PC es 10.0.2.2.
    // Si pruebas en un celular físico, cambia esta URL por la IP de tu PC.
    private val cliente = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .build()
    private val api: UsuariosAdminApi by lazy {
        Retrofit.Builder()
            .baseUrl("http://127.0.0.1:8081/api/")
            .client(cliente)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(UsuariosAdminApi::class.java)
    }

    private fun solicitante(): String = SesionActual.usuario?.id.orEmpty()

    suspend fun listar(): Result<List<UsuarioDto>> =
        llamar { api.listar(solicitante()) }

    suspend fun crear(datos: UsuarioRequest): Result<UsuarioDto> =
        llamar { api.crear(solicitante(), datos) }

    suspend fun actualizar(id: String, datos: UsuarioRequest): Result<UsuarioDto> =
        llamar { api.actualizar(solicitante(), id, datos) }

    suspend fun eliminar(id: String): Result<Unit> =
        llamar {
            val respuesta = api.eliminar(solicitante(), id)
            if (!respuesta.isSuccessful) throw HttpException(respuesta)
        }

    private suspend fun <T> llamar(bloque: suspend () -> T): Result<T> =
        try {
            Result.success(bloque())
        } catch (e: Exception) {
            Result.failure(e)
        }
}

// Convierte un error en un mensaje para mostrar. Usa el "detail" que manda el servidor.
fun Throwable.mensajeUsuario(): String = when (this) {
    is HttpException -> {
        val cuerpo = response()?.errorBody()?.string()
        val detalle = cuerpo?.let {
            runCatching { JSONObject(it).getString("detail") }.getOrNull()
        }
        detalle ?: "Error ${code()} del servidor"
    }
    is IOException -> "No hay conexión con usuario-service: ${message}"
    else -> message ?: "Error desconocido"
}