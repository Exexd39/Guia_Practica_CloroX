package com.example.cloroapp.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.cloroapp.data.RegistroAcciones
import com.example.cloroapp.data.Repositorio
import com.example.cloroapp.data.model.Granja
import com.example.cloroapp.data.model.PuntoMedicion
import com.example.cloroapp.data.model.RegistroAccion
import com.example.cloroapp.data.model.SesionActual
import com.example.cloroapp.ui.theme.AzulPrincipal
import com.example.cloroapp.ui.theme.Blanco
import com.example.cloroapp.ui.theme.EstadoAdvertencia
import com.example.cloroapp.ui.theme.EstadoNormal
import com.example.cloroapp.ui.theme.FondoClaro
import com.example.cloroapp.ui.theme.TextoOscuro
import com.example.cloroapp.ui.theme.TextoSecundario
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val tiposAccion = listOf(
    "Ajuste de dosificación",
    "Revisión de equipo",
    "Limpieza de red",
    "Otra"
)

@Composable
fun AccionScreen(
    granja: Granja,
    punto: PuntoMedicion,
    onVolver: () -> Unit
) {
    var tipoSeleccionado by remember { mutableStateOf(tiposAccion.first()) }
    var observaciones by remember { mutableStateOf("") }
    var guardado by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    var historial by remember { mutableStateOf<List<RegistroAccion>>(emptyList()) }
    var mensaje by remember { mutableStateOf<String?>(null) }
    var enviando by remember { mutableStateOf(false) }

    LaunchedEffect(punto.id) {
        Repositorio.cargarAcciones(punto.id).onSuccess { historial = it }
    }

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
            text = "Registrar acción",
            style = MaterialTheme.typography.headlineMedium,
            color = TextoOscuro
        )
        Text(
            text = "${granja.nombre} · ${punto.nombre}",
            style = MaterialTheme.typography.bodyMedium,
            color = TextoSecundario
        )
        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Blanco)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Valor actual",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextoSecundario,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "${"%.2f".format(punto.cloroLibre)} mg/L · ${punto.estado.texto()}",
                    style = MaterialTheme.typography.titleMedium,
                    color = punto.estado.color()
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Tipo de acción",
            style = MaterialTheme.typography.titleMedium,
            color = TextoOscuro
        )
        tiposAccion.forEach { tipo ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = tipo == tipoSeleccionado,
                    onClick = { tipoSeleccionado = tipo }
                )
                Text(text = tipo, color = TextoOscuro)
            }
        }
        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = observaciones,
            onValueChange = { observaciones = it },
            label = { Text("Observaciones") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (observaciones.trim().length < 3) {
                    guardado = false
                    mensaje = "Escribe al menos 3 caracteres en las observaciones"
                } else {
                    scope.launch {
                        enviando = true
                        Repositorio.registrarAccion(punto.id, tipoSeleccionado, observaciones.trim())
                            .onSuccess {
                                observaciones = ""
                                guardado = true
                                mensaje = null
                                Repositorio.cargarAcciones(punto.id).onSuccess { historial = it }
                            }
                            .onFailure {
                                guardado = false
                                mensaje = it.message
                            }
                        enviando = false
                    }
                }
            },
            enabled = !enviando,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (enviando) "Guardando..." else "Guardar acción")
        }
        mensaje?.let {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = it,
                style = MaterialTheme.typography.bodySmall,
                color = EstadoAdvertencia
            )
        }

        if (guardado) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "✓ Acción guardada",
                style = MaterialTheme.typography.labelLarge,
                color = EstadoNormal
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Seguimiento",
            style = MaterialTheme.typography.titleMedium,
            color = TextoOscuro
        )
        Spacer(modifier = Modifier.height(8.dp))

        if (historial.isEmpty()) {
            Text(
                text = "Aún no hay acciones registradas",
                style = MaterialTheme.typography.bodyMedium,
                color = TextoSecundario
            )
        }

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(historial) { accion ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Blanco)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = accion.tipo,
                            style = MaterialTheme.typography.titleSmall,
                            color = TextoOscuro
                        )
                        Text(
                            text = "${accion.fecha} · ${accion.rol}",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextoSecundario
                        )
                        if (accion.observaciones.isNotBlank()) {
                            Text(
                                text = accion.observaciones,
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextoOscuro
                            )
                        }
                    }
                }
            }
        }
    }
}