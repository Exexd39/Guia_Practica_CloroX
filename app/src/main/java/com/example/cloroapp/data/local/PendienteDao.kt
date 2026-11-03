package com.example.cloroapp.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.cloroapp.data.model.PendienteEntity

@Dao
@JvmSuppressWildcards
interface PendienteDao {
    @Insert
    suspend fun guardar(pendiente: PendienteEntity): Long

    @Query("SELECT * FROM pendientes_table ORDER BY id")
    suspend fun obtenerTodas(): List<PendienteEntity>

    @Query("DELETE FROM pendientes_table WHERE id = :id")
    suspend fun borrar(id: Long): Int
}