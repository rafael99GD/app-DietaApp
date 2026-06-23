package com.rafael.dietaapp.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import com.rafael.dietaapp.data.entities.Alimento
import com.rafael.dietaapp.data.model.RecetaDetallada
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

object ExportUtils {

    fun compartirReceta(context: Context, receta: RecetaDetallada) {
        compartirRecetas(context, listOf(receta))
    }

    fun compartirRecetas(context: Context, recetas: List<RecetaDetallada>) {
        val root = JSONObject().apply {
            put("tipo", "MULTIPLE_RECETAS")
            val array = JSONArray()
            recetas.forEach { receta ->
                val rJson = JSONObject().apply {
                    put("nombre", receta.receta.nombre)
                    put("emoji", receta.receta.emoji)
                    put("notas", receta.receta.notas)
                    
                    val ingredientesJson = JSONArray()
                    receta.ingredientes.forEach { linea ->
                        val ing = JSONObject().apply {
                            put("nombre", linea.alimento.nombre)
                            put("emoji", linea.alimento.emoji)
                            put("gramos", linea.gramos)
                            put("kcal", linea.alimento.kcal)
                            put("proteinas", linea.alimento.proteinas)
                            put("hidratos", linea.alimento.hidratos)
                            put("grasas", linea.alimento.grasas)
                            put("grasasSaturadas", linea.alimento.grasasSaturadas)
                            put("azucares", linea.alimento.azucares)
                            put("sal", linea.alimento.sal)
                        }
                        ingredientesJson.put(ing)
                    }
                    put("ingredientes", ingredientesJson)
                }
                array.put(rJson)
            }
            put("recetas", array)
        }
        
        val nombreArchivo = if (recetas.size == 1) 
            "receta_${recetas[0].receta.nombre.replace(" ", "_")}.dieta"
        else 
            "recetas_mi_dieta.dieta"
            
        compartirJson(context, root, nombreArchivo)
    }

    fun compartirAlimento(context: Context, alimento: Alimento) {
        compartirAlimentos(context, listOf(alimento))
    }

    fun compartirAlimentos(context: Context, alimentos: List<Alimento>) {
        val root = JSONObject().apply {
            put("tipo", "MULTIPLE_ALIMENTOS")
            val array = JSONArray()
            alimentos.forEach { alimento ->
                val aJson = JSONObject().apply {
                    put("nombre", alimento.nombre)
                    put("emoji", alimento.emoji)
                    put("kcal", alimento.kcal)
                    put("proteinas", alimento.proteinas)
                    put("hidratos", alimento.hidratos)
                    put("grasas", alimento.grasas)
                    put("grasasSaturadas", alimento.grasasSaturadas)
                    put("azucares", alimento.azucares)
                    put("sal", alimento.sal)
                }
                array.put(aJson)
            }
            put("alimentos", array)
        }

        val nombreArchivo = if (alimentos.size == 1)
            "alimento_${alimentos[0].nombre.replace(" ", "_")}.dieta"
        else
            "alimentos_mi_dieta.dieta"

        compartirJson(context, root, nombreArchivo)
    }

    private fun compartirJson(context: Context, json: JSONObject, fileName: String) {
        val file = File(context.cacheDir, fileName)
        file.writeText(json.toString())
        
        val uri: Uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )

        val intent = Intent(Intent.ACTION_SEND).apply {
            // Cambiamos a un tipo MIME más específico para ayudar a Android
            type = "application/octet-stream" 
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        
        context.startActivity(Intent.createChooser(intent, "Compartir o Guardar archivo .dieta"))
    }
}
