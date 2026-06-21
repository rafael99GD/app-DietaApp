package com.rafael.dietaapp.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Una receta guardada por el usuario: nombre, foto, y los totales calculados
 * (se guardan "cacheados" para no tener que recalcular cada vez, aunque también
 * podrían derivarse de RecetaAlimento + Alimento).
 */
@Entity(tableName = "recetas")
data class Receta(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val nombre: String,
    val fotoUri: String? = null,
    val emoji: String = "📖",
    val fechaCreacion: Long = System.currentTimeMillis()
)
