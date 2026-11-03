package com.example.cloroapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pendientes_table")
data class PendienteEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val idPunto: String,
    val nivelCloro: Double,
    val fechaHora: String
)