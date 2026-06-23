package com.rafael.dietaapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.rafael.dietaapp.DietaApplication
import com.rafael.dietaapp.R
import com.rafael.dietaapp.util.AppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerfilScreen(onVolver: () -> Unit) {
    val context = LocalContext.current
    val app = context.applicationContext as DietaApplication
    val userPrefs = app.userPreferences

    val theme by userPrefs.theme

    var kcalGoal by remember { mutableStateOf(userPrefs.kcalGoal.value.toString()) }
    var proteinasGoal by remember { mutableStateOf(userPrefs.proteinasGoal.value.toString()) }
    var carbsGoal by remember { mutableStateOf(userPrefs.carbsGoal.value.toString()) }
    var grasasGoal by remember { mutableStateOf(userPrefs.grasasGoal.value.toString()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Configuración") },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Logo grande en el perfil
            Icon(
                painter = painterResource(id = R.drawable.logo_dieta),
                contentDescription = null,
                modifier = Modifier
                    .size(100.dp)
                    .clip(MaterialTheme.shapes.medium),
                tint = Color.Unspecified
            )
            
            Text("Ajustes de Usuario", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            
            // Sección de Tema
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
            ) {
                Column(Modifier.padding(16.dp)) {
                    val currentTheme by userPrefs.theme
                    Text("Tema de la aplicación", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ThemeButton("Claro", com.rafael.dietaapp.util.AppTheme.LIGHT, currentTheme, Modifier.weight(1f)) { userPrefs.saveTheme(it) }
                        ThemeButton("Oscuro", com.rafael.dietaapp.util.AppTheme.DARK, currentTheme, Modifier.weight(1f)) { userPrefs.saveTheme(it) }
                        ThemeButton("Sistema", com.rafael.dietaapp.util.AppTheme.SYSTEM, currentTheme, Modifier.weight(1f)) { userPrefs.saveTheme(it) }
                    }
                }
            }

            Text(
                "Objetivos Diarios",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth()
            )
            
            GoalField("Calorías diarias (kcal)", kcalGoal) { kcalGoal = it }
            GoalField("Proteínas (g)", proteinasGoal) { proteinasGoal = it }
            GoalField("Carbohidratos (g)", carbsGoal) { carbsGoal = it }
            GoalField("Grasas (g)", grasasGoal) { grasasGoal = it }
            
            Spacer(Modifier.height(24.dp))
            
            Button(
                onClick = {
                    userPrefs.saveGoals(
                        kcalGoal.toDoubleOrNull() ?: 2000.0,
                        proteinasGoal.toDoubleOrNull() ?: 150.0,
                        carbsGoal.toDoubleOrNull() ?: 250.0,
                        grasasGoal.toDoubleOrNull() ?: 70.0
                    )
                    onVolver()
                },
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.large
            ) {
                Icon(Icons.Default.Save, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Guardar Objetivos")
            }
        }
    }
}

@Composable
fun ThemeButton(
    label: String,
    themeValue: com.rafael.dietaapp.util.AppTheme,
    currentTheme: com.rafael.dietaapp.util.AppTheme,
    modifier: Modifier = Modifier,
    onClick: (com.rafael.dietaapp.util.AppTheme) -> Unit
) {
    val isSelected = themeValue == currentTheme
    OutlinedButton(
        onClick = { onClick(themeValue) },
        modifier = modifier,
        colors = if (isSelected) 
            ButtonDefaults.outlinedButtonColors(containerColor = MaterialTheme.colorScheme.primary, contentColor = MaterialTheme.colorScheme.onPrimary)
        else 
            ButtonDefaults.outlinedButtonColors(),
        border = if (isSelected) null else ButtonDefaults.outlinedButtonBorder
    ) {
        Text(label, style = MaterialTheme.typography.labelSmall)
    }
}

@Composable
fun GoalField(label: String, value: String, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        singleLine = true
    )
}
