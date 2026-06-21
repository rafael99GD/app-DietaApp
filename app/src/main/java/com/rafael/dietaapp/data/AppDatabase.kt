package com.rafael.dietaapp.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.rafael.dietaapp.data.dao.AlimentoDao
import com.rafael.dietaapp.data.dao.ComidaDao
import com.rafael.dietaapp.data.dao.DiaDao
import com.rafael.dietaapp.data.dao.ExtraDao
import com.rafael.dietaapp.data.dao.RecetaDao
import com.rafael.dietaapp.data.entities.Alimento
import com.rafael.dietaapp.data.entities.Comida
import com.rafael.dietaapp.data.entities.ComidaAlimento
import com.rafael.dietaapp.data.entities.ComidaReceta
import com.rafael.dietaapp.data.entities.Dia
import com.rafael.dietaapp.data.entities.Extra
import com.rafael.dietaapp.data.entities.Receta
import com.rafael.dietaapp.data.entities.RecetaAlimento

@Database(
    entities = [
        Alimento::class,
        Dia::class,
        Comida::class,
        ComidaAlimento::class,
        ComidaReceta::class,
        Extra::class,
        Receta::class,
        RecetaAlimento::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun alimentoDao(): AlimentoDao
    abstract fun diaDao(): DiaDao
    abstract fun comidaDao(): ComidaDao
    abstract fun extraDao(): ExtraDao
    abstract fun recetaDao(): RecetaDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "dieta_app_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
