package com.rafael.dietaapp.data.dao

import androidx.room.*
import com.rafael.dietaapp.data.entities.Dia
import kotlinx.coroutines.flow.Flow

@Dao
interface DiaDao {
    @Query("SELECT * FROM dias ORDER BY fecha DESC")
    fun obtenerTodos(): Flow<List<Dia>>

    @Query("SELECT * FROM dias WHERE fecha = :fecha")
    suspend fun obtenerPorFecha(fecha: String): Dia?

    @Query("SELECT * FROM dias WHERE fecha = :fecha")
    fun obtenerPorFechaFlow(fecha: String): Flow<Dia?>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertar(dia: Dia)

    @Update
    suspend fun actualizar(dia: Dia)

    @Delete
    suspend fun eliminar(dia: Dia)

    /** Elimina días que no tienen ni comidas ni extras para ahorrar espacio, 
     * aunque en SQLite esto es casi despreciable. */
    @Query("""
        DELETE FROM dias 
        WHERE fecha NOT IN (SELECT DISTINCT diaFecha FROM comidas)
          AND fecha NOT IN (SELECT DISTINCT diaFecha FROM extras)
          AND agua = 0
    """)
    suspend fun limpiarDiasVacios()
}
