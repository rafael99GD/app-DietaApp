package com.rafael.dietaapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.rafael.dietaapp.data.entities.Extra
import com.rafael.dietaapp.data.repository.DietaRepository
import com.rafael.dietaapp.ui.components.CampoNumerico
import com.rafael.dietaapp.ui.components.SelectorEmojiOImagen
import com.rafael.dietaapp.ui.components.aDouble
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.first

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExtraFormScreen(
    fecha: String,
    extraId: Long?,
    repository: DietaRepository,
    onVolver: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val esEdicion = extraId != null

    var nombre by remember { mutableStateOf("") }
    var emoji by remember { mutableStateOf("🍴") }
    var kcal by remember { mutableStateOf("") }
    var grasas by remember { mutableStateOf("") }
    var grasasSaturadas by remember { mutableStateOf("") }
    var hidratos by remember { mutableStateOf("") }
    var azucares by remember { mutableStateOf("") }
    var proteinas by remember { mutableStateOf("") }
    var sal by remember { mutableStateOf("") }
    var extraOriginal by remember { mutableStateOf<Extra?>(null) }
    var cargado by remember { mutableStateOf(!esEdicion) }
    val focusManager = LocalFocusManager.current

    LaunchedEffect(extraId) {
        if (extraId != null) {
            // Buscamos el extra dentro de los extras del día (no hay obtenerPorId directo, así que filtramos)
            val extras = repository.obtenerDiaDetallado(fecha)
            // Nos basta con una lectura puntual; usamos first() para no quedarnos suscritos.
            val detalle = extras.first()
            val encontrado = detalle.extras.find { it.id == extraId }
            if (encontrado != null) {
                extraOriginal = encontrado
                nombre = encontrado.nombre
                emoji = encontrado.emoji
                kcal = encontrado.kcal.toString()
                grasas = encontrado.grasas.toString()
                grasasSaturadas = encontrado.grasasSaturadas.toString()
                hidratos = encontrado.hidratos.toString()
                azucares = encontrado.azucares.toString()
                proteinas = encontrado.proteinas.toString()
                sal = encontrado.sal.toString()
            }
            cargado = true
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (esEdicion) "Editar extra" else "Comida fuera / Extra") },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    if (esEdicion) {
                        IconButton(onClick = {
                            scope.launch {
                                extraOriginal?.let { repository.eliminarExtra(it) }
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
                .imePadding() // Añade espacio dinámico según el teclado
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                "Para cosas que comes fuera y no sabes la composición exacta: pon una estimación de lo que crees que llevaba en total.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                SelectorEmojiOImagen(
                    emojiSeleccionado = emoji,
                    imagenUri = null,
                    onEmojiSeleccionado = { emoji = it },
                    onImagenSeleccionada = { }
                )
            }

            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("¿Qué has comido?") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
                modifier = Modifier.fillMaxWidth()
            )

            Text("Estimación nutricional (total de lo comido, no por 100g)", style = MaterialTheme.typography.titleMedium)

            CampoNumerico(kcal, { kcal = it }, "Kcal", sufijo = "kcal", onAction = { focusManager.moveFocus(FocusDirection.Down) })
            CampoNumerico(grasas, { grasas = it }, "Grasas", onAction = { focusManager.moveFocus(FocusDirection.Down) })
            CampoNumerico(grasasSaturadas, { grasasSaturadas = it }, "  de las cuales saturadas", onAction = { focusManager.moveFocus(FocusDirection.Down) })
            CampoNumerico(hidratos, { hidratos = it }, "Hidratos de carbono", onAction = { focusManager.moveFocus(FocusDirection.Down) })
            CampoNumerico(azucares, { azucares = it }, "  de los cuales azúcares", onAction = { focusManager.moveFocus(FocusDirection.Down) })
            CampoNumerico(proteinas, { proteinas = it }, "Proteínas", onAction = { focusManager.moveFocus(FocusDirection.Down) })
            CampoNumerico(sal, { sal = it }, "Sal", imeAction = ImeAction.Done, onAction = { focusManager.clearFocus() })

            Spacer(Modifier.height(8.dp))

            Button(
                onClick = {
                    scope.launch {
                        val nuevo = Extra(
                            id = extraId ?: 0,
                            diaFecha = fecha,
                            nombre = nombre.trim(),
                            emoji = emoji,
                            kcal = kcal.aDouble(),
                            grasas = grasas.aDouble(),
                            grasasSaturadas = grasasSaturadas.aDouble(),
                            hidratos = hidratos.aDouble(),
                            azucares = azucares.aDouble(),
                            proteinas = proteinas.aDouble(),
                            sal = sal.aDouble()
                        )
                        if (esEdicion) {
                            repository.actualizarExtra(nuevo)
                        } else {
                            repository.agregarExtra(nuevo)
                        }
                        onVolver()
                    }
                },
                enabled = nombre.isNotBlank(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (esEdicion) "Guardar cambios" else "Añadir")
            }

            // Espacio extra al final para que los campos de abajo puedan subir por encima del teclado
            Spacer(Modifier.height(300.dp))
        }
    }
}
