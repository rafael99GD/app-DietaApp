package com.rafael.dietaapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.rafael.dietaapp.data.entities.Receta
import com.rafael.dietaapp.data.repository.DietaRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecetasScreen(
    repository: DietaRepository,
    onVolver: () -> Unit,
    onAbrirReceta: (Long) -> Unit,
    onNuevaReceta: () -> Unit
) {
    val recetas by repository.obtenerRecetas().collectAsState(initial = emptyList())

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis recetas") },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onNuevaReceta) {
                Icon(Icons.Default.Add, contentDescription = "Nueva receta")
            }
        }
    ) { padding ->
        if (recetas.isEmpty()) {
            Box(Modifier.padding(padding).fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    "Aún no tienes recetas.\nPuedes crear una desde aquí, o guardar una comida ya hecha como receta.",
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
                items(recetas, key = { it.id }) { receta ->
                    TarjetaReceta(receta, onClick = { onAbrirReceta(receta.id) })
                }
                item { Spacer(Modifier.height(80.dp)) }
            }
        }
    }
}

@Composable
fun TarjetaReceta(receta: Receta, onClick: () -> Unit) {
    Card(onClick = onClick, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp)) {
        Row(Modifier.padding(12.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            if (receta.fotoUri != null) {
                AsyncImage(
                    model = receta.fotoUri,
                    contentDescription = null,
                    modifier = Modifier.size(56.dp).clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(Modifier.size(56.dp).clip(CircleShape), contentAlignment = Alignment.Center) {
                    Text(receta.emoji, style = MaterialTheme.typography.headlineMedium)
                }
            }
            Spacer(Modifier.width(12.dp))
            Text(receta.nombre, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        }
    }
}
