package com.rafael.dietaapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.rafael.dietaapp.util.FechaUtils
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

/**
 * Diálogo de calendario mensual (estilo selector nativo tipo Samsung):
 * navegas mes a mes, los días con datos llevan un puntito debajo,
 * y al tocar un día se selecciona y se cierra.
 */
@Composable
fun SelectorCalendarioDialog(
    fechaSeleccionada: String,
    fechasConDatos: Set<String>,
    onFechaSeleccionada: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var mesMostrado by remember {
        mutableStateOf(YearMonth.from(FechaUtils.deTexto(fechaSeleccionada)))
    }
    val hoy = LocalDate.now()

    Dialog(onDismissRequest = onDismiss) {
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp)) {

                // Cabecera: mes/año + flechas de navegación
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { mesMostrado = mesMostrado.minusMonths(1) }) {
                        Icon(Icons.Default.ChevronLeft, contentDescription = "Mes anterior")
                    }
                    Text(
                        "${mesMostrado.month.getDisplayName(TextStyle.FULL, Locale("es", "ES")).replaceFirstChar { it.uppercase() }} ${mesMostrado.year}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    IconButton(
                        onClick = { mesMostrado = mesMostrado.plusMonths(1) },
                        enabled = mesMostrado.isBefore(YearMonth.from(hoy))
                    ) {
                        Icon(Icons.Default.ChevronRight, contentDescription = "Mes siguiente")
                    }
                }

                Spacer(Modifier.height(8.dp))

                // Cabecera de días de la semana (L M X J V S D)
                Row(Modifier.fillMaxWidth()) {
                    listOf("L", "M", "X", "J", "V", "S", "D").forEach { dia ->
                        Text(
                            dia,
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(Modifier.height(4.dp))

                // Grid de días del mes
                val primerDiaDelMes = mesMostrado.atDay(1)
                // DayOfWeek.value: lunes=1 ... domingo=7, así que esto ya alinea la semana empezando en lunes
                val offsetInicial = primerDiaDelMes.dayOfWeek.value - 1
                val diasEnMes = mesMostrado.lengthOfMonth()

                val celdas = buildList {
                    repeat(offsetInicial) { add(null) }
                    for (dia in 1..diasEnMes) add(mesMostrado.atDay(dia))
                }

                LazyVerticalGrid(
                    columns = GridCells.Fixed(7),
                    modifier = Modifier.height(((celdas.size / 7 + 1) * 44).dp)
                ) {
                    items(celdas) { fecha ->
                        if (fecha == null) {
                            Box(Modifier.size(44.dp))
                        } else {
                            val fechaTexto = FechaUtils.aTexto(fecha)
                            val esFuturo = fecha.isAfter(hoy)
                            val esSeleccionado = fechaTexto == fechaSeleccionada
                            val esHoy = fecha == hoy
                            val tieneDatos = fechasConDatos.contains(fechaTexto)

                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .padding(2.dp)
                                    .clip(CircleShape)
                                    .background(
                                        when {
                                            esSeleccionado -> MaterialTheme.colorScheme.primary
                                            esHoy -> MaterialTheme.colorScheme.primaryContainer
                                            else -> Color.Transparent
                                        }
                                    )
                                    .clickable(enabled = !esFuturo) {
                                        onFechaSeleccionada(fechaTexto)
                                        onDismiss()
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        fecha.dayOfMonth.toString(),
                                        color = when {
                                            esSeleccionado -> MaterialTheme.colorScheme.onPrimary
                                            esFuturo -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                                            else -> MaterialTheme.colorScheme.onSurface
                                        },
                                        fontWeight = if (esHoy || esSeleccionado) FontWeight.Bold else FontWeight.Normal,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    if (tieneDatos) {
                                        Box(
                                            Modifier
                                                .padding(top = 1.dp)
                                                .size(4.dp)
                                                .clip(CircleShape)
                                                .background(
                                                    if (esSeleccionado) MaterialTheme.colorScheme.onPrimary
                                                    else MaterialTheme.colorScheme.secondary
                                                )
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(Modifier.height(8.dp))

                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) { Text("Cerrar") }
                }
            }
        }
    }
}
