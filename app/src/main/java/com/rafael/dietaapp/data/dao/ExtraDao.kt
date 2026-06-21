package com.rafael.dietaapp.data.dao

import androidx.room.*
import com.rafael.dietaapp.data.entities.Extra
import kotlinx.coroutines.flow.Flow

@Dao
interface ExtraDao {
    @Query("SELECT * FROM extras WHERE diaFecha = :fecha ORDER BY horaCreacion ASC")
    fun obtenerExtrasDelDia(fecha: String): Flow<List<Extra>>

    @Insert
    suspend fun insertar(extra: Extra): Long

    @Update
    suspend fun actualizar(extra: Extra)

    @Delete
    suspend fun eliminar(extra: Extra)
}
