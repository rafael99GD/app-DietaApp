package com.rafael.dietaapp.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Un alimento guardado en la biblioteca del usuario.
 * Todos los valores nutricionales se guardan POR CADA 100g del alimento,
 * tal y como aparece en la tabla nutricional de cualquier producto.
 */
@Entity(tableName = "alimentos")
data class Alimento(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val nombre: String,
    val emoji: String = "🍽️",       // emoticono elegido por el usuario
    val imagenUri: String? = null,   // si prefiere foto en vez de emoji
    val kcal: Double,
    val grasas: Double,
    val grasasSaturadas: Double,
    val hidratos: Double,
    val azucares: Double,
    val proteinas: Double,
    val sal: Double
)
