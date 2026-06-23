package com.rafael.dietaapp.data.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Un "extra": algo que el usuario ha comido fuera (un helado, un menú del día, etc.)
 * del que no tiene los datos exactos del alimento, así que introduce una ESTIMACIÓN
 * manual de los valores totales (no por 100g, sino el total de lo que se ha comido).
 */
@Entity(
    tableName = "extras",
    foreignKeys = [
        ForeignKey(
            entity = Dia::class,
            parentColumns = ["fecha"],
            childColumns = ["diaFecha"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["diaFecha"])]
)
data class Extra(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val diaFecha: String,
    val nombre: String,
    val emoji: String = "🍴",
    val kcal: Double,
    val grasas: Double,
    val grasasSaturadas: Double,
    val hidratos: Double,
    val azucares: Double,
    val proteinas: Double,
    val sal: Double,
    val horaCreacion: Long = System.currentTimeMillis()
)
