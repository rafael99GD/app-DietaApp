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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.rafael.dietaapp.data.entities.Comida
import com.rafael.dietaapp.data.entities.ComidaAlimento
import com.rafael.dietaapp.data.entities.ComidaReceta
import com.rafael.dietaapp.data.model.ComidaDetallada
import com.rafael.dietaapp.data.model.LineaAlimento
import com.rafael.dietaapp.data.model.LineaReceta
import com.rafael.dietaapp.data.repository.DietaRepository
import com.rafael.dietaapp.ui.components.CampoNumerico
import com.rafael.dietaapp.ui.components.ResumenNutrientesCard
import com.rafael.dietaapp.ui.components.formato
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComidaDetalleScreen(
    comidaId: Long,
    repository: DietaRepository,
    onVolver: () -> Unit,
    onGuardarComoReceta: (Long) -> Unit
) {
    val scope = rememberCoroutineScope()

    var comida by remember { mutableStateOf<Comida?>(null) }
    var detalleComida by remember { mutableStateOf<ComidaDetallada?>(null) }
    var version by remember { mutableStateOf(0) }

    var mostrarSelectorAlimento by remember { mutableStateOf(false) }
    var mostrarSelectorReceta by remember { mutableStateOf(false) }
    var mostrarMenu by remember { mutableStateOf(false) }
    var mostrarDialogoEditarNombre by remember { mutableStateOf(false) }

    LaunchedEffect(comidaId, version) {
        val detalle = repository.obtenerComidaDetalladaUnaVez(comidaId)
        comida = detalle?.comida
        detalleComida = detalle
    }

    fun refrescar() { version++ }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(comida?.nombre ?: "Comida") },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    IconButton(onClick = { mostrarMenu = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Más opciones")
                    }
                    DropdownMenu(expanded = mostrarMenu, onDismissRequest = { mostrarMenu = false }) {
                        DropdownMenuItem(
                            text = { Text("Cambiar nombre") },
                            onClick = { mostrarMenu = false; mostrarDialogoEditarNombre = true }
                        )
                        DropdownMenuItem(
                            text = { Text("Guardar como receta") },
                            onClick = { mostrarMenu = false; onGuardarComoReceta(comidaId) }
                        )
                        DropdownMenuItem(
                            text = { Text("Eliminar comida") },
                            onClick = {
                                mostrarMenu = false
                                scope.launch {
                                    comida?.let { repository.eliminarComida(it) }
                                    onVolver()
                                }
                            }
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            Column(horizontalAlignment = Alignment.End) {
                ExtendedFloatingActionButton(
                    onClick = { mostrarSelectorReceta = true },
                    icon = { Icon(Icons.Default.MenuBook, contentDescription = null) },
                    text = { Text("Receta") }
                )
                Spacer(Modifier.height(12.dp))
                ExtendedFloatingActionButton(
                    onClick = { mostrarSelectorAlimento = true },
                    icon = { Icon(Icons.Default.Add, contentDescription = null) },
                    text = { Text("Alimento") }
                )
            }
        }
    ) { padding ->
        val detalleActual = detalleComida
        if (detalleActual == null) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier.padding(padding).fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    ResumenNutrientesCard(totales = detalleActual.totales, titulo = "Total de esta comida")
                }

                if (detalleActual.lineasRecetas.isNotEmpty()) {
                    item { Text("Recetas añadidas", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold) }
                    items(detalleActual.lineasRecetas, key = { "receta_${it.comidaRecetaId}" }) { linea ->
                        FilaLineaReceta(linea) {
                            scope.launch {
                                repository.eliminarLineaReceta(
                                    ComidaReceta(
                                        id = linea.comidaRecetaId, comidaId = comidaId,
                                        recetaId = linea.receta.id, factor = linea.factor
                                    )
                                )
                                refrescar()
                            }
                        }
                    }
                }

                if (detalleActual.lineasAlimentos.isNotEmpty()) {
                    item { Text("Alimentos añadidos", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold) }
                    items(detalleActual.lineasAlimentos, key = { "alimento_${it.comidaAlimentoId}" }) { linea ->
                        FilaLineaAlimento(linea) {
                            scope.launch {
                                repository.eliminarLineaAlimento(
                                    ComidaAlimento(
                                        id = linea.comidaAlimentoId, comidaId = comidaId,
                                        alimentoId = linea.alimento.id, gramos = linea.gramos
                                    )
                                )
                                refrescar()
                            }
                        }
                    }
                }

                if (detalleActual.lineasAlimentos.isEmpty() && detalleActual.lineasRecetas.isEmpty()) {
                    item {
                        Box(Modifier.fillMaxWidth().padding(vertical = 40.dp), contentAlignment = Alignment.Center) {
                            Text(
                                "Añade alimentos o una receta con los botones de abajo.",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }

                item { Spacer(Modifier.height(80.dp)) }
            }
        }
    }

    if (mostrarSelectorAlimento) {
        SelectorAlimentoDialog(
            repository = repository,
            onDismiss = { mostrarSelectorAlimento = false },
            onConfirmar = { alimentoId, gramos ->
                scope.launch {
                    repository.agregarAlimentoAComida(comidaId, alimentoId, gramos)
                    mostrarSelectorAlimento = false
                    refrescar()
                }
            }
        )
    }

    if (mostrarSelectorReceta) {
        SelectorRecetaDialog(
            repository = repository,
            onDismiss = { mostrarSelectorReceta = false },
            onConfirmar = { recetaId, factor ->
                scope.launch {
                    repository.agregarRecetaAComida(comidaId, recetaId, factor)
                    mostrarSelectorReceta = false
                    refrescar()
                }
            }
        )
    }

    if (mostrarDialogoEditarNombre && comida != null) {
        var nuevoNombre by remember { mutableStateOf(comida!!.nombre) }
        AlertDialog(
            onDismissRequest = { mostrarDialogoEditarNombre = false },
            title = { Text("Cambiar nombre") },
            text = {
                OutlinedTextField(value = nuevoNombre, onValueChange = { nuevoNombre = it }, singleLine = true)
            },
            confirmButton = {
                TextButton(onClick = {
                    scope.launch {
                        comida?.let { repository.renombrarComida(it, nuevoNombre.trim()) }
                        mostrarDialogoEditarNombre = false
                        refrescar()
                    }
                }) { Text("Guardar") }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogoEditarNombre = false }) { Text("Cancelar") }
            }
        )
    }
}

