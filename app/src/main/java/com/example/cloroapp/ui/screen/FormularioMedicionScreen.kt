package com.example.cloroapp.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.cloroapp.data.Repositorio
import com.example.cloroapp.data.model.Granja
import com.example.cloroapp.data.model.PuntoMedicion
import com.example.cloroapp.ui.theme.AzulPrincipal
import com.example.cloroapp.ui.theme.EstadoAdvertencia
import com.example.cloroapp.ui.theme.EstadoNormal
import com.example.cloroapp.ui.theme.FondoClaro
import com.example.cloroapp.ui.theme.TextoOscuro
import com.example.cloroapp.ui.theme.TextoSecundario
import kotlinx.coroutines.launch

@Composable
fun FormularioMedicionScreen(
    granja: Granja,
    punto: PuntoMedicion,
    onVolver: () -> Unit
) {
    val scope = rememberCoroutineScope()
    var texto by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    var resultado by remember { mutableStateOf<String?>(null) }
    var enviando by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FondoClaro)
            .padding(16.dp)
    ) {
        TextButton(onClick = onVolver) {
            Text("← Volver", color = AzulPrincipal)
        }
        Text(
            text = "Registrar medición",
            style = MaterialTheme.typography.headlineMedium,
            color = TextoOscuro
        )
        Text(
            text = "${granja.nombre} · ${punto.nombre}",
            style = MaterialTheme.typography.bodyMedium,
            color = TextoSecundario
        )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = texto,
            onValueChange = {
                texto = it
                error = null
                resultado = null
            },
            label = { Text("Cloro libre (mg/L)") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            isError = error != null,
            modifier = Modifier.fillMaxWidth()
        )

        error?.let {
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = it, style = MaterialTheme.typography.bodySmall, color = EstadoAdvertencia)
        }
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                val valor = texto.replace(',', '.').toDoubleOrNull()
                if (valor == null) {
                    error = "Escribe un número, por ejemplo 3.50"
                } else if (valor < 0 || valor > 20) {
                    error = "El valor debe estar entre 0 y 20 mg/L"
                } else {
                    scope.launch {
                        enviando = true
                        Repositorio.registrarMedicion(punto.id, valor)
                            .onSuccess { r ->
                                texto = ""
                                resultado = if (r.pendiente) {
                                    "Sin conexión: la medición quedó guardada en el celular y se enviará cuando vuelva internet."
                                } else {
                                    "Medición registrada: ${r.estado?.texto() ?: ""}" +
                                            if (r.hayAlerta) ". Se generó una alerta." else ""
                                }
                            }
                            .onFailure { error = it.message }
                        enviando = false
                    }
                }
            },
            enabled = !enviando,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (enviando) "Enviando..." else "Guardar medición")
        }

        resultado?.let {
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = it, style = MaterialTheme.typography.labelLarge, color = EstadoNormal)
        }
    }
}