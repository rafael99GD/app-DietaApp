package com.rafael.dietaapp.util

import android.content.Context
import android.net.Uri
import android.widget.Toast
import com.rafael.dietaapp.data.entities.Alimento
import com.rafael.dietaapp.data.repository.DietaRepository
import org.json.JSONObject

object ImportUtils {

    suspend fun importarDesdeUri(context: Context, uri: Uri, repository: DietaRepository) {
        try {
            val content = context.contentResolver.openInputStream(uri)?.bufferedReader()?.use { it.readText() }
            if (content == null) return

            val json = JSONObject(content)
            val tipo = json.optString("tipo")

            if (tipo == "ALIMENTO") {
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
                Toast.makeText(context, "Alimento '${alimento.nombre}' importado", Toast.LENGTH_SHORT).show()
            } else if (tipo == "RECETA") {
                val nombre = json.getString("nombre")
                val emoji = json.getString("emoji")
                val notas = json.optString("notas", "")
                val ingredientesJson = json.getJSONArray("ingredientes")
                
                val ingredientesIds = mutableListOf<Pair<Long, Double>>()
                
                for (i in 0 until ingredientesJson.length()) {
                    val ingJson = ingredientesJson.getJSONObject(i)
                    val nombreAl = ingJson.getString("nombre")
                    
                    val existentes = repository.obtenerAlimentosUnaVez()
                    var alimentoId: Long? = existentes.find { it.nombre.equals(nombreAl, ignoreCase = true) }?.id
                    
                    if (alimentoId == null) {
                        val nuevoAl = Alimento(
                            nombre = nombreAl,
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
                Toast.makeText(context, "Receta '$nombre' importada con sus ingredientes", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Error al importar: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
}
