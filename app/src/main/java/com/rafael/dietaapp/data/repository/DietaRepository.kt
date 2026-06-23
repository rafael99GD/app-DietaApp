package com.rafael.dietaapp.data.repository

import com.rafael.dietaapp.data.dao.AlimentoDao
import com.rafael.dietaapp.data.dao.ComidaDao
import com.rafael.dietaapp.data.dao.DiaDao
import com.rafael.dietaapp.data.dao.ExtraDao
import com.rafael.dietaapp.data.dao.RecetaDao
import com.rafael.dietaapp.data.entities.Alimento
import com.rafael.dietaapp.data.entities.Comida
import com.rafael.dietaapp.data.entities.ComidaAlimento
import com.rafael.dietaapp.data.entities.ComidaReceta
import com.rafael.dietaapp.data.entities.Dia
import com.rafael.dietaapp.data.entities.Extra
import com.rafael.dietaapp.data.entities.Receta
import com.rafael.dietaapp.data.entities.RecetaAlimento
import com.rafael.dietaapp.data.model.ComidaDetallada
import com.rafael.dietaapp.data.model.DiaDetallado
import com.rafael.dietaapp.data.model.LineaAlimento
import com.rafael.dietaapp.data.model.LineaReceta
import com.rafael.dietaapp.data.model.NutrientesTotales
import com.rafael.dietaapp.data.model.RecetaDetallada
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map

