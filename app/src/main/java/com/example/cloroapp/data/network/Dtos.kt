package com.example.cloroapp.data.network

import com.example.cloroapp.data.model.Rol
import com.example.cloroapp.data.model.Usuario


// Esto lo mandamos al servidor
data class LoginRequest(
    val correo: String,
    val clave: String
)

// Se recibe sin clave
data class UsuarioDto(
    val id: String,
    val nombre: String,
    val correo: String,
    val rol: String,
    val granjasAsignadas: List<String>
)

// Convierte el DTO al model que usamos en pantalla
fun UsuarioDto.aModelo() = Usuario(
    id = id,
    nombre = nombre,
    correo = correo,
    clave = "",
    rol = Rol.valueOf(rol),
    granjasAsignadas = granjasAsignadas
)