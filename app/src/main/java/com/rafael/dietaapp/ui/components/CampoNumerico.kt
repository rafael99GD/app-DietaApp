package com.rafael.dietaapp.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType

@Composable
fun CampoNumerico(
    valor: String,
    onValorCambia: (String) -> Unit,
    etiqueta: String,
    sufijo: String = "g",
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = valor,
        onValueChange = { nuevo ->
            // Solo permite números y un punto/coma decimal
            if (nuevo.isEmpty() || nuevo.matches(Regex("^\\d*[.,]?\\d*$"))) {
                onValorCambia(nuevo)
            }
        },
        label = { Text(etiqueta) },
        suffix = { Text(sufijo) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        singleLine = true,
        modifier = modifier.fillMaxWidth()
    )
}

fun String.aDouble(): Double = this.replace(",", ".").toDoubleOrNull() ?: 0.0
