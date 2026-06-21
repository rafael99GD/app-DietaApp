package com.rafael.dietaapp.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.rafael.dietaapp.data.model.NutrientesTotales
import java.util.Locale

/** Formatea un número con 1 decimal, quitando el .0 si es entero. */
fun Double.formato(): String {
    return if (this == this.toLong().toDouble()) {
        this.toLong().toString()
    } else {
        String.format(Locale.US, "%.1f", this)
    }
}

@Composable
fun ResumenNutrientesCard(
    totales: NutrientesTotales,
    titulo: String = "Resumen nutricional",
    kcalGoal: Double = 0.0,
    proteinasGoal: Double = 0.0,
    carbsGoal: Double = 0.0,
    grasasGoal: Double = 0.0,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        Column(Modifier.padding(20.dp)) {
            Text(titulo, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(16.dp))

            // Calorías con progreso circular o grande
            val progresoKcal = if (kcalGoal > 0) (totales.kcal / kcalGoal).toFloat() else 0f
            
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        "${totales.kcal.formato()} kcal",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.primary
                    )
                    if (kcalGoal > 0) {
                        Text(
                            "de ${kcalGoal.toInt()} kcal objetivo",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                
                if (kcalGoal > 0) {
                    Box(contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(
                            progress = { progresoKcal.coerceIn(0f, 1f) },
                            modifier = Modifier.size(60.dp),
                            strokeWidth = 8.dp,
                            trackColor = MaterialTheme.colorScheme.surface,
                            strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
                        )
                        Text(
                            "${(progresoKcal * 100).toInt()}%",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            
            Spacer(Modifier.height(20.dp))

            // Macros con barras de progreso
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                MacroColumna("Proteínas", totales.proteinas, proteinasGoal, MaterialTheme.colorScheme.primary, Modifier.weight(1f))
                MacroColumna("Carbs", totales.hidratos, carbsGoal, Color(0xFFE91E63), Modifier.weight(1f))
                MacroColumna("Grasas", totales.grasas, grasasGoal, Color(0xFFFF9800), Modifier.weight(1f))
            }

            Spacer(Modifier.height(16.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant, thickness = 0.5.dp)
            Spacer(Modifier.height(12.dp))

            FilaNutrienteSimple("Grasas saturadas", totales.grasasSaturadas, "g")
            FilaNutrienteSimple("Azúcares", totales.azucares, "g")
            FilaNutrienteSimple("Sal", totales.sal, "g")
        }
    }
}

@Composable
fun MacroColumna(nombre: String, valor: Double, goal: Double, color: Color, modifier: Modifier = Modifier) {
    Column(modifier) {
        Text(nombre, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(4.dp))
        val progreso = if (goal > 0) (valor / goal).toFloat() else 0f
        LinearProgressIndicator(
            progress = { progreso.coerceIn(0f, 1f) },
            modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
            color = color,
            trackColor = color.copy(alpha = 0.2f),
            strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
        )
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("${valor.formato()}g", style = MaterialTheme.typography.labelSmall)
            if (goal > 0) {
                Text("${goal.toInt()}g", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
fun FilaNutrienteSimple(nombre: String, valor: Double, unidad: String) {
    Row(
        Modifier.fillMaxWidth().padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(nombre, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text("${valor.formato()} $unidad", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun FilaNutriente(nombre: String, valor: Double, unidad: String) {
    Row(
        Modifier.fillMaxWidth().padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(nombre, style = MaterialTheme.typography.bodyMedium)
        Text("${valor.formato()} $unidad", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
    }
}
