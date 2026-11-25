package com.example.cloroapp.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.cloroapp.data.clima.ClimaActual
import com.example.cloroapp.data.clima.ClimaRepositorio
import com.example.cloroapp.data.clima.CoordenadasGranjas
import com.example.cloroapp.data.model.Granja

@Composable
fun ClimaScreen(granjas: List<Granja>) {
    val climas = remember { mutableStateMapOf<String, ClimaActual>() }
    var error by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(granjas) {
        error = null
        granjas.forEach { granja ->
            val coord = CoordenadasGranjas.de(granja.id)
            ClimaRepositorio.obtener(coord.latitud, coord.longitud)
                .onSuccess { climas[granja.id] = it }
                .onFailure { error = "No se pudo obtener el clima. Revisa tu conexión." }
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text("Clima actual", style = MaterialTheme.typography.headlineSmall)
        }
        error?.let { mensaje ->
            item { Text(mensaje, color = MaterialTheme.colorScheme.error) }
        }
        items(granjas, key = { it.id }) { granja ->
            val clima = climas[granja.id]
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(granja.nombre, style = MaterialTheme.typography.titleMedium)
                    if (clima == null) {
                        Text("Cargando...")
                    } else {
                        Text("Temperatura: ${"%.1f".format(clima.temperatura)} °C")
                        Text("Humedad: ${"%.0f".format(clima.humedad)} %")
                        Text("Viento: ${"%.1f".format(clima.viento)} km/h")
                    }
                }
            }
        }
    }
}