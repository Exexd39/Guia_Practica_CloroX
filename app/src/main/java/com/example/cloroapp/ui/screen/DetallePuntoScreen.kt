package com.example.cloroapp.ui.screen

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cloroapp.data.model.Granja
import com.example.cloroapp.data.model.PuntoMedicion
import com.example.cloroapp.ui.theme.AzulPrincipal
import com.example.cloroapp.ui.theme.EstadoAdvertencia
import com.example.cloroapp.ui.theme.FondoClaro
import com.example.cloroapp.ui.theme.TextoOscuro
import com.example.cloroapp.ui.theme.TextoSecundario
import com.example.cloroapp.ui.viewmodel.DetallePuntoViewModel
import com.example.cloroapp.ui.viewmodel.PERIODOS_HISTORIAL

@Composable
fun DetallePuntoScreen(
    granja: Granja,
    punto: PuntoMedicion,
    onVolver: () -> Unit,
    puedeRegistrarMedicion: Boolean = false,
    onRegistrarMedicion: () -> Unit = {},
    vm: DetallePuntoViewModel = viewModel()
) {
    val estado by vm.estado.collectAsState()

    LaunchedEffect(punto.id) { vm.cargar(punto.id) }

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
        if (puedeRegistrarMedicion) {
            Spacer(modifier = Modifier.height(12.dp))
            Button(onClick = onRegistrarMedicion) { Text("Registrar medición") }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Histórico",
            style = MaterialTheme.typography.titleMedium,
            color = TextoOscuro
        )
        Spacer(modifier = Modifier.height(8.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            PERIODOS_HISTORIAL.forEach { (clave, texto) ->
                if (clave == estado.periodo) {
                    Button(onClick = { vm.cambiarPeriodo(punto.id, clave) }) { Text(texto) }
                } else {
                    OutlinedButton(onClick = { vm.cambiarPeriodo(punto.id, clave) }) { Text(texto) }
                }
            }
        }
        Spacer(modifier = Modifier.height(12.dp))

        when {
            estado.cargando -> Row(verticalAlignment = Alignment.CenterVertically) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp,
                    color = AzulPrincipal
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Cargando historial...",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextoSecundario
                )
            }

            estado.error != null -> Text(
                text = estado.error ?: "",
                style = MaterialTheme.typography.bodyMedium,
                color = EstadoAdvertencia
            )

            estado.historial.isEmpty() -> Text(
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
                    estado.historial.forEach { valor ->
                        BarraHistorial(
                            fraccion = (valor / 6.0).coerceIn(0.0, 1.0).toFloat(),
                            color = punto.rango.clasificar(valor).color(),
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 1.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "${estado.historial.size} mediciones · Promedio ${"%.2f".format(estado.promedio)}" +
                            " · Máx ${"%.2f".format(estado.maximo)} · Mín ${"%.2f".format(estado.minimo)} mg/L",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextoSecundario
                )
            }
        }
    }
}

// Cada barra "crece" desde cero hasta su valor al aparecer o cuando cambia
@Composable
private fun BarraHistorial(fraccion: Float, color: Color, modifier: Modifier = Modifier) {
    var objetivo by remember { mutableStateOf(0f) }
    LaunchedEffect(fraccion) { objetivo = fraccion }

    val animada by animateFloatAsState(
        targetValue = objetivo,
        animationSpec = tween(durationMillis = 600),
        label = "alturaBarra"
    )

    Box(
        modifier = modifier
            .height((animada * 150).dp)
            .background(color, RoundedCornerShape(2.dp))
    )
}