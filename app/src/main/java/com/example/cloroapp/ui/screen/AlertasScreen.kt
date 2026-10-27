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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.cloroapp.data.Permisos
import com.example.cloroapp.data.RegistroAcciones
import com.example.cloroapp.data.model.EstadoCloro
import com.example.cloroapp.data.model.Granja
import com.example.cloroapp.data.model.PuntoMedicion
import com.example.cloroapp.data.model.Rol
import com.example.cloroapp.data.model.TipoDesviacion
import com.example.cloroapp.ui.theme.AzulPrincipal
import com.example.cloroapp.ui.theme.Blanco
import com.example.cloroapp.ui.theme.FondoClaro
import com.example.cloroapp.ui.theme.TextoOscuro
import com.example.cloroapp.ui.theme.TextoSecundario

@Composable
fun AlertasScreen(
    granjas: List<Granja>,
    rol: Rol,
    onRegistrarClick: (PuntoMedicion) -> Unit = {}
) {
    val alertas: List<Pair<Granja, PuntoMedicion>> = granjas.flatMap { granja ->
        granja.puntos
            .filter { it.estado != EstadoCloro.NORMAL && !RegistroAcciones.estaCerrada(it.id) }
            .map { granja to it }
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
        Spacer(modifier = Modifier.height(16.dp))

        if (alertas.isEmpty()) {
            Text(
                text = "Sin alertas activas",
                style = MaterialTheme.typography.bodyLarge,
                color = TextoSecundario
            )
        }

        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(alertas) { (granja, punto) ->
                val tieneAccion = RegistroAcciones.tieneAcciones(punto.id)

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
                                    text = "${"%.2f".format(punto.cloroLibre)} ppm · ${punto.hora}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextoSecundario
                                )
                            }
                            Text(
                                text = punto.estado.texto(),
                                style = MaterialTheme.typography.labelLarge,
                                color = punto.estado.color()
                            )
                        }

                        val tipoDesviacion = when (punto.desviacion) {
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
                                        onClick = { RegistroAcciones.cerrar(punto.id) },
                                        enabled = tieneAccion
                                    ) {
                                        Text("Cerrar alerta")
                                    }
                                    if (!tieneAccion) {
                                        Text(
                                            text = "Requiere acción registrada",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = TextoSecundario
                                        )
                                    }
                                }
                            } else {
                                Text(
                                    text = if (tieneAccion) "Pendiente de cierre" else "Sin acción registrada",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = TextoSecundario
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}