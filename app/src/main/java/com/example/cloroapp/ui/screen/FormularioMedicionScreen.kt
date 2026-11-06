package com.example.cloroapp.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cloroapp.data.model.Granja
import com.example.cloroapp.data.model.PuntoMedicion
import com.example.cloroapp.ui.components.CampoValidado
import com.example.cloroapp.ui.theme.AzulPrincipal
import com.example.cloroapp.ui.theme.EstadoNormal
import com.example.cloroapp.ui.theme.FondoClaro
import com.example.cloroapp.ui.theme.TextoOscuro
import com.example.cloroapp.ui.theme.TextoSecundario
import com.example.cloroapp.ui.viewmodel.MedicionViewModel

@Composable
fun FormularioMedicionScreen(
    granja: Granja,
    punto: PuntoMedicion,
    onVolver: () -> Unit,
    vm: MedicionViewModel = viewModel()
) {
    val estado by vm.estado.collectAsState()

    val mensaje = estado.resultado?.let { r ->
        if (r.pendiente) {
            "Sin conexión: la medición quedó guardada en el celular y se enviará cuando vuelva internet."
        } else {
            "Medición registrada: ${r.estado?.texto() ?: ""}" +
                    if (r.hayAlerta) ". Se generó una alerta." else ""
        }
    }

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
            text = "Registrar medición",
            style = MaterialTheme.typography.headlineMedium,
            color = TextoOscuro
        )
        Text(
            text = "${granja.nombre} · ${punto.nombre}",
            style = MaterialTheme.typography.bodyMedium,
            color = TextoSecundario
        )
        Spacer(modifier = Modifier.height(16.dp))

        CampoValidado(
            valor = estado.texto,
            onCambio = vm::onTextoChange,
            etiqueta = "Cloro libre (mg/L)",
            error = estado.error,
            teclado = KeyboardType.Decimal
        )
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { vm.guardar(punto.id) },
            enabled = !estado.enviando,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (estado.enviando) "Enviando..." else "Guardar medición")
        }

        mensaje?.let {
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = it, style = MaterialTheme.typography.labelLarge, color = EstadoNormal)
        }
    }
}