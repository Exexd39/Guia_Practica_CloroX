package com.example.cloroapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

// Aqui Room va a leerlo y generara una tabla llamada tal cual esta ahi.
@Entity(tableName = "granjas_table")
data class Granja(
    @PrimaryKey
    @SerializedName("id") val id: String,
    @SerializedName("nombre") val nombre: String
)