@Composable
fun FilaLineaAlimento(linea: LineaAlimento, onEliminar: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
        Row(
            Modifier.padding(12.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                Text(linea.alimento.emoji, style = MaterialTheme.typography.titleLarge)
                Spacer(Modifier.width(10.dp))
                Column {
                    Text(linea.alimento.nombre, fontWeight = FontWeight.SemiBold)
                    Text(
                        "${linea.gramos.formato()} g · ${linea.totales.kcal.formato()} kcal",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            IconButton(onClick = onEliminar) {
                Icon(Icons.Default.Close, contentDescription = "Quitar")
            }
        }
    }
}

@Composable
fun FilaLineaReceta(linea: LineaReceta, onEliminar: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Row(
            Modifier.padding(12.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                Text(linea.receta.emoji, style = MaterialTheme.typography.titleLarge)
                Spacer(Modifier.width(10.dp))
                Column {
                    Text(linea.receta.nombre, fontWeight = FontWeight.SemiBold)
                    Text(
                        "x${linea.factor.formato()} · ${linea.totales.kcal.formato()} kcal",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            IconButton(onClick = onEliminar) {
                Icon(Icons.Default.Close, contentDescription = "Quitar")
            }
        }
    }
}

@Composable
fun SelectorAlimentoDialog(
    repository: DietaRepository,
    onDismiss: () -> Unit,
    onConfirmar: (alimentoId: Long, gramos: Double) -> Unit
) {
    val alimentos by repository.obtenerAlimentos().collectAsState(initial = emptyList())
    var alimentoSeleccionado by remember { mutableStateOf<Long?>(null) }
    var gramos by remember { mutableStateOf("") }
    var busqueda by remember { mutableStateOf("") }

    val filtrados = alimentos.filter { it.nombre.contains(busqueda, ignoreCase = true) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Añadir alimento") },
        text = {
            Column {
                if (alimentoSeleccionado == null) {
                    OutlinedTextField(
                        value = busqueda,
                        onValueChange = { busqueda = it },
                        placeholder = { Text("Buscar...") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(8.dp))
                    LazyColumn(modifier = Modifier.heightIn(max = 300.dp)) {
                        items(filtrados, key = { it.id }) { alimento ->
                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .clickable { alimentoSeleccionado = alimento.id }
                                    .padding(vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(alimento.emoji)
                                Spacer(Modifier.width(8.dp))
                                Text(alimento.nombre)
                            }
                        }
                        if (filtrados.isEmpty()) {
                            item { Text("No tienes alimentos guardados con ese nombre.", color = MaterialTheme.colorScheme.onSurfaceVariant) }
                        }
                    }
                } else {
                    val alimento = alimentos.find { it.id == alimentoSeleccionado }
                    Text("${alimento?.emoji} ${alimento?.nombre}", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(12.dp))
                    CampoNumerico(gramos, { gramos = it }, "Cantidad usada", imeAction = ImeAction.Done)
                }
            }
        },
        confirmButton = {
            if (alimentoSeleccionado != null) {
                TextButton(
                    enabled = gramos.aDoubleSeguro() > 0,
                    onClick = { onConfirmar(alimentoSeleccionado!!, gramos.aDoubleSeguro()) }
                ) { Text("Añadir") }
            }
        },
        dismissButton = {
            TextButton(onClick = {
                if (alimentoSeleccionado != null) alimentoSeleccionado = null else onDismiss()
            }) { Text(if (alimentoSeleccionado != null) "Atrás" else "Cancelar") }
        }
    )
}

@Composable
fun SelectorRecetaDialog(
    repository: DietaRepository,
    onDismiss: () -> Unit,
    onConfirmar: (recetaId: Long, factor: Double) -> Unit
) {
    val recetas by repository.obtenerRecetas().collectAsState(initial = emptyList())
    var recetaSeleccionada by remember { mutableStateOf<Long?>(null) }
    var factor by remember { mutableStateOf("1") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Añadir receta") },
        text = {
            Column {
                if (recetaSeleccionada == null) {
                    if (recetas.isEmpty()) {
                        Text("No tienes recetas guardadas todavía.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    } else {
                        LazyColumn(modifier = Modifier.heightIn(max = 300.dp)) {
                            items(recetas, key = { it.id }) { receta ->
                                Row(
                                    Modifier
                                        .fillMaxWidth()
                                        .clickable { recetaSeleccionada = receta.id }
                                        .padding(vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(receta.emoji)
                                    Spacer(Modifier.width(8.dp))
                                    Text(receta.nombre)
                                }
                            }
                        }
                    }
                } else {
                    val receta = recetas.find { it.id == recetaSeleccionada }
                    Text("${receta?.emoji} ${receta?.nombre}", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(12.dp))
                    Text("¿Cuántas raciones de la receta? (1 = la receta completa)", style = MaterialTheme.typography.labelMedium)
                    Spacer(Modifier.height(4.dp))
                    CampoNumerico(factor, { factor = it }, "Cantidad", sufijo = "x", imeAction = ImeAction.Done)
                }
            }
        },
        confirmButton = {
            if (recetaSeleccionada != null) {
                TextButton(
                    enabled = factor.aDoubleSeguro() > 0,
                    onClick = { onConfirmar(recetaSeleccionada!!, factor.aDoubleSeguro()) }
                ) { Text("Añadir") }
            }
        },
        dismissButton = {
            TextButton(onClick = {
                if (recetaSeleccionada != null) recetaSeleccionada = null else onDismiss()
            }) { Text(if (recetaSeleccionada != null) "Atrás" else "Cancelar") }
        }
    )
}

private fun String.aDoubleSeguro(): Double = this.replace(",", ".").toDoubleOrNull() ?: 0.0
