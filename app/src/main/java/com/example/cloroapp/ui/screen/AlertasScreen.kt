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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.cloroapp.data.model.EstadoCloro
import com.example.cloroapp.data.model.Granja
import com.example.cloroapp.data.model.PuntoMedicion
import com.example.cloroapp.ui.theme.AzulPrincipal
import com.example.cloroapp.ui.theme.Blanco
import com.example.cloroapp.ui.theme.FondoClaro
import com.example.cloroapp.ui.theme.TextoOscuro
import com.example.cloroapp.ui.theme.TextoSecundario

@Composable
fun AlertasScreen(granjas: List<Granja>, onVolver: () -> Unit) {
    // Solo puntos fuera de rango normal
    val alertas: List<Pair<Granja, PuntoMedicion>> = granjas.flatMap { granja ->
        granja.puntos
            .filter { it.estado != EstadoCloro.NORMAL }
            .map { granja to it }
    }

    // Ids de alertas marcadas como revisadas (luego lo puede manejar el ViewModel)
    val revisadas = remember { mutableStateListOf<String>() }

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
            text = "Alertas",
            style = MaterialTheme.typography.headlineMedium,
            color = TextoOscuro
        )
        Text(
            text = "${alertas.size} puntos fuera de rango",
            style = MaterialTheme.typography.bodyMedium,
            color = TextoSecundario
        )
        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(alertas) { (granja, punto) ->
                val revisada = punto.id in revisadas

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
                                    text = "${"%.1f".format(punto.cloroLibre)} mg/L · ${punto.actualizado}",
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

                        Spacer(modifier = Modifier.height(12.dp))

                        if (revisada) {
                            Text(
                                text = "✓ Revisada",
                                style = MaterialTheme.typography.labelLarge,
                                color = TextoSecundario
                            )
                        } else {
                            Button(onClick = { revisadas.add(punto.id) }) {
                                Text("Marcar como revisada")
                            }
                        }
                    }
                }
            }
        }
    }
}