package com.rafael.dietaapp.data.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Línea de ingrediente dentro de una receta: "esta receta lleva X gramos de este alimento".
 */
@Entity(
    tableName = "receta_alimentos",
    foreignKeys = [
        ForeignKey(
            entity = Receta::class,
            parentColumns = ["id"],
            childColumns = ["recetaId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Alimento::class,
            parentColumns = ["id"],
            childColumns = ["alimentoId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("recetaId"), Index("alimentoId")]
)
data class RecetaAlimento(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val recetaId: Long,
    val alimentoId: Long,
    val gramos: Double
)
