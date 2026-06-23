package com.rafael.dietaapp.util

import android.content.Context
import android.widget.Toast
import com.rafael.dietaapp.data.entities.Alimento
import com.rafael.dietaapp.data.repository.DietaRepository
import com.rafael.dietaapp.data.model.LineaAlimento
import org.json.JSONArray
import org.json.JSONObject

object ImportUtils {

    suspend fun importarDesdeUri(context: Context, uri: android.net.Uri, repository: DietaRepository) {
        try {
            val content = context.contentResolver.openInputStream(uri)?.bufferedReader()?.use { it.readText() }
            if (content == null) return

            val json = JSONObject(content)
            val tipo = json.optString("tipo")

            var importados = 0
            var omitidos = 0

            when (tipo) {
                "ALIMENTO" -> {
                    if (importarAlimento(json, repository)) importados++ else omitidos++
                }
                "MULTIPLE_ALIMENTOS" -> {
                    val array = json.getJSONArray("alimentos")
                    for (i in 0 until array.length()) {
                        if (importarAlimento(array.getJSONObject(i), repository)) importados++ else omitidos++
                    }
                }
                "RECETA" -> {
                    if (importarReceta(json, repository)) importados++ else omitidos++
                }
                "MULTIPLE_RECETAS" -> {
                    val array = json.getJSONArray("recetas")
                    for (i in 0 until array.length()) {
                        if (importarReceta(array.getJSONObject(i), repository)) importados++ else omitidos++
                    }
                }
            }
            
            val msg = if (omitidos == 0) "Importados $importados elementos"
            else "Importados $importados (omitidos $omitidos duplicados)"
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()

        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Error al importar: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private suspend fun importarAlimento(json: JSONObject, repository: DietaRepository): Boolean {
        if (encontrarAlimentoIdentico(json, repository) != null) return false

        val alimento = Alimento(
            nombre = json.getString("nombre"),
            emoji = json.getString("emoji"),
            kcal = json.getDouble("kcal"),
            proteinas = json.getDouble("proteinas"),
            hidratos = json.getDouble("hidratos"),
            grasas = json.getDouble("grasas"),
            grasasSaturadas = json.getDouble("grasasSaturadas"),
            azucares = json.getDouble("azucares"),
            sal = json.getDouble("sal")
        )
        repository.guardarAlimento(alimento)
        return true
    }

    private suspend fun encontrarAlimentoIdentico(json: JSONObject, repository: DietaRepository): Long? {
        val nombre = json.getString("nombre")
        val kcal = json.getDouble("kcal")
        val prot = json.getDouble("proteinas")
        val carb = json.getDouble("hidratos")
        val gras = json.getDouble("grasas")
        val grasSat = json.getDouble("grasasSaturadas")
        val azuc = json.getDouble("azucares")
        val sal = json.getDouble("sal")
        
        val existentes = repository.obtenerAlimentosUnaVez()
        return existentes.find { 
            it.nombre.equals(nombre, ignoreCase = true) &&
            Math.abs(it.kcal - kcal) < 0.01 &&
            Math.abs(it.proteinas - prot) < 0.01 &&
            Math.abs(it.hidratos - carb) < 0.01 &&
            Math.abs(it.grasas - gras) < 0.01 &&
            Math.abs(it.grasasSaturadas - grasSat) < 0.01 &&
            Math.abs(it.azucares - azuc) < 0.01 &&
            Math.abs(it.sal - sal) < 0.01
        }?.id
    }

    private suspend fun importarReceta(json: JSONObject, repository: DietaRepository): Boolean {
        if (esRecetaIdentica(json, repository)) return false

        val nombre = json.getString("nombre")
        val emoji = json.getString("emoji")
        val notas = json.optString("notas", "")
        val ingredientesJson = json.getJSONArray("ingredientes")
        
        val ingredientesIds = mutableListOf<Pair<Long, Double>>()
        
        for (i in 0 until ingredientesJson.length()) {
            val ingJson = ingredientesJson.getJSONObject(i)
            var alimentoId = encontrarAlimentoIdentico(ingJson, repository)
            
            if (alimentoId == null) {
                val nuevoAl = Alimento(
                    nombre = ingJson.getString("nombre"),
                    emoji = ingJson.getString("emoji"),
                    kcal = ingJson.getDouble("kcal"),
                    proteinas = ingJson.getDouble("proteinas"),
                    hidratos = ingJson.getDouble("hidratos"),
                    grasas = ingJson.getDouble("grasas"),
                    grasasSaturadas = ingJson.getDouble("grasasSaturadas"),
                    azucares = ingJson.getDouble("azucares"),
                    sal = ingJson.getDouble("sal")
                )
                alimentoId = repository.guardarAlimento(nuevoAl)
            }
            ingredientesIds.add(alimentoId to ingJson.getDouble("gramos"))
        }
        
        repository.crearReceta(nombre, null, emoji, notas, ingredientesIds)
        return true
    }

    private suspend fun esRecetaIdentica(json: JSONObject, repository: DietaRepository): Boolean {
        val nombre = json.getString("nombre")
        val emoji = json.getString("emoji")
        val notas = json.optString("notas", "")
        val ingredientesJson = json.getJSONArray("ingredientes")
        
        val existentes = repository.obtenerTodasLasRecetasDetalladasUnaVez()
        return existentes.any { r ->
            r.receta.nombre.equals(nombre, ignoreCase = true) &&
            r.receta.emoji == emoji &&
            r.receta.notas == notas &&
            compararIngredientes(ingredientesJson, r.ingredientes)
        }
    }

    private fun compararIngredientes(jsonArr: JSONArray, existentes: List<LineaAlimento>): Boolean {
        if (jsonArr.length() != existentes.size) return false
        
        for (i in 0 until jsonArr.length()) {
            val j = jsonArr.getJSONObject(i)
            val nombre = j.getString("nombre")
            val gramos = j.getDouble("gramos")
            
            val coincide = existentes.any { ex ->
                ex.alimento.nombre.equals(nombre, ignoreCase = true) &&
                Math.abs(ex.gramos - gramos) < 0.01
            }
            if (!coincide) return false
        }
        return true
    }
}
