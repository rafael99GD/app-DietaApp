package com.rafael.dietaapp.data.entities

import androidx.room.Embedded
import androidx.room.Relation

/**
 * Clase de ayuda para Room que resuelve la relación entre una línea de comida y su alimento.
 */
data class ComidaAlimentoRelacion(
    @Embedded val linea: ComidaAlimento,
    @Relation(
        parentColumn = "alimentoId",
        entityColumn = "id"
    )
    val alimento: Alimento?
)

/**
 * Clase de ayuda para Room que resuelve la relación entre una línea de comida y su receta.
 */
data class ComidaRecetaRelacion(
    @Embedded val linea: ComidaReceta,
    @Relation(
        parentColumn = "recetaId",
        entityColumn = "id"
    )
    val receta: Receta?
)

/**
 * Clase de ayuda para Room que resuelve la relación entre una receta y sus ingredientes.
 */
data class RecetaAlimentoRelacion(
    @Embedded val linea: RecetaAlimento,
    @Relation(
        parentColumn = "alimentoId",
        entityColumn = "id"
    )
    val alimento: Alimento?
)
