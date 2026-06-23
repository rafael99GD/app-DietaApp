package com.rafael.dietaapp.data.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "comidas",
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
data class Comida(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val diaFecha: String,   // FK hacia Dia.fecha
    val nombre: String,     // "Desayuno", "Comida", "Cena", o lo que el usuario quiera
    val horaCreacion: Long = System.currentTimeMillis()
)
