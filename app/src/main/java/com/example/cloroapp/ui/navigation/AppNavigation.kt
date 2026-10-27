package com.example.cloroapp.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.cloroapp.data.DatosFalsos
import com.example.cloroapp.data.Usuarios
import com.example.cloroapp.data.model.SesionActual
import com.example.cloroapp.ui.screen.AccionScreen
import com.example.cloroapp.ui.screen.AlertasScreen
import com.example.cloroapp.ui.screen.DashboardScreen
import com.example.cloroapp.ui.screen.DetalleGranjaScreen
import com.example.cloroapp.ui.screen.DetallePuntoScreen
import com.example.cloroapp.ui.screen.HistorialScreen
import com.example.cloroapp.ui.screen.LoginScreen

object Rutas {
    const val LOGIN = "login"
    const val DASHBOARD = "dashboard"
    const val ALERTAS = "alertas"
    const val HISTORIAL = "historial"
    const val DETALLE_GRANJA = "granja/{granjaId}"
    const val DETALLE_PUNTO = "punto/{puntoId}"
    const val ACCION = "accion/{puntoId}"

    fun detalleGranja(id: String) = "granja/$id"
    fun detallePunto(id: String) = "punto/$id"
    fun accion(id: String) = "accion/$id"
}

private data class PestanaInferior(
    val ruta: String,
    val texto: String,
    val icono: ImageVector
)

private val pestanas = listOf(
    PestanaInferior(Rutas.DASHBOARD, "Granjas", Icons.Default.Home),
    PestanaInferior(Rutas.ALERTAS, "Alertas", Icons.Default.Warning),
    PestanaInferior(Rutas.HISTORIAL, "Historial", Icons.AutoMirrored.Filled.List)
)

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val entradaActual by navController.currentBackStackEntryAsState()
    val rutaActual = entradaActual?.destination?.route

    Scaffold(
        bottomBar = {
            if (pestanas.any { it.ruta == rutaActual }) {
                NavigationBar {
                    pestanas.forEach { pestana ->
                        NavigationBarItem(
                            selected = rutaActual == pestana.ruta,
                            onClick = {
                                navController.navigate(pestana.ruta) {
                                    popUpTo(Rutas.DASHBOARD) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Icon(pestana.icono, contentDescription = pestana.texto) },
                            label = { Text(pestana.texto) }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Rutas.LOGIN,
            modifier = Modifier.padding(innerPadding)
        ) {

            composable(Rutas.LOGIN) {
                LoginScreen(
                    onIngresar = { usuario ->
                        SesionActual.usuario = usuario
                        navController.navigate(Rutas.DASHBOARD) {
                            popUpTo(Rutas.LOGIN) { inclusive = true }
                        }
                    }
                )
            }

            composable(Rutas.DASHBOARD) {
                DashboardScreen(
                    granjas = Usuarios.granjasVisibles(SesionActual.usuario),
                    onGranjaClick = { granja ->
                        navController.navigate(Rutas.detalleGranja(granja.id))
                    },
                    onSalir = {
                        SesionActual.usuario = null
                        navController.navigate(Rutas.LOGIN) {
                            popUpTo(navController.graph.id) { inclusive = true }
                        }
                    }
                )
            }

            composable(Rutas.ALERTAS) {
                AlertasScreen(
                    granjas = Usuarios.granjasVisibles(SesionActual.usuario),
                    rol = SesionActual.rol,
                    onRegistrarClick = { punto ->
                        navController.navigate(Rutas.accion(punto.id))
                    }
                )
            }

            composable(Rutas.HISTORIAL) {
                HistorialScreen(
                    granjas = Usuarios.granjasVisibles(SesionActual.usuario),
                    onPuntoClick = { punto ->
                        navController.navigate(Rutas.detallePunto(punto.id))
                    }
                )
            }

            composable(Rutas.DETALLE_GRANJA) { entrada ->
                val granjaId = entrada.arguments?.getString("granjaId")
                val granja = Usuarios.granjasVisibles(SesionActual.usuario)
                    .find { it.id == granjaId }
                if (granja != null) {
                    DetalleGranjaScreen(
                        granja = granja,
                        onVolver = { navController.popBackStack() },
                        onPuntoClick = { punto ->
                            navController.navigate(Rutas.detallePunto(punto.id))
                        }
                    )
                }
            }

            composable(Rutas.DETALLE_PUNTO) { entrada ->
                val puntoId = entrada.arguments?.getString("puntoId")
                val encontrado = puntoId?.let { DatosFalsos.buscarPunto(it) }
                if (encontrado != null) {
                    DetallePuntoScreen(
                        granja = encontrado.first,
                        punto = encontrado.second,
                        onVolver = { navController.popBackStack() }
                    )
                }
            }

            composable(Rutas.ACCION) { entrada ->
                val puntoId = entrada.arguments?.getString("puntoId")
                val encontrado = puntoId?.let { DatosFalsos.buscarPunto(it) }
                if (encontrado != null) {
                    AccionScreen(
                        granja = encontrado.first,
                        punto = encontrado.second,
                        onVolver = { navController.popBackStack() }
                    )
                }
            }
        }
    }
}