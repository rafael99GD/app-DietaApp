package com.rafael.dietaapp.data.model

import com.rafael.dietaapp.data.entities.Alimento
import com.rafael.dietaapp.data.entities.Comida
import com.rafael.dietaapp.data.entities.Extra
import com.rafael.dietaapp.data.entities.Receta

/** Una línea de alimento dentro de una comida, ya con el alimento resuelto y sus gramos. */
data class LineaAlimento(
    val comidaAlimentoId: Long,
    val alimento: Alimento,
    val gramos: Double
) {
    val totales: NutrientesTotales
        get() = NutrientesTotales.deAlimento(
            alimento.kcal, alimento.grasas, alimento.grasasSaturadas,
            alimento.hidratos, alimento.azucares, alimento.proteinas, alimento.sal, gramos
        )
}

/** Una línea de receta dentro de una comida, ya con la receta resuelta y su factor. */
data class LineaReceta(
    val comidaRecetaId: Long,
    val receta: Receta,
    val factor: Double,
    val totalesBase: NutrientesTotales // totales de la receta completa (factor 1.0)
) {
    val totales: NutrientesTotales
        get() = totalesBase * factor
}

/** Una comida con todo su contenido resuelto: líneas de alimentos sueltos + líneas de recetas. */
data class ComidaDetallada(
    val comida: Comida,
    val lineasAlimentos: List<LineaAlimento>,
    val lineasRecetas: List<LineaReceta>
) {
    val totales: NutrientesTotales
        get() = lineasAlimentos.fold(NutrientesTotales()) { acc, l -> acc + l.totales } +
                lineasRecetas.fold(NutrientesTotales()) { acc, l -> acc + l.totales }
}

/** Resumen completo de un día: todas sus comidas + extras + el total general. */
data class DiaDetallado(
    val fecha: String,
    val comidas: List<ComidaDetallada>,
    val extras: List<Extra>
) {
    val totalExtras: NutrientesTotales
        get() = extras.fold(NutrientesTotales()) { acc, e ->
            acc + NutrientesTotales(
                e.kcal, e.grasas, e.grasasSaturadas, e.hidratos, e.azucares, e.proteinas, e.sal
            )
        }

    val totalDia: NutrientesTotales
        get() = comidas.fold(NutrientesTotales()) { acc, c -> acc + c.totales } + totalExtras
}

/** Una receta con sus ingredientes resueltos. */
data class RecetaDetallada(
    val receta: Receta,
    val ingredientes: List<LineaAlimento>
) {
    val totales: NutrientesTotales
        get() = ingredientes.fold(NutrientesTotales()) { acc, l -> acc + l.totales }
}
