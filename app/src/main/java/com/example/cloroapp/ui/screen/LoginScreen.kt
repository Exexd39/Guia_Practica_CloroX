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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cloroapp.data.model.Usuario
import com.example.cloroapp.ui.components.CampoValidado
import com.example.cloroapp.ui.theme.AzulPrincipal
import com.example.cloroapp.ui.theme.Blanco
import com.example.cloroapp.ui.theme.EstadoCritico
import com.example.cloroapp.ui.theme.FondoClaro
import com.example.cloroapp.ui.theme.TextoSecundario
import com.example.cloroapp.ui.viewmodel.LoginViewModel

@Composable
fun LoginScreen(
    onIngresar: (Usuario) -> Unit,
    vm: LoginViewModel = viewModel()
) {
    val estado by vm.estado.collectAsState()

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
        Spacer(modifier = Modifier.height(32.dp))

        CampoValidado(
            valor = estado.correo,
            onCambio = vm::onCorreoChange,
            etiqueta = "Correo",
            error = estado.errorCorreo,
            teclado = KeyboardType.Email
        )
        Spacer(modifier = Modifier.height(12.dp))

        CampoValidado(
            valor = estado.clave,
            onCambio = vm::onClaveChange,
            etiqueta = "Contraseña",
            error = estado.errorClave,
            teclado = KeyboardType.Password,
            esClave = true
        )

        estado.errorGeneral?.let {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = it,
                style = MaterialTheme.typography.bodySmall,
                color = EstadoCritico
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { vm.ingresar(onIngresar) },
            enabled = !estado.cargando,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = AzulPrincipal)
        ) {
            Text(if (estado.cargando) "Conectando..." else "Ingresar", color = Blanco)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Usuarios de prueba (clave 1234)",
            style = MaterialTheme.typography.bodySmall,
            color = TextoSecundario
        )
        Text(
            text = "operario@cloro.cl\nsupervisor@cloro.cl\njefatura@cloro.cl",
            style = MaterialTheme.typography.bodySmall,
            color = TextoSecundario
        )
    }
}