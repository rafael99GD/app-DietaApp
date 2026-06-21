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
        val json = JSONObject().apply {
            put("tipo", "RECETA")
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
        
        compartirJson(context, json, "receta_${receta.receta.nombre.replace(" ", "_")}.dieta")
    }

    fun compartirAlimento(context: Context, alimento: Alimento) {
        val json = JSONObject().apply {
            put("tipo", "ALIMENTO")
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
        compartirJson(context, json, "alimento_${alimento.nombre.replace(" ", "_")}.dieta")
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
            // Usamos un MIME type que Android suele asociar con "archivos" para forzar opciones de guardado
            type = "*/*" 
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        
        // Creamos el chooser, Android 10+ suele incluir "Guardar en Drive" o "Files" automáticamente con type */*
        context.startActivity(Intent.createChooser(intent, "Compartir o Guardar archivo .dieta"))
    }
}
