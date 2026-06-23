package com.rafael.dietaapp.data.dao

import androidx.room.*
import com.rafael.dietaapp.data.entities.Comida
import com.rafael.dietaapp.data.entities.ComidaAlimento
import com.rafael.dietaapp.data.entities.ComidaReceta
import kotlinx.coroutines.flow.Flow

@Dao
interface ComidaDao {
    @Query("SELECT * FROM comidas WHERE diaFecha = :fecha ORDER BY horaCreacion ASC")
    fun obtenerComidasDelDia(fecha: String): Flow<List<Comida>>

    @Query("SELECT * FROM comidas WHERE id = :id")
    suspend fun obtenerPorId(id: Long): Comida?

    @Insert
    suspend fun insertarComida(comida: Comida): Long

    @Update
    suspend fun actualizarComida(comida: Comida)

    @Delete
    suspend fun eliminarComida(comida: Comida)

    // --- Líneas de alimentos sueltos dentro de una comida ---

    @Transaction
    @Query("SELECT * FROM comida_alimentos WHERE comidaId = :comidaId")
    fun obtenerLineasAlimentoConInfo(comidaId: Long): Flow<List<com.rafael.dietaapp.data.entities.ComidaAlimentoRelacion>>

    @Transaction
    @Query("SELECT * FROM comida_alimentos WHERE comidaId = :comidaId")
    suspend fun obtenerLineasAlimentoConInfoSync(comidaId: Long): List<com.rafael.dietaapp.data.entities.ComidaAlimentoRelacion>

    @Query("SELECT * FROM comida_alimentos WHERE comidaId = :comidaId")
    fun obtenerLineasAlimento(comidaId: Long): Flow<List<ComidaAlimento>>

    @Query("SELECT * FROM comida_alimentos WHERE comidaId = :comidaId")
    suspend fun obtenerLineasAlimentoSync(comidaId: Long): List<ComidaAlimento>

    @Insert
    suspend fun insertarLineaAlimento(linea: ComidaAlimento): Long

    @Update
    suspend fun actualizarLineaAlimento(linea: ComidaAlimento)

    @Delete
    suspend fun eliminarLineaAlimento(linea: ComidaAlimento)

    // --- Líneas de recetas dentro de una comida ---

    @Transaction
    @Query("SELECT * FROM comida_recetas WHERE comidaId = :comidaId")
    fun obtenerLineasRecetaConInfo(comidaId: Long): Flow<List<com.rafael.dietaapp.data.entities.ComidaRecetaRelacion>>

    @Transaction
    @Query("SELECT * FROM comida_recetas WHERE comidaId = :comidaId")
    suspend fun obtenerLineasRecetaConInfoSync(comidaId: Long): List<com.rafael.dietaapp.data.entities.ComidaRecetaRelacion>

    @Query("SELECT * FROM comida_recetas WHERE comidaId = :comidaId")
    fun obtenerLineasReceta(comidaId: Long): Flow<List<ComidaReceta>>

    @Query("SELECT * FROM comida_recetas WHERE comidaId = :comidaId")
    suspend fun obtenerLineasRecetaSync(comidaId: Long): List<ComidaReceta>

    @Insert
    suspend fun insertarLineaReceta(linea: ComidaReceta): Long

    @Update
    suspend fun actualizarLineaReceta(linea: ComidaReceta)

    @Delete
    suspend fun eliminarLineaReceta(linea: ComidaReceta)
}
