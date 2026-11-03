package com.example.cloroapp.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.cloroapp.data.model.GranjaEntity
import com.example.cloroapp.data.model.PuntoEntity
import com.example.cloroapp.data.model.PendienteEntity

// Al abs class le pasamos entidades que existen en la db
@Database(
    entities = [GranjaEntity::class, PuntoEntity::class, PendienteEntity::class],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun granjaDao(): GranjaDao
    abstract fun puntoDao(): PuntoDao
    abstract fun pendienteDao(): PendienteDao

    // Aca crearemos un patron SINGLETON (asegura que exista 1 conexion abierta)
    companion object {
        @Volatile
        private var INSTANCE : AppDatabase? = null

        fun getDatabase(context: Context) : AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "cloro_database" // este seria el nombre oculto en el celu
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }

    }

}