class DietaRepository(
    private val alimentoDao: AlimentoDao,
    private val diaDao: DiaDao,
    private val comidaDao: ComidaDao,
    private val extraDao: ExtraDao,
    private val recetaDao: RecetaDao
) {

    // ---------- ALIMENTOS ----------

    fun obtenerAlimentos(): Flow<List<Alimento>> = alimentoDao.obtenerTodos()

    suspend fun obtenerAlimentosUnaVez(): List<Alimento> = alimentoDao.obtenerTodosSync()

    fun buscarAlimentos(query: String): Flow<List<Alimento>> = alimentoDao.buscar(query)

    suspend fun obtenerAlimento(id: Long): Alimento? = alimentoDao.obtenerPorId(id)

    suspend fun guardarAlimento(alimento: Alimento): Long = alimentoDao.insertar(alimento)

    suspend fun actualizarAlimento(alimento: Alimento) = alimentoDao.actualizar(alimento)

    suspend fun eliminarAlimento(alimento: Alimento) = alimentoDao.eliminar(alimento)

    // ---------- DÍAS ----------

    /** Se asegura de que exista una fila Dia para esa fecha (la crea si no existe). */
    suspend fun asegurarDia(fecha: String) {
        if (diaDao.obtenerPorFecha(fecha) == null) {
            diaDao.insertar(Dia(fecha))
        }
    }

    fun obtenerDiasConDatos(): Flow<List<Dia>> = diaDao.obtenerTodos()

    fun obtenerDia(fecha: String): Flow<Dia?> = kotlinx.coroutines.flow.flow {
        emit(diaDao.obtenerPorFecha(fecha))
    }

    suspend fun actualizarDia(dia: Dia) = diaDao.actualizar(dia)

    suspend fun limpiarDatosHuerfanos() {
        diaDao.limpiarDiasVacios()
    }

    /** Conjunto de fechas (yyyy-MM-dd) que tienen al menos un dato registrado, para marcar el calendario. */
    fun obtenerFechasConDatos(): Flow<Set<String>> =
        diaDao.obtenerTodos().map { dias -> dias.map { it.fecha }.toSet() }

    /** Devuelve el día completo: comidas (con sus líneas resueltas) + extras, como Flow reactivo. */
    @OptIn(ExperimentalCoroutinesApi::class)
    fun obtenerDiaDetallado(fecha: String): Flow<DiaDetallado> {
        val diaFlow = diaDao.obtenerPorFechaFlow(fecha).map { it ?: Dia(fecha) }
        val comidasFlow = comidaDao.obtenerComidasDelDia(fecha)
        val extrasFlow = extraDao.obtenerExtrasDelDia(fecha)

        val comidasDetalladasFlow = comidasFlow.flatMapLatest { comidas ->
            if (comidas.isEmpty()) {
                kotlinx.coroutines.flow.flowOf<List<ComidaDetallada>>(emptyList())
            } else {
                val flows = comidas.map { comida -> obtenerComidaDetallada(comida) }
                combine(flows) { it.toList() }
            }
        }

        return combine(comidasDetalladasFlow, extrasFlow, diaFlow) { comidasDetalladas, extras, dia ->
            DiaDetallado(
                fecha = fecha,
                comidas = comidasDetalladas,
                extras = extras,
                agua = dia.agua
            )
        }
    }

    private fun obtenerComidaDetallada(comida: Comida): Flow<ComidaDetallada> {
        val lineasAlimentoFlow = comidaDao.obtenerLineasAlimentoConInfo(comida.id)
        val lineasRecetaFlow = comidaDao.obtenerLineasRecetaConInfo(comida.id)

        return combine(lineasAlimentoFlow, lineasRecetaFlow) { lineasAl, lineasRec ->
            val lineasAlimentos = lineasAl.mapNotNull { relacion ->
                relacion.alimento?.let { LineaAlimento(relacion.linea.id, it, relacion.linea.gramos) }
            }
            val lineasRecetas = lineasRec.mapNotNull { relacion ->
                relacion.receta?.let {
                    // Aquí seguimos necesitando calcular los totales de la receta.
                    // Podríamos optimizarlo más si la receta guardara sus totales, 
                    // pero por ahora el cálculo es rápido.
                    val totalesBase = calcularTotalesReceta(it.id)
                    LineaReceta(relacion.linea.id, it, relacion.linea.factor, totalesBase)
                }
            }
            ComidaDetallada(comida, lineasAlimentos, lineasRecetas)
        }
    }

    // ---------- COMIDAS ----------

    suspend fun crearComida(fecha: String, nombre: String): Long {
        asegurarDia(fecha)
        return comidaDao.insertarComida(Comida(diaFecha = fecha, nombre = nombre))
    }

    suspend fun renombrarComida(comida: Comida, nuevoNombre: String) {
        comidaDao.actualizarComida(comida.copy(nombre = nuevoNombre))
    }

    suspend fun eliminarComida(comida: Comida) = comidaDao.eliminarComida(comida)

    suspend fun agregarAlimentoAComida(comidaId: Long, alimentoId: Long, gramos: Double) {
        comidaDao.insertarLineaAlimento(ComidaAlimento(comidaId = comidaId, alimentoId = alimentoId, gramos = gramos))
    }

    suspend fun actualizarLineaAlimento(linea: ComidaAlimento) = comidaDao.actualizarLineaAlimento(linea)

    suspend fun eliminarLineaAlimento(linea: ComidaAlimento) = comidaDao.eliminarLineaAlimento(linea)

    suspend fun agregarRecetaAComida(comidaId: Long, recetaId: Long, factor: Double = 1.0) {
        comidaDao.insertarLineaReceta(ComidaReceta(comidaId = comidaId, recetaId = recetaId, factor = factor))
    }

    suspend fun eliminarLineaReceta(linea: ComidaReceta) = comidaDao.eliminarLineaReceta(linea)

    /** Obtiene una comida ya resuelta una sola vez (no como Flow), útil para "guardar como receta". */
    suspend fun obtenerComidaDetalladaUnaVez(comidaId: Long): ComidaDetallada? {
        val comida = comidaDao.obtenerPorId(comidaId) ?: return null
        val lineasAl = comidaDao.obtenerLineasAlimentoConInfoSync(comidaId)
        val lineasRec = comidaDao.obtenerLineasRecetaConInfoSync(comidaId)

        val lineasAlimentos = lineasAl.mapNotNull { rel ->
            rel.alimento?.let { LineaAlimento(rel.linea.id, it, rel.linea.gramos) }
        }
        val lineasRecetas = lineasRec.mapNotNull { rel ->
            rel.receta?.let {
                LineaReceta(rel.linea.id, it, rel.linea.factor, calcularTotalesReceta(it.id))
            }
        }
        return ComidaDetallada(comida, lineasAlimentos, lineasRecetas)
    }

    // ---------- EXTRAS ----------

    suspend fun agregarExtra(extra: Extra) {
        asegurarDia(extra.diaFecha)
        extraDao.insertar(extra)
    }

    suspend fun actualizarExtra(extra: Extra) = extraDao.actualizar(extra)

    suspend fun eliminarExtra(extra: Extra) = extraDao.eliminar(extra)

    // ---------- RECETAS ----------

    fun obtenerRecetas(): Flow<List<Receta>> = recetaDao.obtenerTodas()

    /** Devuelve la lista de recetas junto con sus kcal totales calculadas. */
    @OptIn(ExperimentalCoroutinesApi::class)
    fun obtenerRecetasConDetalle(): Flow<List<RecetaDetallada>> {
        return recetaDao.obtenerTodas().flatMapLatest { recetas ->
            if (recetas.isEmpty()) {
                kotlinx.coroutines.flow.flowOf(emptyList())
            } else {
                val flows = recetas.map { receta ->
                    obtenerRecetaDetallada(receta.id)
                }
                combine(flows) { it.toList() }
            }
        }
    }

    suspend fun obtenerReceta(id: Long): Receta? = recetaDao.obtenerPorId(id)

    suspend fun obtenerTodasLasRecetasDetalladasUnaVez(): List<RecetaDetallada> {
        val recetas = recetaDao.obtenerTodasSync()
        return recetas.map { receta ->
            val ingredientes = recetaDao.obtenerIngredientesConInfoSync(receta.id)
            val lineas = ingredientes.mapNotNull { rel ->
                rel.alimento?.let { LineaAlimento(rel.linea.id, it, rel.linea.gramos) }
            }
            RecetaDetallada(receta, lineas)
        }
    }

    fun obtenerRecetaDetallada(recetaId: Long): Flow<RecetaDetallada> {
        return recetaDao.obtenerIngredientesConInfo(recetaId).map { ingredientes ->
            val receta = recetaDao.obtenerPorId(recetaId) ?: Receta(id = recetaId, nombre = "")
            val lineas = ingredientes.mapNotNull { rel ->
                rel.alimento?.let { LineaAlimento(rel.linea.id, it, rel.linea.gramos) }
            }
            RecetaDetallada(receta, lineas)
        }
    }

    /** Calcula los totales nutricionales de una receta completa (factor 1.0), de una sola vez. */
    suspend fun calcularTotalesReceta(recetaId: Long): NutrientesTotales {
        val ingredientes = recetaDao.obtenerIngredientesConInfoSync(recetaId)
        return ingredientes.fold(NutrientesTotales()) { acc, rel ->
            val alimento = rel.alimento ?: return@fold acc
            acc + NutrientesTotales.deAlimento(
                alimento.kcal, alimento.grasas, alimento.grasasSaturadas,
                alimento.hidratos, alimento.azucares, alimento.proteinas, alimento.sal,
                rel.linea.gramos
            )
        }
    }

    /**
     * Crea una receta nueva a partir de los ingredientes indicados (gramos por alimento).
     * Se usa tanto desde "guardar comida como receta" como desde la creación manual de recetas.
     */
    suspend fun crearReceta(nombre: String, fotoUri: String?, emoji: String, notas: String = "", ingredientes: List<Pair<Long, Double>>): Long {
        val recetaId = recetaDao.insertar(Receta(nombre = nombre, fotoUri = fotoUri, emoji = emoji, notas = notas))
        ingredientes.forEach { (alimentoId, gramos) ->
            recetaDao.insertarIngrediente(RecetaAlimento(recetaId = recetaId, alimentoId = alimentoId, gramos = gramos))
        }
        return recetaId
    }

    suspend fun eliminarReceta(receta: Receta) = recetaDao.eliminar(receta)

    suspend fun actualizarRecetaCompleta(id: Long, nombre: String, fotoUri: String?, emoji: String, notas: String, ingredientes: List<Pair<Long, Double>>) {
        recetaDao.actualizar(Receta(id = id, nombre = nombre, fotoUri = fotoUri, emoji = emoji, notas = notas))
        recetaDao.eliminarTodosLosIngredientes(id)
        ingredientes.forEach { (alimentoId, gramos) ->
            recetaDao.insertarIngrediente(RecetaAlimento(recetaId = id, alimentoId = alimentoId, gramos = gramos))
        }
    }

    /**
     * Genera una receta a partir de una comida ya existente: copia todas sus líneas de alimento
     * sueltas Y "aplana" las recetas que tuviera dentro (las convierte en ingredientes directos
     * multiplicados por su factor), para que la nueva receta sea autocontenida.
     */
    suspend fun guardarComidaComoReceta(comidaId: Long, nombreReceta: String, fotoUri: String?, emoji: String): Long {
        val detalle = obtenerComidaDetalladaUnaVez(comidaId) ?: return -1

        // Combinamos ingredientes directos + ingredientes "aplanados" de las recetas incluidas
        val ingredientesCombinados = mutableMapOf<Long, Double>()

        detalle.lineasAlimentos.forEach { linea ->
            ingredientesCombinados[linea.alimento.id] =
                (ingredientesCombinados[linea.alimento.id] ?: 0.0) + linea.gramos
        }

        detalle.lineasRecetas.forEach { lineaReceta ->
            val ingredientesReceta = recetaDao.obtenerIngredientesSync(lineaReceta.receta.id)
            ingredientesReceta.forEach { ing ->
                val gramosEscalados = ing.gramos * lineaReceta.factor
                ingredientesCombinados[ing.alimentoId] =
                    (ingredientesCombinados[ing.alimentoId] ?: 0.0) + gramosEscalados
            }
        }

        return crearReceta(
            nombre = nombreReceta,
            fotoUri = fotoUri,
            emoji = emoji,
            ingredientes = ingredientesCombinados.map { it.key to it.value }
        )
    }
}
