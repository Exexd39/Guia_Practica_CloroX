package com.example.cloroapp.ui.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cloroapp.data.EstadoRed
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
import com.example.cloroapp.ui.viewmodel.DashboardViewModel

@Composable
fun DashboardScreen(
    onGranjaClick: (Granja) -> Unit = {},
    onSalir: () -> Unit = {},
    vm: DashboardViewModel = viewModel()
) {
    val granjas by vm.granjas.collectAsState()
    val estadoRed by vm.estadoRed.collectAsState()
    val pendientes by vm.pendientes.collectAsState()
    val contexto = LocalContext.current.applicationContext

    LaunchedEffect(Unit) { vm.cargar(contexto) }

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
            TextButton(onClick = {
                vm.cerrarSesion()
                onSalir()
            }) {
                Text("Salir", color = AzulPrincipal)
            }
        }
        Text(
            text = "Sesión: ${SesionActual.usuario?.nombre ?: "-"} · ${SesionActual.rol.nombre}",
            style = MaterialTheme.typography.bodyMedium,
            color = TextoSecundario
        )
        Spacer(modifier = Modifier.height(16.dp))

        BannerEstado(estadoRed, hayDatos = granjas.isNotEmpty(), onReintentar = vm::reintentar)

        AnimatedVisibility(
            visible = pendientes > 0,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Column {
                Text(
                    text = "$pendientes medición(es) pendiente(s) de enviar",
                    style = MaterialTheme.typography.bodySmall,
                    color = EstadoAdvertencia
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

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
            items(granjas, key = { it.id }) { granja ->
                GranjaCard(
                    granja = granja,
                    onClick = { onGranjaClick(granja) },
                    modifier = Modifier.animateItem()
                )
            }
        }
    }
}

@Composable
fun GranjaCard(granja: Granja, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val estado = granja.estadoGeneral
    // El color cambia con una transición suave cuando cambia el estado de la granja
    val colorEstado by animateColorAsState(
        targetValue = estado.color(),
        animationSpec = tween(durationMillis = 500),
        label = "colorEstadoGranja"
    )

    Card(
        modifier = modifier.fillMaxWidth(),
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
                    .background(colorEstado)
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
                color = colorEstado
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

    // Cuando cambia el mensaje, el texto anterior se desvanece y aparece el nuevo
    Crossfade(
        targetState = mensaje,
        animationSpec = tween(durationMillis = 300),
        label = "bannerEstado"
    ) { texto ->
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (estado.cargando) {
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    strokeWidth = 2.dp,
                    color = AzulPrincipal
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(
                text = texto,
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