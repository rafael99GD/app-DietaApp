package com.rafael.dietaapp

import android.app.Application
import com.rafael.dietaapp.data.AppDatabase
import com.rafael.dietaapp.data.repository.DietaRepository
import com.rafael.dietaapp.util.UserPreferences

class DietaApplication : Application() {

    private val database by lazy { AppDatabase.getInstance(this) }

    val userPreferences by lazy { UserPreferences(this) }

    val repository by lazy {
        DietaRepository(
            alimentoDao = database.alimentoDao(),
            diaDao = database.diaDao(),
            comidaDao = database.comidaDao(),
            extraDao = database.extraDao(),
            recetaDao = database.recetaDao()
        )
    }
}
