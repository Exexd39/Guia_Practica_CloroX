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
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cloroapp.data.model.Granja
import com.example.cloroapp.data.model.PuntoMedicion
import com.example.cloroapp.ui.components.CampoValidado
import com.example.cloroapp.ui.theme.AzulPrincipal
import com.example.cloroapp.ui.theme.Blanco
import com.example.cloroapp.ui.theme.EstadoAdvertencia
import com.example.cloroapp.ui.theme.EstadoNormal
import com.example.cloroapp.ui.theme.FondoClaro
import com.example.cloroapp.ui.theme.TextoOscuro
import com.example.cloroapp.ui.theme.TextoSecundario
import com.example.cloroapp.ui.viewmodel.AccionViewModel
import com.example.cloroapp.ui.viewmodel.TIPOS_ACCION

@Composable
fun AccionScreen(
    granja: Granja,
    punto: PuntoMedicion,
    onVolver: () -> Unit,
    vm: AccionViewModel = viewModel()
) {
    val estado by vm.estado.collectAsState()

    LaunchedEffect(punto.id) { vm.cargarHistorial(punto.id) }

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
        TIPOS_ACCION.forEach { tipo ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = tipo == estado.tipo,
                    onClick = { vm.onTipoChange(tipo) }
                )
                Text(text = tipo, color = TextoOscuro)
            }
        }
        Spacer(modifier = Modifier.height(12.dp))

        CampoValidado(
            valor = estado.observaciones,
            onCambio = vm::onObservacionesChange,
            etiqueta = "Observaciones",
            error = estado.errorObservaciones,
            unaLinea = false
        )
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { vm.guardar(punto.id) },
            enabled = !estado.enviando,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (estado.enviando) "Guardando..." else "Guardar acción")
        }

        estado.errorGeneral?.let {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = it,
                style = MaterialTheme.typography.bodySmall,
                color = EstadoAdvertencia
            )
        }

        if (estado.guardado) {
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

        if (estado.historial.isEmpty()) {
            Text(
                text = "Aún no hay acciones registradas",
                style = MaterialTheme.typography.bodyMedium,
                color = TextoSecundario
            )
        }

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(estado.historial) { accion ->
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