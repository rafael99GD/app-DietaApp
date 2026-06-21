package com.rafael.dietaapp.data.dao

import androidx.room.*
import com.rafael.dietaapp.data.entities.Alimento
import kotlinx.coroutines.flow.Flow

@Dao
interface AlimentoDao {
    @Query("SELECT * FROM alimentos ORDER BY nombre ASC")
    fun obtenerTodos(): Flow<List<Alimento>>

    @Query("SELECT * FROM alimentos ORDER BY nombre ASC")
    suspend fun obtenerTodosSync(): List<Alimento>

    @Query("SELECT * FROM alimentos WHERE id = :id")
    suspend fun obtenerPorId(id: Long): Alimento?

    @Query("SELECT * FROM alimentos WHERE nombre LIKE '%' || :query || '%' ORDER BY nombre ASC")
    fun buscar(query: String): Flow<List<Alimento>>

    @Insert
    suspend fun insertar(alimento: Alimento): Long

    @Update
    suspend fun actualizar(alimento: Alimento)

    @Delete
    suspend fun eliminar(alimento: Alimento)
}
