package com.rafael.dietaapp.navigation

object Rutas {
    const val DIA = "dia/{fecha}"
    const val ALIMENTOS = "alimentos"
    const val ALIMENTO_FORM = "alimento_form?id={id}"
    const val RECETAS = "recetas"
    const val RECETA_DETALLE = "receta_detalle/{recetaId}"
    const val RECETA_FORM = "receta_form?id={id}"
    const val COMIDA_DETALLE = "comida_detalle/{comidaId}"
    const val EXTRA_FORM = "extra_form/{fecha}?extraId={extraId}"
    const val GUARDAR_RECETA = "guardar_receta/{comidaId}"

    fun dia(fecha: String) = "dia/$fecha"
    fun alimentoForm(id: Long? = null) = if (id != null) "alimento_form?id=$id" else "alimento_form"
    fun recetaDetalle(recetaId: Long) = "receta_detalle/$recetaId"
    fun recetaForm(id: Long? = null) = if (id != null) "receta_form?id=$id" else "receta_form"
    fun comidaDetalle(comidaId: Long) = "comida_detalle/$comidaId"
    fun extraForm(fecha: String, extraId: Long? = null) =
        if (extraId != null) "extra_form/$fecha?extraId=$extraId" else "extra_form/$fecha"
    fun guardarReceta(comidaId: Long) = "guardar_receta/$comidaId"
}
