package com.rafael.dietaapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.rafael.dietaapp.data.model.LineaAlimento
import com.rafael.dietaapp.data.model.RecetaDetallada
import com.rafael.dietaapp.data.repository.DietaRepository
import com.rafael.dietaapp.ui.components.ResumenNutrientesCard
import com.rafael.dietaapp.ui.components.formato
import com.rafael.dietaapp.util.ExportUtils
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecetaDetalleScreen(
    recetaId: Long,
    repository: DietaRepository,
    onVolver: () -> Unit,
    onEditarReceta: (Long) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val detalle by repository.obtenerRecetaDetallada(recetaId).collectAsState(
        initial = RecetaDetallada(com.rafael.dietaapp.data.entities.Receta(nombre = ""), emptyList())
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(detalle.receta.nombre) },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        onEditarReceta(recetaId)
                    }) {
                        Icon(Icons.Default.Edit, contentDescription = "Editar receta")
                    }
                    IconButton(onClick = {
                        ExportUtils.compartirReceta(context, detalle)
                    }) {
                        Icon(Icons.Default.Share, contentDescription = "Compartir")
                    }
                    IconButton(onClick = {
                        scope.launch {
                            repository.eliminarReceta(detalle.receta)
                            onVolver()
                        }
                    }) {
                        Icon(Icons.Default.Delete, contentDescription = "Eliminar receta")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding).fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                ResumenNutrientesCard(totales = detalle.totales, titulo = "Total de la receta")
            }

            if (detalle.receta.notas.isNotBlank()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f))
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            Text("Preparación / Notas", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                            Spacer(Modifier.height(4.dp))
                            Text(detalle.receta.notas, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }

            item {
                Text("Ingredientes", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            }
            items(detalle.ingredientes, key = { it.comidaAlimentoId }) { linea ->
                FilaIngrediente(linea)
            }
            if (detalle.ingredientes.isEmpty()) {
                item {
                    Text("Esta receta no tiene ingredientes.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}

@Composable
fun FilaIngrediente(linea: LineaAlimento) {
    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
        Row(
            Modifier.padding(12.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(linea.alimento.emoji, style = MaterialTheme.typography.titleLarge)
                Spacer(Modifier.width(10.dp))
                Text(linea.alimento.nombre, fontWeight = FontWeight.SemiBold)
            }
            Text("${linea.gramos.formato()} g", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
