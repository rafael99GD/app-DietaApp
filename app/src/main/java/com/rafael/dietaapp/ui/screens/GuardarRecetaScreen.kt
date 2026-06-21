package com.rafael.dietaapp.ui.screens

import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.rafael.dietaapp.data.model.ComidaDetallada
import com.rafael.dietaapp.data.repository.DietaRepository
import com.rafael.dietaapp.ui.components.ResumenNutrientesCard
import com.rafael.dietaapp.ui.components.SelectorEmojiOImagen
import com.rafael.dietaapp.ui.components.formato
import kotlinx.coroutines.launch

/**
 * Pantalla que se abre desde el menú de una comida: "Guardar como receta".
 * Muestra un resumen de todo lo que lleva la comida (alimentos sueltos + recetas ya incluidas,
 * aplanado todo en una lista única de ingredientes) y permite ponerle nombre y foto a la nueva receta.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GuardarRecetaScreen(
    comidaId: Long,
    repository: DietaRepository,
    onVolver: () -> Unit,
    onRecetaGuardada: () -> Unit
) {
    val scope = rememberCoroutineScope()
    var detalle by remember { mutableStateOf<ComidaDetallada?>(null) }
    var nombre by remember { mutableStateOf("") }
    var emoji by remember { mutableStateOf("📖") }
    var imagenUri by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(comidaId) {
        detalle = repository.obtenerComidaDetalladaUnaVez(comidaId)
        nombre = detalle?.comida?.nombre ?: ""
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Guardar como receta") },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        val det = detalle
        if (det == null) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }

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
                    ResumenNutrientesCard(totales = det.totales, titulo = "Total de la receta")
                }

                if (det.lineasRecetas.isNotEmpty()) {
                    item {
                        Text(
                            "Nota: las recetas que ya tenías añadidas en esta comida se incluirán como ingredientes sueltos en la nueva receta.",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                item { Text("Ingredientes que incluirá", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold) }

                items(det.lineasAlimentos) { linea ->
                    FilaIngrediente(linea)
                }
            }

            Button(
                onClick = {
                    scope.launch {
                        repository.guardarComidaComoReceta(
                            comidaId = comidaId,
                            nombreReceta = nombre.trim(),
                            fotoUri = imagenUri,
                            emoji = emoji
                        )
                        onRecetaGuardada()
                    }
                },
                enabled = nombre.isNotBlank(),
                modifier = Modifier.fillMaxWidth().padding(16.dp)
            ) {
                Text("Guardar receta")
            }
        }
    }
}
