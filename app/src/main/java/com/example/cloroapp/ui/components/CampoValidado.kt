package com.example.cloroapp.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation

@Composable
fun CampoValidado(
    valor: String,
    onCambio: (String) -> Unit,
    etiqueta: String,
    error: String?,
    modifier: Modifier = Modifier,
    teclado: KeyboardType = KeyboardType.Text,
    esClave: Boolean = false,
    unaLinea: Boolean = true
) {
    OutlinedTextField(
        value = valor,
        onValueChange = onCambio,
        label = { Text(etiqueta) },
        singleLine = unaLinea,
        isError = error != null,
        trailingIcon = {
            if (error != null) {
                Icon(
                    imageVector = Icons.Filled.Error,
                    contentDescription = "Error",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        },
        supportingText = { if (error != null) Text(error) },
        visualTransformation =
            if (esClave) PasswordVisualTransformation() else VisualTransformation.None,
        keyboardOptions = KeyboardOptions(keyboardType = teclado),
        modifier = modifier.fillMaxWidth()
    )
}