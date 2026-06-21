package com.rafael.dietaapp.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Una comida dentro de un día concreto (ej: "Desayuno", "Comida de mediodía").
 * Está compuesta por alimentos (ComidaAlimento) y opcionalmente por recetas (ComidaReceta).
 */
@Entity(tableName = "comidas")
data class Comida(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val diaFecha: String,   // FK lógica hacia Dia.fecha
    val nombre: String,     // "Desayuno", "Comida", "Cena", o lo que el usuario quiera
    val horaCreacion: Long = System.currentTimeMillis()
)
