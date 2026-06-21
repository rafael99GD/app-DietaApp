package com.rafael.dietaapp.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Paleta inspirada en alimentación: verde principal, naranja para energía/kcal
val VerdePrincipal = Color(0xFF2E7D32)
val VerdeClaro = Color(0xFF66BB6A)
val VerdeOscuro = Color(0xFF1B5E20)
val NaranjaAcento = Color(0xFFFF8A50)
val FondoClaro = Color(0xFFF7F8F3)
val SuperficieClara = Color(0xFFFFFFFF)
val FondoOscuro = Color(0xFF121611)
val SuperficieOscura = Color(0xFF1E231C)

private val LightColors = lightColorScheme(
    primary = VerdePrincipal,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFC8E6C9),
    onPrimaryContainer = VerdeOscuro,
    secondary = NaranjaAcento,
    onSecondary = Color.White,
    background = FondoClaro,
    onBackground = Color(0xFF1A1C19),
    surface = SuperficieClara,
    onSurface = Color(0xFF1A1C19),
    surfaceVariant = Color(0xFFE3E8DD),
    error = Color(0xFFBA1A1A)
)

private val DarkColors = darkColorScheme(
    primary = VerdeClaro,
    onPrimary = Color(0xFF003910),
    primaryContainer = VerdeOscuro,
    onPrimaryContainer = Color(0xFFC8E6C9),
    secondary = NaranjaAcento,
    onSecondary = Color(0xFF4A1B00),
    background = FondoOscuro,
    onBackground = Color(0xFFE2E3DD),
    surface = SuperficieOscura,
    onSurface = Color(0xFFE2E3DD),
    surfaceVariant = Color(0xFF424940),
    error = Color(0xFFFFB4AB)
)

@Composable
fun DietaAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColors else LightColors
    MaterialTheme(
        colorScheme = colorScheme,
        typography = DietaTypography,
        content = content
    )
}
