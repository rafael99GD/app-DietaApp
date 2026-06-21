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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerfilScreen(onVolver: () -> Unit) {
    val context = LocalContext.current
    val app = context.applicationContext as DietaApplication
    val userPrefs = app.userPreferences

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
            
            Text(
                "Establece tus objetivos diarios para ver tu progreso en la pantalla principal.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            
            Spacer(Modifier.height(8.dp))
            
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
