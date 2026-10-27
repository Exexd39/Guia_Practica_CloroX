package com.example.cloroapp.data

import com.example.cloroapp.data.model.Rol

// Reglas del caso: el operario registra, el supervisor cierra,
// jefatura solo consulta.
object Permisos {
    fun puedeRegistrarAccion(rol: Rol): Boolean = rol != Rol.JEFATURA
    fun puedeCerrarAlerta(rol: Rol): Boolean = rol == Rol.SUPERVISOR
}