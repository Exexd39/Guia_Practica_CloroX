package com.example.cloroapp.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.cloroapp.data.model.PuntoEntity

@Dao
@JvmSuppressWildcards
interface PuntoDao {
    @Query("SELECT * FROM puntos_table")
    suspend fun obtenerTodos(): List<PuntoEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun guardar(puntos: List<PuntoEntity>): List<Long>
}