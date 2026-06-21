package com.rafael.dietaapp.data.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Cuando el usuario añade una RECETA COMPLETA dentro de una comida
 * (en vez de ir añadiendo alimento por alimento).
 * Lleva un "factor" porque el usuario podría comer solo media receta, o el doble, etc.
 * factor = 1.0 significa "la receta completa, tal cual se guardó".
 */
@Entity(
    tableName = "comida_recetas",
    foreignKeys = [
        ForeignKey(
            entity = Comida::class,
            parentColumns = ["id"],
            childColumns = ["comidaId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Receta::class,
            parentColumns = ["id"],
            childColumns = ["recetaId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("comidaId"), Index("recetaId")]
)
data class ComidaReceta(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val comidaId: Long,
    val recetaId: Long,
    val factor: Double = 1.0
)
