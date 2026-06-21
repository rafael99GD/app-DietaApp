package com.rafael.dietaapp.ui.screens

import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.rafael.dietaapp.data.entities.Alimento
import com.rafael.dietaapp.data.repository.DietaRepository
import com.rafael.dietaapp.ui.components.CampoNumerico
import com.rafael.dietaapp.ui.components.SelectorEmojiOImagen
import com.rafael.dietaapp.ui.components.formato
import kotlinx.coroutines.launch

private data class IngredienteTemporal(val alimento: Alimento, val gramos: Double)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecetaFormScreen(
    repository: DietaRepository,
    onVolver: () -> Unit
) {
    val scope = rememberCoroutineScope()

    var nombre by remember { mutableStateOf("") }
    var emoji by remember { mutableStateOf("📖") }
    var imagenUri by remember { mutableStateOf<String?>(null) }
    var ingredientes by remember { mutableStateOf(listOf<IngredienteTemporal>()) }
    var mostrarSelector by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nueva receta") },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { mostrarSelector = true },
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("Añadir ingrediente") }
            )
        }
    ) { padding ->
        Column(Modifier.padding(padding).fillMaxSize()) {
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        SelectorEmojiOImagen(
                            emojiSeleccionado = emoji,
                            imagenUri = imagenUri,
                            onEmojiSeleccionado = { emoji = it },
                            onImagenSeleccionada = { uri: Uri? -> imagenUri = uri?.toString() }
                        )
                    }
                }
                item {
                    OutlinedTextField(
                        value = nombre,
                        onValueChange = { nombre = it },
                        label = { Text("Nombre de la receta") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    Text("Ingredientes", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                }
                items(ingredientes) { ing ->
                    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
                        Row(
                            Modifier.padding(12.dp).fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(ing.alimento.emoji, style = MaterialTheme.typography.titleLarge)
                                Spacer(Modifier.width(10.dp))
                                Column {
                                    Text(ing.alimento.nombre, fontWeight = FontWeight.SemiBold)
                                    Text("${ing.gramos.formato()} g", style = MaterialTheme.typography.labelMedium)
                                }
                            }
                            IconButton(onClick = { ingredientes = ingredientes - ing }) {
                                Icon(Icons.Default.Close, contentDescription = "Quitar")
                            }
                        }
                    }
                }
                if (ingredientes.isEmpty()) {
                    item {
                        Text(
                            "Añade ingredientes con el botón de abajo.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                item { Spacer(Modifier.height(80.dp)) }
            }

            Button(
                onClick = {
                    scope.launch {
                        repository.crearReceta(
                            nombre = nombre.trim(),
                            fotoUri = imagenUri,
                            emoji = emoji,
                            ingredientes = ingredientes.map { it.alimento.id to it.gramos }
                        )
                        onVolver()
                    }
                },
                enabled = nombre.isNotBlank() && ingredientes.isNotEmpty(),
                modifier = Modifier.fillMaxWidth().padding(16.dp)
            ) {
                Text("Guardar receta")
            }
        }
    }

    if (mostrarSelector) {
        SelectorAlimentoParaRecetaDialog(
            repository = repository,
            onDismiss = { mostrarSelector = false },
            onConfirmar = { alimento, gramos ->
                ingredientes = ingredientes + IngredienteTemporal(alimento, gramos)
                mostrarSelector = false
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SelectorAlimentoParaRecetaDialog(
    repository: DietaRepository,
    onDismiss: () -> Unit,
    onConfirmar: (Alimento, Double) -> Unit
) {
    val alimentos by repository.obtenerAlimentos().collectAsState(initial = emptyList())
    var seleccionado by remember { mutableStateOf<Alimento?>(null) }
    var gramos by remember { mutableStateOf("") }
    var busqueda by remember { mutableStateOf("") }

    val filtrados = alimentos.filter { it.nombre.contains(busqueda, ignoreCase = true) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Añadir ingrediente") },
        text = {
            Column {
                if (seleccionado == null) {
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
                                    .clickable { seleccionado = alimento }
                                    .padding(vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(alimento.emoji)
                                Spacer(Modifier.width(8.dp))
                                Text(alimento.nombre)
                            }
                        }
                        if (filtrados.isEmpty()) {
                            item { Text("Sin resultados. Añádelo primero en 'Mis alimentos'.", color = MaterialTheme.colorScheme.onSurfaceVariant) }
                        }
                    }
                } else {
                    Text("${seleccionado?.emoji} ${seleccionado?.nombre}", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(12.dp))
                    CampoNumerico(gramos, { gramos = it }, "Cantidad")
                }
            }
        },
        confirmButton = {
            if (seleccionado != null) {
                TextButton(
                    enabled = (gramos.replace(",", ".").toDoubleOrNull() ?: 0.0) > 0,
                    onClick = { onConfirmar(seleccionado!!, gramos.replace(",", ".").toDoubleOrNull() ?: 0.0) }
                ) { Text("Añadir") }
            }
        },
        dismissButton = {
            TextButton(onClick = {
                if (seleccionado != null) seleccionado = null else onDismiss()
            }) { Text(if (seleccionado != null) "Atrás" else "Cancelar") }
        }
    )
}
