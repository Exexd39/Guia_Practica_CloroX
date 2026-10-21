package com.example.cloroapp.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.cloroapp.data.model.Granja


@Dao
@JvmSuppressWildcards
interface GranjaDao {
    // Leemos que hay y traemos todo
    @Query("SELECT * FROM granjas_table")
    suspend fun obtenerTodas(): List<Granja>

    // Aca guardamos la lista que viene de FastApi, replace actualiza datos con la misma ID
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun guardarGranjas(granja: List<Granja>): List<Long>

    // Limpiamos la bodega, esto se quitara en produccion jeje
    @Query("DELETE FROM granjas_table")
    suspend fun borrarTodas(): Int

}