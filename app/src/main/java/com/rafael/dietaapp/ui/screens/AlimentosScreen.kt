package com.rafael.dietaapp.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.rafael.dietaapp.data.entities.Alimento
import com.rafael.dietaapp.data.repository.DietaRepository
import com.rafael.dietaapp.ui.components.formato
import com.rafael.dietaapp.util.ExportUtils
import com.rafael.dietaapp.util.FuzzySearch
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlimentosScreen(
    repository: DietaRepository,
    onVolver: () -> Unit,
    onEditarAlimento: (Long) -> Unit,
    onNuevoAlimento: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var busqueda by remember { mutableStateOf("") }
    var mostrarBuscador by remember { mutableStateOf(false) }

    val alimentosRaw by repository.obtenerAlimentos().collectAsState(initial = emptyList())
    
    val alimentos = remember(busqueda, alimentosRaw) {
        if (busqueda.isBlank()) alimentosRaw 
        else alimentosRaw.filter { FuzzySearch.coincide(busqueda, it.nombre) }
    }

    var seleccionados by remember { mutableStateOf(setOf<Long>()) }
    val modoSeleccion = seleccionados.isNotEmpty()

    BackHandler(enabled = modoSeleccion || mostrarBuscador) {
        if (modoSeleccion) seleccionados = emptySet()
        else if (mostrarBuscador) {
            mostrarBuscador = false
            busqueda = ""
        }
    }

    Scaffold(
        topBar = {
            if (modoSeleccion) {
                TopAppBar(
                    title = { Text("${seleccionados.size} seleccionados") },
                    navigationIcon = {
                        IconButton(onClick = { seleccionados = emptySet() }) {
                            Icon(Icons.Default.Close, contentDescription = "Cancelar")
                        }
                    },
                    actions = {
                        IconButton(onClick = {
                            if (seleccionados.size == alimentosRaw.size) {
                                seleccionados = emptySet()
                            } else {
                                seleccionados = alimentosRaw.map { it.id }.toSet()
                            }
                        }) {
                            Icon(
                                if (seleccionados.size == alimentosRaw.size) Icons.Default.CheckBox else Icons.Default.CheckBoxOutlineBlank,
                                contentDescription = "Seleccionar todos"
                            )
                        }
                        IconButton(onClick = {
                            val aCompartir = alimentosRaw.filter { it.id in seleccionados }
                            ExportUtils.compartirAlimentos(context, aCompartir)
                            seleccionados = emptySet()
                        }) {
                            Icon(Icons.Default.Share, contentDescription = "Compartir seleccionados")
                        }
                        IconButton(onClick = {
                            scope.launch {
                                alimentosRaw.filter { it.id in seleccionados }.forEach {
                                    repository.eliminarAlimento(it)
                                }
                                seleccionados = emptySet()
                            }
                        }) {
                            Icon(Icons.Default.Delete, contentDescription = "Eliminar seleccionados")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                )
            } else if (mostrarBuscador) {
                TopAppBar(
                    title = {
                        TextField(
                            value = busqueda,
                            onValueChange = { busqueda = it },
                            placeholder = { Text("Buscar alimento...") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = TextFieldDefaults.colors(
                                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                                unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                disabledContainerColor = Color.Transparent,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent
                            ),
                            singleLine = true
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { 
                            mostrarBuscador = false
                            busqueda = ""
                        }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Cerrar búsqueda")
                        }
                    },
                    actions = {
                        if (busqueda.isNotEmpty()) {
                            IconButton(onClick = { busqueda = "" }) {
                                Icon(Icons.Default.Close, contentDescription = "Borrar")
                            }
                        }
                    }
                )
            } else {
                TopAppBar(
                    title = { Text("Mis alimentos") },
                    navigationIcon = {
                        IconButton(onClick = onVolver) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                        }
                    },
                    actions = {
                        IconButton(onClick = { mostrarBuscador = true }) {
                            Icon(Icons.Default.Search, contentDescription = "Buscar")
                        }
                    }
                )
            }
        },
        floatingActionButton = {
            if (!modoSeleccion) {
                FloatingActionButton(onClick = onNuevoAlimento) {
                    Icon(Icons.Default.Add, contentDescription = "Añadir alimento")
                }
            }
        }
    ) { padding ->
        Column(Modifier.padding(padding).fillMaxSize()) {
            if (alimentos.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        if (busqueda.isBlank())
                            "Aún no has guardado ningún alimento.\nPulsa + para añadir el primero."
                        else "No se ha encontrado ningún alimento que coincida.",
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(alimentos, key = { it.id }) { alimento ->
                        TarjetaAlimento(
                            alimento = alimento, 
                            estaSeleccionado = alimento.id in seleccionados,
                            onClick = {
                                if (modoSeleccion) {
                                    seleccionados = if (alimento.id in seleccionados) seleccionados - alimento.id else seleccionados + alimento.id
                                } else {
                                    onEditarAlimento(alimento.id)
                                }
                            },
                            onLongClick = {
                                if (!modoSeleccion) seleccionados = setOf(alimento.id)
                            },
                            onShare = { ExportUtils.compartirAlimento(context, alimento) }
                        )
                    }
                    item { Spacer(Modifier.height(80.dp)) }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TarjetaAlimento(
    alimento: Alimento, 
    estaSeleccionado: Boolean,
    onClick: () -> Unit, 
    onLongClick: () -> Unit,
    onShare: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            ),
        shape = RoundedCornerShape(14.dp),
        colors = if (estaSeleccionado) 
            CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f))
        else 
            CardDefaults.cardColors()
    ) {
        Row(
            Modifier.padding(12.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(contentAlignment = Alignment.BottomEnd) {
                if (alimento.imagenUri != null) {
                    AsyncImage(
                        model = alimento.imagenUri,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp).clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        Modifier.size(48.dp).clip(CircleShape).background(MaterialTheme.colorScheme.surfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(alimento.emoji, style = MaterialTheme.typography.headlineSmall)
                    }
                }
                
                if (estaSeleccionado) {
                    Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp).background(MaterialTheme.colorScheme.primary, CircleShape).padding(2.dp)
                    )
                }
            }
            
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(alimento.nombre, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Text(
                    "${alimento.kcal.formato()} kcal / 100g",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            if (!estaSeleccionado) {
                IconButton(onClick = onShare) {
                    Icon(Icons.Default.Share, contentDescription = "Compartir")
                }
            }
        }
    }
}
