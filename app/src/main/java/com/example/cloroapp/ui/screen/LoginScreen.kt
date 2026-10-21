package com.example.cloroapp.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.cloroapp.data.model.Rol
import com.example.cloroapp.ui.theme.AzulPrincipal
import com.example.cloroapp.ui.theme.Blanco
import com.example.cloroapp.ui.theme.FondoClaro
import com.example.cloroapp.ui.theme.TextoOscuro
import com.example.cloroapp.ui.theme.TextoSecundario

@Composable
fun LoginScreen(onIngresar: (Rol) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FondoClaro)
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "CloroApp",
            style = MaterialTheme.typography.headlineLarge,
            color = AzulPrincipal
        )
        Text(
            text = "Control de cloro libre",
            style = MaterialTheme.typography.bodyMedium,
            color = TextoSecundario
        )
        Spacer(modifier = Modifier.height(40.dp))

        Text(
            text = "Ingresa con tu perfil",
            style = MaterialTheme.typography.titleMedium,
            color = TextoOscuro
        )
        Spacer(modifier = Modifier.height(16.dp))

        Rol.entries.forEach { rol ->
            Button(
                onClick = { onIngresar(rol) },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = AzulPrincipal)
            ) {
                Column(modifier = Modifier.padding(vertical = 4.dp)) {
                    Text(text = rol.nombre, color = Blanco, style = MaterialTheme.typography.titleSmall)
                    Text(text = rol.descripcion, color = Blanco, style = MaterialTheme.typography.bodySmall)
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}