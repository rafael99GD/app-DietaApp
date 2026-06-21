package com.rafael.dietaapp.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.rafael.dietaapp.data.entities.Extra
import com.rafael.dietaapp.data.model.ComidaDetallada
import com.rafael.dietaapp.data.repository.DietaRepository
import com.rafael.dietaapp.ui.components.ResumenNutrientesCard
import com.rafael.dietaapp.ui.components.SelectorCalendarioDialog
import com.rafael.dietaapp.ui.components.formato
import com.rafael.dietaapp.util.FechaUtils
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiaScreen(
    fecha: String,
    repository: DietaRepository,
    onIrAComida: (Long) -> Unit,
    onIrAAlimentos: () -> Unit,
    onIrARecetas: () -> Unit,
    onIrAExtra: (Long?) -> Unit,
    onCambiarFecha: (String) -> Unit
) {
    val scope = rememberCoroutineScope()
    val diaDetallado by repository.obtenerDiaDetallado(fecha).collectAsState(
        initial = com.rafael.dietaapp.data.model.DiaDetallado(fecha, emptyList(), emptyList())
    )
    val fechasConDatos by repository.obtenerFechasConDatos().collectAsState(initial = emptySet())

    var mostrarDialogoNuevaComida by remember { mutableStateOf(false) }
    var mostrarCalendario by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mi Dieta") },
                actions = {
                    if (!FechaUtils.esHoy(fecha)) {
                        TextButton(onClick = { onCambiarFecha(FechaUtils.hoy()) }) {
                            Text("Hoy")
                        }
                    }
                    IconButton(onClick = onIrAAlimentos) {
                        Icon(Icons.Default.Restaurant, contentDescription = "Alimentos")
                    }
                    IconButton(onClick = onIrARecetas) {
                        Icon(Icons.Default.MenuBook, contentDescription = "Recetas")
                    }
                }
            )
        },
        floatingActionButton = {
            Column(horizontalAlignment = Alignment.End) {
                ExtendedFloatingActionButton(
                    onClick = { onIrAExtra(null) },
                    icon = { Icon(Icons.Default.LocalCafe, contentDescription = null) },
                    text = { Text("Extra") }
                )
                Spacer(Modifier.height(12.dp))
                ExtendedFloatingActionButton(
                    onClick = { mostrarDialogoNuevaComida = true },
                    icon = { Icon(Icons.Default.Add, contentDescription = null) },
                    text = { Text("Comida") }
                )
            }
        }
    ) { padding ->
        Column(Modifier.padding(padding).fillMaxSize()) {

            // Selector de fecha con flechas
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { onCambiarFecha(FechaUtils.diaAnterior(fecha)) }) {
                    Icon(Icons.Default.ChevronLeft, contentDescription = "Día anterior")
                }
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clip(androidx.compose.foundation.shape.RoundedCornerShape(8.dp))
                        .clickable { mostrarCalendario = true }
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Text(
                        if (FechaUtils.esHoy(fecha)) "Hoy" else FechaUtils.formatoCorto(fecha),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        FechaUtils.formatoLegible(fecha),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                IconButton(
                    onClick = { onCambiarFecha(FechaUtils.diaSiguiente(fecha)) },
                    enabled = FechaUtils.puedeAvanzar(fecha)
                ) {
                    Icon(Icons.Default.ChevronRight, contentDescription = "Día siguiente")
                }
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    ResumenNutrientesCard(
                        totales = diaDetallado.totalDia,
                        titulo = "Resumen del día"
                    )
                }

                if (diaDetallado.comidas.isEmpty() && diaDetallado.extras.isEmpty()) {
                    item {
                        Box(
                            Modifier.fillMaxWidth().padding(vertical = 48.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "Todavía no has añadido nada hoy.\nUsa los botones de abajo para empezar.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }

                if (diaDetallado.comidas.isNotEmpty()) {
                    item {
                        Text("Comidas", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    }
                    items(diaDetallado.comidas, key = { "comida_${it.comida.id}" }) { comidaDetallada ->
                        TarjetaComida(comidaDetallada, onClick = { onIrAComida(comidaDetallada.comida.id) })
                    }
                }

                if (diaDetallado.extras.isNotEmpty()) {
                    item {
                        Text("Extras (fuera de casa)", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    }
                    items(diaDetallado.extras, key = { "extra_${it.id}" }) { extra ->
                        TarjetaExtra(extra, onClick = { onIrAExtra(extra.id) })
                    }
                }

                item { Spacer(Modifier.height(80.dp)) }
            }
        }
    }

    if (mostrarDialogoNuevaComida) {
        var nombreComida by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { mostrarDialogoNuevaComida = false },
            title = { Text("Nueva comida") },
            text = {
                OutlinedTextField(
                    value = nombreComida,
                    onValueChange = { nombreComida = it },
                    label = { Text("Nombre (ej. Desayuno)") },
                    singleLine = true
                )
            },
            confirmButton = {
                TextButton(
                    enabled = nombreComida.isNotBlank(),
                    onClick = {
                        scope.launch {
                            val id = repository.crearComida(fecha, nombreComida.trim())
                            mostrarDialogoNuevaComida = false
                            nombreComida = ""
                            onIrAComida(id)
                        }
                    }
                ) { Text("Crear") }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogoNuevaComida = false }) { Text("Cancelar") }
            }
        )
    }

    if (mostrarCalendario) {
        SelectorCalendarioDialog(
            fechaSeleccionada = fecha,
            fechasConDatos = fechasConDatos,
            onFechaSeleccionada = { nuevaFecha -> onCambiarFecha(nuevaFecha) },
            onDismiss = { mostrarCalendario = false }
        )
    }
}

@Composable
fun TarjetaComida(comidaDetallada: ComidaDetallada, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp)
    ) {
        Row(
            Modifier.padding(14.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(comidaDetallada.comida.nombre, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                val numItems = comidaDetallada.lineasAlimentos.size + comidaDetallada.lineasRecetas.size
                Text(
                    "$numItems ${if (numItems == 1) "elemento" else "elementos"}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                "${comidaDetallada.totales.kcal.formato()} kcal",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun TarjetaExtra(extra: Extra, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
    ) {
        Row(
            Modifier.padding(14.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(extra.emoji, style = MaterialTheme.typography.titleLarge)
                Spacer(Modifier.width(10.dp))
                Text(extra.nombre, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            }
            Text(
                "${extra.kcal.formato()} kcal",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
