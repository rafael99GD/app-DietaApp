package com.rafael.dietaapp.data.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Línea de "esta comida lleva X gramos de este alimento".
 * Una Comida puede tener varias líneas de este tipo (varios alimentos).
 */
@Entity(
    tableName = "comida_alimentos",
    foreignKeys = [
        ForeignKey(
            entity = Comida::class,
            parentColumns = ["id"],
            childColumns = ["comidaId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Alimento::class,
            parentColumns = ["id"],
            childColumns = ["alimentoId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("comidaId"), Index("alimentoId")]
)
data class ComidaAlimento(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val comidaId: Long,
    val alimentoId: Long,
    val gramos: Double
)
