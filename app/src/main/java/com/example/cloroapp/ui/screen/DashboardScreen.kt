package com.example.cloroapp.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.cloroapp.data.EstadoRed
import com.example.cloroapp.data.Repositorio.pendientes
import com.example.cloroapp.data.model.EstadoCloro
import com.example.cloroapp.data.model.Granja
import com.example.cloroapp.data.model.SesionActual
import com.example.cloroapp.ui.theme.AzulPrincipal
import com.example.cloroapp.ui.theme.Blanco
import com.example.cloroapp.ui.theme.EstadoAdvertencia
import com.example.cloroapp.ui.theme.EstadoCritico
import com.example.cloroapp.ui.theme.EstadoNormal
import com.example.cloroapp.ui.theme.FondoClaro
import com.example.cloroapp.ui.theme.TextoOscuro
import com.example.cloroapp.ui.theme.TextoSecundario

@Composable
fun DashboardScreen(
    granjas: List<Granja>,
    estadoRed: EstadoRed = EstadoRed(),
    onReintentar: () -> Unit = {},
    onGranjaClick: (Granja) -> Unit = {},
    onSalir: () -> Unit = {},
    pendientes: Int = 0
) {
    if (pendientes > 0) {
        Text(
            text = "$pendientes medición(es) pendiente(s) de enviar",
            style = MaterialTheme.typography.bodySmall,
            color = EstadoAdvertencia
        )
        Spacer(modifier = Modifier.height(8.dp))
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FondoClaro)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Granjas",
                style = MaterialTheme.typography.headlineMedium,
                color = TextoOscuro,
                modifier = Modifier.weight(1f)
            )
            TextButton(onClick = onSalir) {
                Text("Salir", color = AzulPrincipal)
            }
        }
        Text(
            text = "Sesión: ${SesionActual.usuario?.nombre ?: "-"} · ${SesionActual.rol.nombre}",
            style = MaterialTheme.typography.bodyMedium,
            color = TextoSecundario
        )
        Spacer(modifier = Modifier.height(16.dp))

        BannerEstado(estadoRed, hayDatos = granjas.isNotEmpty(), onReintentar = onReintentar)

        if (granjas.isEmpty() && !estadoRed.cargando) {
            Text(
                text = if (estadoRed.error != null)
                    "Todavía no hay datos guardados. Conéctate a internet para descargarlos."
                else "No tienes granjas asignadas.",
                style = MaterialTheme.typography.bodyLarge,
                color = TextoSecundario
            )
        }

        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(granjas) { granja ->
                GranjaCard(granja = granja, onClick = { onGranjaClick(granja) })
            }
        }
    }
}

@Composable
fun GranjaCard(granja: Granja, onClick: () -> Unit) {
    val estado = granja.estadoGeneral

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Blanco),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(14.dp)
                    .clip(CircleShape)
                    .background(estado.color())
            )
            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = granja.nombre,
                    style = MaterialTheme.typography.titleMedium,
                    color = TextoOscuro
                )
                Text(
                    text = "${granja.puntos.size} ${if (granja.puntos.size == 1) "punto" else "puntos"} de medición",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextoSecundario
                )
            }

            Text(
                text = estado.texto(),
                style = MaterialTheme.typography.labelLarge,
                color = estado.color()
            )
        }
    }
}

@Composable
fun BannerEstado(estado: EstadoRed, hayDatos: Boolean, onReintentar: () -> Unit) {
    val mensaje = when {
        estado.cargando -> "Actualizando... (si el servidor estaba dormido, puede tardar un minuto)"
        estado.error != null ->
            if (hayDatos) "${estado.error}. Mostrando datos guardados." else estado.error

        estado.ultimaActualizacion != null -> "Última actualización: ${estado.ultimaActualizacion}"
        else -> null
    } ?: return

    val hayError = estado.error != null && !estado.cargando

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = mensaje,
            style = MaterialTheme.typography.bodySmall,
            color = if (hayError) EstadoAdvertencia else TextoSecundario,
            modifier = Modifier.weight(1f)
        )
        if (!estado.cargando) {
            TextButton(onClick = onReintentar) {
                Text(if (hayError) "Reintentar" else "Actualizar", color = AzulPrincipal)
            }
        }
    }
    Spacer(modifier = Modifier.height(8.dp))
}

// Colores y textos asociados a cada estado
fun EstadoCloro.color(): Color = when (this) {
    EstadoCloro.NORMAL -> EstadoNormal
    EstadoCloro.ADVERTENCIA -> EstadoAdvertencia
    EstadoCloro.CRITICO -> EstadoCritico
}

fun EstadoCloro.texto(): String = when (this) {
    EstadoCloro.NORMAL -> "Normal"
    EstadoCloro.ADVERTENCIA -> "Advertencia"
    EstadoCloro.CRITICO -> "Crítico"
}