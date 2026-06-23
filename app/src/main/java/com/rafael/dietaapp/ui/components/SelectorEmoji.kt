package com.rafael.dietaapp.ui.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage

val EMOJIS_COMIDA = listOf(
    // Frutas
    "🍎", "🍏", "🍐", "🍊", "🍋", "🍌", "🍉", "🍇", "🍓", "🫐", "🍈", "🍒", "🍑", "🥭", "🍍", "🥥", "🥝",
    // Verduras
    "🍅", "🍆", "🥑", "🥦", "🥬", "🥒", "🌶️", "🫑", "🌽", "🥕", "🫒", "🧄", "🧅", "🍄", "🥜", "🫘", "🌰",
    // Panadería y Cereales
    "🍞", "🥐", "🥖", "🫓", "🥨", "🥯", "🥞", "🧇", "🧀", "🍖", "🍗", "🥩", "🥓", "🍔", "🍟", "🍕", "🌭", "🥪", "🌮", "🌯", "🫔", "🥙", "🧆", "🍳", "🥘", "🍲", "🫕", "🥣", "🥗", "🍿", "🧈", "🧂", "🥫",
    // Comida Oriental
    "🍱", "🍘", "🍙", "🍚", "🍛", "🍜", "🍝", "🍠", "🍢", "🍣", "🍤", "🍥", "🥮", "🍡", "🥟", "🥠", "🥡",
    // Dulces y Postres
    "🍦", "🍧", "🍨", "🍩", "🍪", "🎂", "🍰", "🧁", "🥧", "🍫", "🍬", "🍭", "🍮", "🍯",
    // Bebidas
    "🍼", "🥛", "☕", "🫖", "🍵", "🍶", "🍾", "🍷", "🍸", "🍹", "🍺", "🍻", "🥂", "🥃", "🥤", "🧋", "🧃", "🧉", "🧊"
)

@Composable
fun SelectorEmojiOImagen(
    emojiSeleccionado: String,
    imagenUri: String?,
    onEmojiSeleccionado: (String) -> Unit,
    onImagenSeleccionada: (Uri?) -> Unit,
    modifier: Modifier = Modifier
) {
    var mostrarSelector by remember { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) onImagenSeleccionada(uri)
    }

    Box(
        modifier = modifier
            .size(80.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primaryContainer)
            .clickable { mostrarSelector = true },
        contentAlignment = Alignment.Center
    ) {
        if (imagenUri != null) {
            AsyncImage(
                model = imagenUri,
                contentDescription = "Imagen elegida",
                modifier = Modifier.fillMaxSize().clip(CircleShape),
                contentScale = ContentScale.Crop
            )
        } else {
            Text(emojiSeleccionado, style = MaterialTheme.typography.headlineLarge)
        }
    }

    if (mostrarSelector) {
        Dialog(onDismissRequest = { mostrarSelector = false }) {
            Card(shape = RoundedCornerShape(20.dp)) {
                Column(Modifier.padding(16.dp)) {
                    Text("Elige un emoticono o una foto", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(12.dp))

                    OutlinedButton(
                        onClick = {
                            launcher.launch("image/*")
                            mostrarSelector = false
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.PhotoCamera, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Elegir foto de la galería")
                    }

                    Spacer(Modifier.height(16.dp))
                    Text("O elige un emoticono:", style = MaterialTheme.typography.labelLarge)
                    Spacer(Modifier.height(8.dp))

                    val gridState = rememberLazyGridState()
                    val scrollbarColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)

                    Box(modifier = Modifier.heightIn(max = 280.dp)) {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(6),
                            state = gridState,
                            modifier = Modifier
                                .fillMaxWidth()
                                .drawWithContent {
                                    drawContent()
                                    // Dibujar barra de scroll
                                    val layoutInfo = gridState.layoutInfo
                                    val viewportSize = layoutInfo.viewportEndOffset - layoutInfo.viewportStartOffset
                                    val totalItemsCount = layoutInfo.totalItemsCount
                                    if (totalItemsCount > 0) {
                                        val itemsInViewport = layoutInfo.visibleItemsInfo.size
                                        val scrollbarHeight = (itemsInViewport.toFloat() / totalItemsCount.toFloat()) * size.height
                                        val firstVisibleItemIndex = gridState.firstVisibleItemIndex
                                        val scrollbarOffsetY = (firstVisibleItemIndex.toFloat() / totalItemsCount.toFloat()) * size.height
                                        
                                        drawRect(
                                            color = scrollbarColor,
                                            topLeft = Offset(size.width - 4.dp.toPx(), scrollbarOffsetY),
                                            size = Size(4.dp.toPx(), scrollbarHeight)
                                        )
                                    }
                                }
                        ) {
                            items(EMOJIS_COMIDA) { emoji ->
                                Box(
                                    modifier = Modifier
                                        .padding(4.dp)
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .clickable {
                                            onEmojiSeleccionado(emoji)
                                            onImagenSeleccionada(null)
                                            mostrarSelector = false
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(emoji, style = MaterialTheme.typography.titleLarge)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
