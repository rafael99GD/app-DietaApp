package com.rafael.dietaapp.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Un día del diario. La fecha se guarda como String en formato "yyyy-MM-dd"
 * para que sea fácil de ordenar y consultar.
 */
@Entity(tableName = "dias")
data class Dia(
    @PrimaryKey
    val fecha: String // "2026-06-20"
)
