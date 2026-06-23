package com.rafael.dietaapp.util

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State

enum class AppTheme { LIGHT, DARK, SYSTEM }

class UserPreferences(context: Context) {
    private val prefs = context.getSharedPreferences("dieta_prefs", Context.MODE_PRIVATE)

    private val _kcalGoal = mutableStateOf(prefs.getFloat("kcal_goal", 2000f).toDouble())
    val kcalGoal: State<Double> = _kcalGoal

    private val _proteinasGoal = mutableStateOf(prefs.getFloat("proteinas_goal", 150f).toDouble())
    val proteinasGoal: State<Double> = _proteinasGoal

    private val _carbsGoal = mutableStateOf(prefs.getFloat("carbs_goal", 250f).toDouble())
    val carbsGoal: State<Double> = _carbsGoal

    private val _grasasGoal = mutableStateOf(prefs.getFloat("grasas_goal", 70f).toDouble())
    val grasasGoal: State<Double> = _grasasGoal

    private val _theme = mutableStateOf(
        AppTheme.valueOf(prefs.getString("app_theme", AppTheme.SYSTEM.name) ?: AppTheme.SYSTEM.name)
    )
    val theme: State<AppTheme> = _theme

    fun saveGoals(kcal: Double, proteinas: Double, carbs: Double, grasas: Double) {
        prefs.edit().apply {
            putFloat("kcal_goal", kcal.toFloat())
            putFloat("proteinas_goal", proteinas.toFloat())
            putFloat("carbs_goal", carbs.toFloat())
            putFloat("grasas_goal", grasas.toFloat())
            apply()
        }
        _kcalGoal.value = kcal
        _proteinasGoal.value = proteinas
        _carbsGoal.value = carbs
        _grasasGoal.value = grasas
    }

    fun saveTheme(newTheme: AppTheme) {
        prefs.edit().putString("app_theme", newTheme.name).apply()
        _theme.value = newTheme
    }
}
