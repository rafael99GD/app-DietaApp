package com.rafael.dietaapp.data.model

/**
 * Representa un conjunto de valores nutricionales totales (no por 100g, sino ya calculados
 * para una cantidad concreta). Se usa para sumar comidas, días, recetas, etc.
 */
data class NutrientesTotales(
    val kcal: Double = 0.0,
    val grasas: Double = 0.0,
    val grasasSaturadas: Double = 0.0,
    val hidratos: Double = 0.0,
    val azucares: Double = 0.0,
    val proteinas: Double = 0.0,
    val sal: Double = 0.0
) {
    operator fun plus(other: NutrientesTotales): NutrientesTotales = NutrientesTotales(
        kcal = kcal + other.kcal,
        grasas = grasas + other.grasas,
        grasasSaturadas = grasasSaturadas + other.grasasSaturadas,
        hidratos = hidratos + other.hidratos,
        azucares = azucares + other.azucares,
        proteinas = proteinas + other.proteinas,
        sal = sal + other.sal
    )

    operator fun times(factor: Double): NutrientesTotales = NutrientesTotales(
        kcal = kcal * factor,
        grasas = grasas * factor,
        grasasSaturadas = grasasSaturadas * factor,
        hidratos = hidratos * factor,
        azucares = azucares * factor,
        proteinas = proteinas * factor,
        sal = sal * factor
    )

    companion object {
        /** Calcula los totales de un alimento (valores por 100g) para una cantidad en gramos dada. */
        fun deAlimento(
            kcal100: Double, grasas100: Double, grasasSat100: Double,
            hidratos100: Double, azucares100: Double, proteinas100: Double,
            sal100: Double, gramos: Double
        ): NutrientesTotales {
            val factor = gramos / 100.0
            return NutrientesTotales(
                kcal = kcal100 * factor,
                grasas = grasas100 * factor,
                grasasSaturadas = grasasSat100 * factor,
                hidratos = hidratos100 * factor,
                azucares = azucares100 * factor,
                proteinas = proteinas100 * factor,
                sal = sal100 * factor
            )
        }
    }
}
