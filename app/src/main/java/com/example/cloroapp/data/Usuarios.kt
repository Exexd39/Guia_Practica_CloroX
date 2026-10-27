package com.example.cloroapp.data

import com.example.cloroapp.data.model.Granja
import com.example.cloroapp.data.model.Rol
import com.example.cloroapp.data.model.Usuario

// Usuarios ficticios para la demo. En un proyecto real la clave
// nunca va en el código: se valida en un servidor y con hash.
object Usuarios {
    val lista = listOf(
        Usuario("u1", "Ana Pérez", "operario@cloro.cl", "1234", Rol.OPERARIO, listOf("g1")),
        Usuario("u2", "Luis Soto", "supervisor@cloro.cl", "1234", Rol.SUPERVISOR, listOf("g1", "g2")),
        Usuario("u3", "Carla Mora", "jefatura@cloro.cl", "1234", Rol.JEFATURA, listOf("g1", "g2", "g3"))
    )

    fun autenticar(correo: String, clave: String): Usuario? =
        lista.find {
            it.correo.equals(correo.trim(), ignoreCase = true) && it.clave == clave
        }

    fun granjasVisibles(usuario: Usuario?): List<Granja> {
        if (usuario == null) return emptyList()
        return DatosFalsos.granjas.filter { it.id in usuario.granjasAsignadas }
    }
}