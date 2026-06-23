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
import com.rafael.dietaapp.data.model.RecetaDetallada
import com.rafael.dietaapp.data.repository.DietaRepository
import com.rafael.dietaapp.ui.components.formato
import com.rafael.dietaapp.util.ExportUtils
import com.rafael.dietaapp.util.FuzzySearch
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecetasScreen(
    repository: DietaRepository,
    onVolver: () -> Unit,
    onAbrirReceta: (Long) -> Unit,
    onNuevaReceta: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    
    var busqueda by remember { mutableStateOf("") }
    var mostrarBuscador by remember { mutableStateOf(false) }

    val recetasRaw by repository.obtenerRecetasConDetalle().collectAsState(initial = emptyList())
    
    val recetasDetalladas = remember(busqueda, recetasRaw) {
        if (busqueda.isBlank()) recetasRaw 
        else recetasRaw.filter { FuzzySearch.coincide(busqueda, it.receta.nombre) }
    }

    var seleccionadas by remember { mutableStateOf(setOf<Long>()) }
    val modoSeleccion = seleccionadas.isNotEmpty()

    BackHandler(enabled = modoSeleccion || mostrarBuscador) {
        if (modoSeleccion) seleccionadas = emptySet()
        else if (mostrarBuscador) {
            mostrarBuscador = false
            busqueda = ""
        }
    }

    Scaffold(
        topBar = {
            if (modoSeleccion) {
                TopAppBar(
                    title = { Text("${seleccionadas.size} seleccionadas") },
                    navigationIcon = {
                        IconButton(onClick = { seleccionadas = emptySet() }) {
                            Icon(Icons.Default.Close, contentDescription = "Cancelar")
                        }
                    },
                    actions = {
                        IconButton(onClick = {
                            if (seleccionadas.size == recetasRaw.size) {
                                seleccionadas = emptySet()
                            } else {
                                seleccionadas = recetasRaw.map { it.receta.id }.toSet()
                            }
                        }) {
                            Icon(
                                if (seleccionadas.size == recetasRaw.size) Icons.Default.CheckBox else Icons.Default.CheckBoxOutlineBlank,
                                contentDescription = "Seleccionar todas"
                            )
                        }
                        IconButton(onClick = {
                            val aCompartir = recetasRaw.filter { it.receta.id in seleccionadas }
                            ExportUtils.compartirRecetas(context, aCompartir)
                            seleccionadas = emptySet()
                        }) {
                            Icon(Icons.Default.Share, contentDescription = "Compartir seleccionadas")
                        }
                        IconButton(onClick = {
                            scope.launch {
                                recetasRaw.filter { it.receta.id in seleccionadas }.forEach {
                                    repository.eliminarReceta(it.receta)
                                }
                                seleccionadas = emptySet()
                            }
                        }) {
                            Icon(Icons.Default.Delete, contentDescription = "Eliminar seleccionadas")
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
                            placeholder = { Text("Buscar receta...") },
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
                    title = { Text("Mis recetas") },
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
                FloatingActionButton(onClick = onNuevaReceta) {
                    Icon(Icons.Default.Add, contentDescription = "Nueva receta")
                }
            }
        }
    ) { padding ->
        if (recetasDetalladas.isEmpty()) {
            Box(Modifier.padding(padding).fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    if (busqueda.isBlank())
                        "Aún no tienes recetas.\nPuedes crear una desde aquí, o guardar una comida ya hecha como receta."
                    else "No se ha encontrado ninguna receta que coincida.",
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(32.dp)
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.padding(padding).fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(recetasDetalladas, key = { it.receta.id }) { detalle ->
                    TarjetaReceta(
                        detalle = detalle, 
                        estaSeleccionada = detalle.receta.id in seleccionadas,
                        onClick = {
                            if (modoSeleccion) {
                                seleccionadas = if (detalle.receta.id in seleccionadas) seleccionadas - detalle.receta.id else seleccionadas + detalle.receta.id
                            } else {
                                onAbrirReceta(detalle.receta.id)
                            }
                        },
                        onLongClick = {
                            if (!modoSeleccion) seleccionadas = setOf(detalle.receta.id)
                        },
                        onShare = { ExportUtils.compartirReceta(context, detalle) }
                    )
                }
                item { Spacer(Modifier.height(80.dp)) }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TarjetaReceta(
    detalle: RecetaDetallada, 
    estaSeleccionada: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    onShare: () -> Unit
) {
    val receta = detalle.receta
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            ),
        shape = RoundedCornerShape(14.dp),
        colors = if (estaSeleccionada) 
            CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f))
        else 
            CardDefaults.cardColors()
    ) {
        Row(Modifier.padding(12.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Box(contentAlignment = Alignment.BottomEnd) {
                if (receta.fotoUri != null) {
                    AsyncImage(
                        model = receta.fotoUri,
                        contentDescription = null,
                        modifier = Modifier.size(56.dp).clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        Modifier.size(56.dp).clip(CircleShape).background(MaterialTheme.colorScheme.surfaceVariant), 
                        contentAlignment = Alignment.Center
                    ) {
                        Text(receta.emoji, style = MaterialTheme.typography.headlineMedium)
                    }
                }
                
                if (estaSeleccionada) {
                    Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(24.dp).background(MaterialTheme.colorScheme.primary, CircleShape).padding(2.dp)
                    )
                }
            }
            
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(receta.nombre, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Text(
                    "${detalle.totales.kcal.formato()} kcal totales",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            if (!estaSeleccionada) {
                IconButton(onClick = onShare) {
                    Icon(Icons.Default.Share, contentDescription = "Compartir")
                }
            }
        }
    }
}
