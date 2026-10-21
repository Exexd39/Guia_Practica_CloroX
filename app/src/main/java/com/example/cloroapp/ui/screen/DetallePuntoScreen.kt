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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.cloroapp.data.DatosFalsos
import com.example.cloroapp.data.model.Granja
import com.example.cloroapp.data.model.PuntoMedicion
import com.example.cloroapp.ui.theme.AzulPrincipal
import com.example.cloroapp.ui.theme.FondoClaro
import com.example.cloroapp.ui.theme.TextoOscuro
import com.example.cloroapp.ui.theme.TextoSecundario

@Composable
fun DetallePuntoScreen(granja: Granja, punto: PuntoMedicion, onVolver: () -> Unit) {
    val historial = DatosFalsos.historial(punto.cloroLibre)

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
            text = "${"%.1f".format(punto.cloroLibre)} mg/L",
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
        Spacer(modifier = Modifier.height(12.dp))

        // Gráfico de barras simple: cada barra es una medición
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.Bottom
        ) {
            historial.forEach { valor ->
                Box(
                    modifier = Modifier
                        .width(24.dp)
                        .height((valor * 90).dp)
                        .background(
                            DatosFalsos.clasificarCloro(valor).color(),
                            RoundedCornerShape(4.dp)
                        )
                )
            }
        }
        Text(
            text = "Últimas 8 mediciones",
            style = MaterialTheme.typography.bodySmall,
            color = TextoSecundario
        )
    }
}