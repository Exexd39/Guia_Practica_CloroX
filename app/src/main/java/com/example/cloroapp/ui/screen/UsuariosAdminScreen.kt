package com.example.cloroapp.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cloroapp.data.model.SesionActual
import com.example.cloroapp.data.network.UsuarioDto
import com.example.cloroapp.data.usuarios.UsuarioRequest
import com.example.cloroapp.ui.viewmodel.UsuariosAdminViewModel
import com.example.cloroapp.ui.viewmodel.Validaciones
import androidx.compose.ui.Alignment
import com.example.cloroapp.ui.viewmodel.ValidacionUsuario

private val ROLES = listOf("OPERARIO", "SUPERVISOR", "JEFATURA")
private val GRANJAS = listOf("g1", "g2", "g3")

@Composable
fun UsuariosAdminScreen(
    onVolver: () -> Unit,
    viewModel: UsuariosAdminViewModel = viewModel()
) {
    val estado by viewModel.estado.collectAsState()
    var formularioAbierto by remember { mutableStateOf(false) }
    var usuarioEditando by remember { mutableStateOf<UsuarioDto?>(null) }
    var usuarioAEliminar by remember { mutableStateOf<UsuarioDto?>(null) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = {
                usuarioEditando = null
                formularioAbierto = true
            }) {
                Icon(Icons.Default.Add, contentDescription = "Nuevo usuario")
            }
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                TextButton(onClick = onVolver) { Text("Volver") }
                Text("Usuarios", style = MaterialTheme.typography.headlineSmall)
            }

            estado.error?.let { mensaje ->
                Text(
                    mensaje,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(estado.usuarios, key = { it.id }) { usuario ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(usuario.nombre, style = MaterialTheme.typography.titleMedium)
                                Text(usuario.correo, style = MaterialTheme.typography.bodyMedium)
                                Text(
                                    "${usuario.rol} · granjas: ${usuario.granjasAsignadas.joinToString()}",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                            IconButton(onClick = {
                                usuarioEditando = usuario
                                formularioAbierto = true
                            }) {
                                Icon(Icons.Default.Edit, contentDescription = "Editar")
                            }
                            // Evitamos que el admin se elimine a sí mismo
                            if (usuario.id != SesionActual.usuario?.id) {
                                IconButton(onClick = { usuarioAEliminar = usuario }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Eliminar")
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (formularioAbierto) {
        FormularioUsuario(
            usuario = usuarioEditando,
            cargando = estado.cargando,
            onCancelar = { formularioAbierto = false },
            onGuardar = { datos ->
                viewModel.guardar(usuarioEditando?.id, datos) { formularioAbierto = false }
            }
        )
    }

    usuarioAEliminar?.let { usuario ->
        AlertDialog(
            onDismissRequest = { usuarioAEliminar = null },
            title = { Text("Eliminar usuario") },
            text = { Text("¿Seguro que quieres eliminar a ${usuario.nombre}?") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.eliminar(usuario.id)
                    usuarioAEliminar = null
                }) { Text("Eliminar") }
            },
            dismissButton = {
                TextButton(onClick = { usuarioAEliminar = null }) { Text("Cancelar") }
            }
        )
    }
}

@Composable
private fun FormularioUsuario(
    usuario: UsuarioDto?,
    cargando: Boolean,
    onCancelar: () -> Unit,
    onGuardar: (UsuarioRequest) -> Unit
) {
    val esNuevo = usuario == null
    var nombre by remember { mutableStateOf(usuario?.nombre ?: "") }
    var correo by remember { mutableStateOf(usuario?.correo ?: "") }
    var clave by remember { mutableStateOf("") }
    var rol by remember { mutableStateOf(usuario?.rol ?: "OPERARIO") }
    var granjas by remember { mutableStateOf(usuario?.granjasAsignadas?.toSet() ?: emptySet()) }
    var errorNombre by remember { mutableStateOf<String?>(null) }
    var errorCorreo by remember { mutableStateOf<String?>(null) }
    var errorClave by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onCancelar,
        title = { Text(if (esNuevo) "Nuevo usuario" else "Editar usuario") },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it; errorNombre = null },
                    label = { Text("Nombre") },
                    isError = errorNombre != null,
                    supportingText = { errorNombre?.let { Text(it) } },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = correo,
                    onValueChange = { correo = it; errorCorreo = null },
                    label = { Text("Correo") },
                    isError = errorCorreo != null,
                    supportingText = { errorCorreo?.let { Text(it) } },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = clave,
                    onValueChange = { clave = it; errorClave = null },
                    label = { Text(if (esNuevo) "Clave" else "Clave (vacía = no cambiar)") },
                    isError = errorClave != null,
                    supportingText = { errorClave?.let { Text(it) } },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(12.dp))
                Text("Rol", style = MaterialTheme.typography.labelLarge)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    ROLES.forEach { opcion ->
                        FilterChip(
                            selected = rol == opcion,
                            onClick = { rol = opcion },
                            label = { Text(opcion) }
                        )
                    }
                }

                Spacer(Modifier.height(12.dp))
                Text("Granjas asignadas", style = MaterialTheme.typography.labelLarge)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    GRANJAS.forEach { granja ->
                        FilterChip(
                            selected = granja in granjas,
                            onClick = {
                                granjas = if (granja in granjas) granjas - granja else granjas + granja
                            },
                            label = { Text(granja) }
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                enabled = !cargando,
                onClick = {
                    errorNombre = ValidacionUsuario.nombre(nombre)
                    errorCorreo = Validaciones.correo(correo)
                    errorClave = ValidacionUsuario.clave(clave, esNuevo)

                    if (errorNombre == null && errorCorreo == null && errorClave == null) {
                        onGuardar(
                            UsuarioRequest(
                                nombre = nombre.trim(),
                                correo = correo.trim(),
                                clave = if (clave.isBlank()) null else clave,
                                rol = rol,
                                granjasAsignadas = granjas.toList()
                            )
                        )
                    }
                }
            ) { Text("Guardar") }
        },
        dismissButton = {
            TextButton(onClick = onCancelar) { Text("Cancelar") }
        }
    )
}