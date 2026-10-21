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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.cloroapp.data.model.Granja
import com.example.cloroapp.data.model.PuntoMedicion
import com.example.cloroapp.ui.theme.AzulPrincipal
import com.example.cloroapp.ui.theme.Blanco
import com.example.cloroapp.ui.theme.FondoClaro
import com.example.cloroapp.ui.theme.TextoOscuro
import com.example.cloroapp.ui.theme.TextoSecundario

@Composable
fun DetalleGranjaScreen(
    granja: Granja,
    onVolver: () -> Unit,
    onPuntoClick: (PuntoMedicion) -> Unit = {}
) {
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
            text = granja.nombre,
            style = MaterialTheme.typography.headlineMedium,
            color = TextoOscuro
        )
        Text(
            text = "Estado general: ${granja.estadoGeneral.texto()}",
            style = MaterialTheme.typography.bodyMedium,
            color = granja.estadoGeneral.color()
        )
        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(granja.puntos) { punto ->
                PuntoCard(punto = punto, onClick = { onPuntoClick(punto) })
            }
        }
    }
}

@Composable
fun PuntoCard(punto: PuntoMedicion, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Blanco),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = punto.nombre,
                    style = MaterialTheme.typography.titleMedium,
                    color = TextoOscuro
                )
                Text(
                    text = "Actualizado a las ${punto.actualizado}",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextoSecundario
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${"%.1f".format(punto.cloroLibre)} mg/L",
                    style = MaterialTheme.typography.titleLarge,
                    color = punto.estado.color()
                )
                Text(
                    text = punto.estado.texto(),
                    style = MaterialTheme.typography.labelMedium,
                    color = punto.estado.color()
                )
            }
        }
    }
}