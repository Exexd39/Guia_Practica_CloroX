package com.example.cloroapp.ui.screen

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cloroapp.data.Evidencias
import com.example.cloroapp.data.model.Granja
import com.example.cloroapp.data.model.PuntoMedicion
import com.example.cloroapp.ui.components.CampoValidado
import com.example.cloroapp.ui.theme.AzulPrincipal
import com.example.cloroapp.ui.theme.Blanco
import com.example.cloroapp.ui.theme.EstadoAdvertencia
import com.example.cloroapp.ui.theme.EstadoNormal
import com.example.cloroapp.ui.theme.FondoClaro
import com.example.cloroapp.ui.theme.TextoOscuro
import com.example.cloroapp.ui.theme.TextoSecundario
import com.example.cloroapp.ui.viewmodel.AccionViewModel
import com.example.cloroapp.ui.viewmodel.TIPOS_ACCION
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

@Composable
fun AccionScreen(
    granja: Granja,
    punto: PuntoMedicion,
    onVolver: () -> Unit,
    vm: AccionViewModel = viewModel()
) {
    val estado by vm.estado.collectAsState()
    val contexto = LocalContext.current
    val appContext = contexto.applicationContext

    LaunchedEffect(punto.id) {
        vm.cargarHistorial(punto.id)
        vm.cargarEvidencias(appContext, punto.id)
    }

    // En camara: devuelve una imagen en miniatura
    val camara = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        if (bitmap != null) vm.guardarEvidencia(appContext, punto.id, bitmap)
    }

    // Permiso de camara en tiempo de ejecucion: si lo conceden, se abre la cámara
    val pedirPermisoCamara = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { concedido ->
        if (concedido) camara.launch(null)
        else vm.avisarEvidencia("Permiso de cámara denegado. Puedes elegir una foto de la galería.")
    }

    // Galeria con el selector del sistema, no requiere ningún permiso
    val galeria = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) vm.guardarEvidenciaDesdeUri(appContext, punto.id, uri)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FondoClaro)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        TextButton(onClick = onVolver) {
            Text("← Volver", color = AzulPrincipal)
        }

        Text(
            text = "Registrar acción",
            style = MaterialTheme.typography.headlineMedium,
            color = TextoOscuro
        )
        Text(
            text = "${granja.nombre} · ${punto.nombre}",
            style = MaterialTheme.typography.bodyMedium,
            color = TextoSecundario
        )
        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Blanco)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Valor actual",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextoSecundario,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "${"%.2f".format(punto.cloroLibre)} mg/L · ${punto.estado.texto()}",
                    style = MaterialTheme.typography.titleMedium,
                    color = punto.estado.color()
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Tipo de acción",
            style = MaterialTheme.typography.titleMedium,
            color = TextoOscuro
        )
        TIPOS_ACCION.forEach { tipo ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = tipo == estado.tipo,
                    onClick = { vm.onTipoChange(tipo) }
                )
                Text(text = tipo, color = TextoOscuro)
            }
        }
        Spacer(modifier = Modifier.height(12.dp))

        CampoValidado(
            valor = estado.observaciones,
            onCambio = vm::onObservacionesChange,
            etiqueta = "Observaciones",
            error = estado.errorObservaciones,
            unaLinea = false
        )
        Spacer(modifier = Modifier.height(16.dp))

        // ---------- Evidencia fotografica ----------
        Text(
            text = "Evidencia fotográfica (opcional)",
            style = MaterialTheme.typography.titleMedium,
            color = TextoOscuro
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = {
                val tienePermiso = ContextCompat.checkSelfPermission(
                    contexto, Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED
                if (tienePermiso) camara.launch(null)
                else pedirPermisoCamara.launch(Manifest.permission.CAMERA)
            }) {
                Text("Tomar foto")
            }
            OutlinedButton(onClick = {
                galeria.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                )
            }) {
                Text("Galería")
            }
        }
        Text(
            text = "Las fotos se guardan solo en este celular.",
            style = MaterialTheme.typography.bodySmall,
            color = TextoSecundario
        )

        estado.mensajeEvidencia?.let {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = it,
                style = MaterialTheme.typography.bodySmall,
                color = if (it.startsWith("✓")) EstadoNormal else EstadoAdvertencia
            )
        }

        if (estado.evidencias.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            LazyRow(
                modifier = Modifier.height(96.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(estado.evidencias, key = { it.absolutePath }) { archivo ->
                    MiniaturaEvidencia(
                        archivo = archivo,
                        onEliminar = { vm.eliminarEvidencia(appContext, punto.id, archivo) }
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { vm.guardar(punto.id) },
            enabled = !estado.enviando,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (estado.enviando) "Guardando..." else "Guardar acción")
        }

        estado.errorGeneral?.let {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = it,
                style = MaterialTheme.typography.bodySmall,
                color = EstadoAdvertencia
            )
        }

        if (estado.guardado) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "✓ Acción guardada",
                style = MaterialTheme.typography.labelLarge,
                color = EstadoNormal
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Seguimiento",
            style = MaterialTheme.typography.titleMedium,
            color = TextoOscuro
        )
        Spacer(modifier = Modifier.height(8.dp))

        if (estado.historial.isEmpty()) {
            Text(
                text = "Aún no hay acciones registradas",
                style = MaterialTheme.typography.bodyMedium,
                color = TextoSecundario
            )
        }

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            estado.historial.forEach { accion ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Blanco)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = accion.tipo,
                            style = MaterialTheme.typography.titleSmall,
                            color = TextoOscuro
                        )
                        Text(
                            text = "${accion.fecha} · ${accion.rol}",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextoSecundario
                        )
                        if (accion.observaciones.isNotBlank()) {
                            Text(
                                text = accion.observaciones,
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextoOscuro
                            )
                        }
                    }
                }
            }
        }
    }
}

// Miniatura con bordes redondeados y recorte para que llene el cuadrado
@Composable
private fun MiniaturaEvidencia(archivo: File, onEliminar: () -> Unit) {
    val imagen by produceState<ImageBitmap?>(initialValue = null, key1 = archivo) {
        value = withContext(Dispatchers.IO) {
            Evidencias.miniatura(archivo)?.asImageBitmap()
        }
    }

    Box(modifier = Modifier.size(96.dp)) {
        imagen?.let {
            Image(
                bitmap = it,
                contentDescription = "Foto de evidencia",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.dp, TextoSecundario, RoundedCornerShape(12.dp))
            )
        }
        IconButton(
            onClick = onEliminar,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(4.dp)
                .size(24.dp)
                .background(Color.Black.copy(alpha = 0.55f), CircleShape)
        ) {
            Icon(
                imageVector = Icons.Filled.Close,
                contentDescription = "Quitar foto",
                tint = Color.White,
                modifier = Modifier.size(14.dp)
            )
        }
    }
}