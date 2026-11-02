package com.example.cloroapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "puntos_table")
data class PuntoEntity(
    @PrimaryKey val id: String,
    val idGranja: String,
    val nombreGranja: String,
    val codigo: String,
    val nombre: String,
    val tipo: String,
    val ubicacion: String,
    val cloroLibre: Double,
    val fecha: String,
    val hora: String
)