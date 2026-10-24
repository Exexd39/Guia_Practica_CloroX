package com.example.cloroapp.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.cloroapp.ui.viewmodel.CloroViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(viewModel: CloroViewModel) {
    // Escuchamos los datos que vienen del ViewModel (Room + FastAPI)
    val listaGranjas by viewModel.granjas.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Dashboard de Granjas") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    ) { paddingValues ->
        // Si la lista esta vacia mostramos un mensaje
        if (listaGranjas.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "Buscando granjas en la base de datos...", style = MaterialTheme.typography.bodyLarge)
            }
        } else {
            // Si hay datos, dibujamos las tarjetas
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
            ) {
                items(listaGranjas) { granja ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(text = "Granja: ${granja.nombre}", style = MaterialTheme.typography.titleLarge)
                            Text(text = "ID: ${granja.id}", style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }
        }
    }
}