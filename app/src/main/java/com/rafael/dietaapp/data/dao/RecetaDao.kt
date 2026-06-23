package com.rafael.dietaapp.data.dao

import androidx.room.*
import com.rafael.dietaapp.data.entities.Receta
import com.rafael.dietaapp.data.entities.RecetaAlimento
import kotlinx.coroutines.flow.Flow

@Dao
interface RecetaDao {
    @Query("SELECT * FROM recetas ORDER BY nombre ASC")
    fun obtenerTodas(): Flow<List<Receta>>

    @Query("SELECT * FROM recetas ORDER BY nombre ASC")
    suspend fun obtenerTodasSync(): List<Receta>

    @Query("SELECT * FROM recetas WHERE id = :id")
    suspend fun obtenerPorId(id: Long): Receta?

    @Insert
    suspend fun insertar(receta: Receta): Long

    @Update
    suspend fun actualizar(receta: Receta)

    @Delete
    suspend fun eliminar(receta: Receta)

    // --- Ingredientes de la receta ---

    @Transaction
    @Query("SELECT * FROM receta_alimentos WHERE recetaId = :recetaId")
    fun obtenerIngredientesConInfo(recetaId: Long): Flow<List<com.rafael.dietaapp.data.entities.RecetaAlimentoRelacion>>

    @Transaction
    @Query("SELECT * FROM receta_alimentos WHERE recetaId = :recetaId")
    suspend fun obtenerIngredientesConInfoSync(recetaId: Long): List<com.rafael.dietaapp.data.entities.RecetaAlimentoRelacion>

    @Query("SELECT * FROM receta_alimentos WHERE recetaId = :recetaId")
    fun obtenerIngredientes(recetaId: Long): Flow<List<RecetaAlimento>>

    @Query("SELECT * FROM receta_alimentos WHERE recetaId = :recetaId")
    suspend fun obtenerIngredientesSync(recetaId: Long): List<RecetaAlimento>

    @Insert
    suspend fun insertarIngrediente(ingrediente: RecetaAlimento): Long

    @Delete
    suspend fun eliminarIngrediente(ingrediente: RecetaAlimento)

    @Query("DELETE FROM receta_alimentos WHERE recetaId = :recetaId")
    suspend fun eliminarTodosLosIngredientes(recetaId: Long)
}
