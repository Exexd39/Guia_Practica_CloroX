package com.example.cloroapp.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.cloroapp.data.Repositorio
import com.example.cloroapp.data.model.Granja
import com.example.cloroapp.data.model.PuntoMedicion
import com.example.cloroapp.ui.theme.AzulPrincipal
import com.example.cloroapp.ui.theme.EstadoAdvertencia
import com.example.cloroapp.ui.theme.FondoClaro
import com.example.cloroapp.ui.theme.TextoOscuro
import com.example.cloroapp.ui.theme.TextoSecundario

@Composable
fun DetallePuntoScreen(granja: Granja, punto: PuntoMedicion, onVolver: () -> Unit) {
    var periodo by remember { mutableStateOf("24h") }
    var historial by remember { mutableStateOf<List<Double>>(emptyList()) }
    var cargando by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(punto.id, periodo) {
        cargando = true
        error = null
        Repositorio.cargarHistorial(punto.id, periodo)
            .onSuccess { historial = it }
            .onFailure { historial = emptyList(); error = it.message }
        cargando = false
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
            text = punto.nombre,
            style = MaterialTheme.typography.headlineMedium,
            color = TextoOscuro
        )
        Text(
            text = granja.nombre,
            style = MaterialTheme.typography.bodyMedium,
            color = TextoSecundario
        )
        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "${"%.2f".format(punto.cloroLibre)} mg/L",
            style = MaterialTheme.typography.displaySmall,
            color = punto.estado.color()
        )
        Text(
            text = "${punto.estado.texto()} · actualizado a las ${punto.actualizado}",
            style = MaterialTheme.typography.bodyMedium,
            color = punto.estado.color()
        )
        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Histórico",
            style = MaterialTheme.typography.titleMedium,
            color = TextoOscuro
        )
        Spacer(modifier = Modifier.height(8.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("24h" to "24 horas", "7d" to "7 días", "30d" to "30 días")
                .forEach { (clave, texto) ->
                    if (clave == periodo) {
                        Button(onClick = { periodo = clave }) { Text(texto) }
                    } else {
                        OutlinedButton(onClick = { periodo = clave }) { Text(texto) }
                    }
                }
        }
        Spacer(modifier = Modifier.height(12.dp))

        when {
            cargando -> Text(
                text = "Cargando historial...",
                style = MaterialTheme.typography.bodyMedium,
                color = TextoSecundario
            )
            error != null -> Text(
                text = error ?: "",
                style = MaterialTheme.typography.bodyMedium,
                color = EstadoAdvertencia
            )
            historial.isEmpty() -> Text(
                text = "Sin mediciones en este período",
                style = MaterialTheme.typography.bodyMedium,
                color = TextoSecundario
            )
            else -> {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    verticalAlignment = Alignment.Bottom
                ) {
                    historial.forEach { valor ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 1.dp)
                                .height((((valor / 6.0).coerceIn(0.0, 1.0)) * 150).dp)
                                .background(
                                    punto.rango.clasificar(valor).color(),
                                    RoundedCornerShape(2.dp)
                                )
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "${historial.size} mediciones · Promedio ${"%.2f".format(historial.average())}" +
                            " · Máx ${"%.2f".format(historial.max())} · Mín ${"%.2f".format(historial.min())} mg/L",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextoSecundario
                )
            }
        }
    }
}