package com.rafael.dietaapp.ui.screens

import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
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
    val focusManager = LocalFocusManager.current

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
                .imePadding() // Añade espacio dinámico según el teclado
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
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
                modifier = Modifier.fillMaxWidth()
            )

            var unidadMedida by remember { mutableStateOf("g") }

            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        "Info por cada 100$unidadMedida",
                        style = MaterialTheme.typography.titleSmall,
                        modifier = Modifier.weight(1f)
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.wrapContentWidth()
                    ) {
                        Text("g", style = MaterialTheme.typography.bodySmall)
                        Switch(
                            checked = unidadMedida == "ml",
                            onCheckedChange = { unidadMedida = if (it) "ml" else "g" },
                            modifier = Modifier.scale(0.8f).padding(horizontal = 4.dp)
                        )
                        Text("ml", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }

            CampoNumerico(kcal, { kcal = it }, "Kcal", sufijo = "kcal", onAction = { focusManager.moveFocus(FocusDirection.Down) })
            CampoNumerico(grasas, { grasas = it }, "Grasas", sufijo = unidadMedida, onAction = { focusManager.moveFocus(FocusDirection.Down) })
            CampoNumerico(grasasSaturadas, { grasasSaturadas = it }, "  de las cuales saturadas", sufijo = unidadMedida, onAction = { focusManager.moveFocus(FocusDirection.Down) })
            CampoNumerico(hidratos, { hidratos = it }, "Hidratos de carbono", sufijo = unidadMedida, onAction = { focusManager.moveFocus(FocusDirection.Down) })
            CampoNumerico(azucares, { azucares = it }, "  de los cuales azúcares", sufijo = unidadMedida, onAction = { focusManager.moveFocus(FocusDirection.Down) })
            CampoNumerico(proteinas, { proteinas = it }, "Proteínas", sufijo = unidadMedida, onAction = { focusManager.moveFocus(FocusDirection.Down) })
            CampoNumerico(sal, { sal = it }, "Sal", sufijo = unidadMedida, imeAction = ImeAction.Done, onAction = { focusManager.clearFocus() })

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

            // Espacio extra al final para que los campos de abajo puedan subir por encima del teclado
            Spacer(Modifier.height(300.dp))
        }
    }
}
