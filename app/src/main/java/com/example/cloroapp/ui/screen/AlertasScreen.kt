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
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.cloroapp.data.Permisos
import com.example.cloroapp.data.Repositorio
import com.example.cloroapp.data.model.Granja
import com.example.cloroapp.data.model.PuntoMedicion
import com.example.cloroapp.data.model.Rol
import com.example.cloroapp.data.model.TipoDesviacion
import com.example.cloroapp.ui.theme.AzulPrincipal
import com.example.cloroapp.ui.theme.Blanco
import com.example.cloroapp.ui.theme.EstadoAdvertencia
import com.example.cloroapp.ui.theme.FondoClaro
import com.example.cloroapp.ui.theme.TextoOscuro
import com.example.cloroapp.ui.theme.TextoSecundario
import kotlinx.coroutines.launch

@Composable
fun AlertasScreen(
    granjas: List<Granja>,
    rol: Rol,
    onRegistrarClick: (PuntoMedicion) -> Unit = {}
) {
    val delServidor by Repositorio.alertas.collectAsState()
    val scope = rememberCoroutineScope()
    var mensajes by remember { mutableStateOf(mapOf<String, String>()) }
    var errorCarga by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        Repositorio.cargarAlertas()
            .onSuccess { errorCarga = null }
            .onFailure { errorCarga = it.message }
    }

    // Solo las alertas de las granjas que este usuario puede ver
    val alertas = delServidor.mapNotNull { alerta ->
        val granja = granjas.find { it.id == alerta.idGranja } ?: return@mapNotNull null
        val punto = granja.puntos.find { it.id == alerta.idPunto } ?: return@mapNotNull null
        Triple(granja, punto, alerta)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FondoClaro)
            .padding(16.dp)
    ) {
        Text(
            text = "Alertas",
            style = MaterialTheme.typography.headlineMedium,
            color = TextoOscuro
        )
        Text(
            text = "${alertas.size} alertas abiertas",
            style = MaterialTheme.typography.bodyMedium,
            color = TextoSecundario
        )
        if (errorCarga != null) {
            Text(
                text = if (alertas.isEmpty()) "${errorCarga}"
                else "${errorCarga}. Mostrando las últimas alertas descargadas.",
                style = MaterialTheme.typography.bodySmall,
                color = EstadoAdvertencia
            )
        }
        Spacer(modifier = Modifier.height(16.dp))

        if (alertas.isEmpty() && errorCarga == null) {
            Text(
                text = "Sin alertas activas",
                style = MaterialTheme.typography.bodyLarge,
                color = TextoSecundario
            )
        }

        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(alertas) { (granja, punto, alerta) ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Blanco)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "${granja.nombre} · ${punto.nombre}",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = TextoOscuro
                                )
                                Text(
                                    text = "${"%.2f".format(alerta.nivelCloro)} ppm · ${alerta.fechaHora.take(16)}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextoSecundario
                                )
                            }
                            Text(
                                text = alerta.gravedad.texto(),
                                style = MaterialTheme.typography.labelLarge,
                                color = alerta.gravedad.color()
                            )
                        }

                        val tipoDesviacion = when (alerta.desviacion) {
                            TipoDesviacion.INSUFICIENTE -> "Cloro insuficiente"
                            TipoDesviacion.EXCESIVO -> "Cloro excesivo"
                            TipoDesviacion.NINGUNA -> null
                        }
                        if (tipoDesviacion != null) {
                            Text(
                                text = tipoDesviacion,
                                style = MaterialTheme.typography.bodySmall,
                                color = TextoSecundario
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (Permisos.puedeRegistrarAccion(rol)) {
                                TextButton(onClick = { onRegistrarClick(punto) }) {
                                    Text("Registrar acción", color = AzulPrincipal)
                                }
                            }

                            Spacer(modifier = Modifier.weight(1f))

                            if (Permisos.puedeCerrarAlerta(rol)) {
                                Column(horizontalAlignment = Alignment.End) {
                                    Button(
                                        onClick = {
                                            scope.launch {
                                                Repositorio.cerrarAlerta(alerta.id)
                                                    .onSuccess { mensajes = mensajes - alerta.id }
                                                    .onFailure {
                                                        mensajes = mensajes +
                                                                (alerta.id to (it.message ?: "No se pudo cerrar"))
                                                    }
                                            }
                                        },
                                        enabled = alerta.tieneAccion
                                    ) {
                                        Text("Cerrar alerta")
                                    }
                                    if (!alerta.tieneAccion) {
                                        Text(
                                            text = "Requiere acción registrada",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = TextoSecundario
                                        )
                                    }
                                }
                            } else {
                                Text(
                                    text = if (alerta.tieneAccion) "Pendiente de cierre" else "Sin acción registrada",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = TextoSecundario
                                )
                            }
                        }

                        mensajes[alerta.id]?.let { mensaje ->
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = mensaje,
                                style = MaterialTheme.typography.bodySmall,
                                color = EstadoAdvertencia
                            )
                        }
                    }
                }
            }
        }
    }
}