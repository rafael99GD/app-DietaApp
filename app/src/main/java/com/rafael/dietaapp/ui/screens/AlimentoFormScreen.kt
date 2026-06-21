package com.rafael.dietaapp.ui.screens

import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.rafael.dietaapp.data.entities.Alimento
import com.rafael.dietaapp.data.repository.DietaRepository
import com.rafael.dietaapp.ui.components.CampoNumerico
import com.rafael.dietaapp.ui.components.SelectorEmojiOImagen
import com.rafael.dietaapp.ui.components.aDouble
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlimentoFormScreen(
    alimentoId: Long?,
    repository: DietaRepository,
    onVolver: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val esEdicion = alimentoId != null

    var nombre by remember { mutableStateOf("") }
    var emoji by remember { mutableStateOf("🍽️") }
    var imagenUri by remember { mutableStateOf<String?>(null) }
    var kcal by remember { mutableStateOf("") }
    var grasas by remember { mutableStateOf("") }
    var grasasSaturadas by remember { mutableStateOf("") }
    var hidratos by remember { mutableStateOf("") }
    var azucares by remember { mutableStateOf("") }
    var proteinas by remember { mutableStateOf("") }
    var sal by remember { mutableStateOf("") }
    var cargado by remember { mutableStateOf(!esEdicion) }
    var alimentoOriginal by remember { mutableStateOf<Alimento?>(null) }

    LaunchedEffect(alimentoId) {
        if (alimentoId != null) {
            val a = repository.obtenerAlimento(alimentoId)
            if (a != null) {
                alimentoOriginal = a
                nombre = a.nombre
                emoji = a.emoji
                imagenUri = a.imagenUri
                kcal = a.kcal.toString()
                grasas = a.grasas.toString()
                grasasSaturadas = a.grasasSaturadas.toString()
                hidratos = a.hidratos.toString()
                azucares = a.azucares.toString()
                proteinas = a.proteinas.toString()
                sal = a.sal.toString()
            }
            cargado = true
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (esEdicion) "Editar alimento" else "Nuevo alimento") },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    if (esEdicion) {
                        IconButton(onClick = {
                            scope.launch {
                                alimentoOriginal?.let { repository.eliminarAlimento(it) }
                                onVolver()
                            }
                        }) {
                            Icon(Icons.Default.Delete, contentDescription = "Eliminar")
                        }
                    }
                }
            )
        }
    ) { padding ->
        if (!cargado) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }

        Column(
            Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                SelectorEmojiOImagen(
                    emojiSeleccionado = emoji,
                    imagenUri = imagenUri,
                    onEmojiSeleccionado = { emoji = it },
                    onImagenSeleccionada = { uri: Uri? -> imagenUri = uri?.toString() }
                )
            }

            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre del alimento") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Text("Información nutricional (por cada 100g)", style = MaterialTheme.typography.titleMedium)

            CampoNumerico(kcal, { kcal = it }, "Kcal", sufijo = "kcal")
            CampoNumerico(grasas, { grasas = it }, "Grasas")
            CampoNumerico(grasasSaturadas, { grasasSaturadas = it }, "  de las cuales saturadas")
            CampoNumerico(hidratos, { hidratos = it }, "Hidratos de carbono")
            CampoNumerico(azucares, { azucares = it }, "  de los cuales azúcares")
            CampoNumerico(proteinas, { proteinas = it }, "Proteínas")
            CampoNumerico(sal, { sal = it }, "Sal")

            Spacer(Modifier.height(8.dp))

            Button(
                onClick = {
                    scope.launch {
                        val nuevoAlimento = Alimento(
                            id = alimentoId ?: 0,
                            nombre = nombre.trim(),
                            emoji = emoji,
                            imagenUri = imagenUri,
                            kcal = kcal.aDouble(),
                            grasas = grasas.aDouble(),
                            grasasSaturadas = grasasSaturadas.aDouble(),
                            hidratos = hidratos.aDouble(),
                            azucares = azucares.aDouble(),
                            proteinas = proteinas.aDouble(),
                            sal = sal.aDouble()
                        )
                        if (esEdicion) {
                            repository.actualizarAlimento(nuevoAlimento)
                        } else {
                            repository.guardarAlimento(nuevoAlimento)
                        }
                        onVolver()
                    }
                },
                enabled = nombre.isNotBlank(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (esEdicion) "Guardar cambios" else "Añadir alimento")
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}
