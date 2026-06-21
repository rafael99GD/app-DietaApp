package com.rafael.dietaapp.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(titulo, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(12.dp))

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("${totales.kcal.formato()} kcal", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(10.dp))

            FilaNutriente("Grasas", totales.grasas, "g")
            FilaNutriente("  de las cuales saturadas", totales.grasasSaturadas, "g")
            FilaNutriente("Hidratos de carbono", totales.hidratos, "g")
            FilaNutriente("  de los cuales azúcares", totales.azucares, "g")
            FilaNutriente("Proteínas", totales.proteinas, "g")
            FilaNutriente("Sal", totales.sal, "g")
        }
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
