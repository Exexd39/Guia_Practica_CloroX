package com.example.cloroapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.cloroapp.data.DatosFalsos
import com.example.cloroapp.data.model.SesionActual
import com.example.cloroapp.ui.screen.AlertasScreen
import com.example.cloroapp.ui.screen.DashboardScreen
import com.example.cloroapp.ui.screen.DetalleGranjaScreen
import com.example.cloroapp.ui.screen.DetallePuntoScreen
import com.example.cloroapp.ui.screen.LoginScreen

object Rutas {
    const val DASHBOARD = "dashboard"
    const val DETALLE_GRANJA = "granja/{granjaId}"
    const val DETALLE_PUNTO = "punto/{puntoId}"
    const val ALERTAS = "alertas"

    const val LOGIN = "login"

    fun detalleGranja(id: String) = "granja/$id"
    fun detallePunto(id: String) = "punto/$id"
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Rutas.LOGIN)  {

        composable(Rutas.LOGIN) {
            LoginScreen(
                onIngresar = { rol ->
                    SesionActual.rol = rol
                    navController.navigate(Rutas.DASHBOARD) {
                        popUpTo(Rutas.LOGIN) { inclusive = true }
                    }
                }
            )
        }

        composable(Rutas.DASHBOARD) {
            DashboardScreen(
                granjas = DatosFalsos.granjas,
                onGranjaClick = { granja ->
                    navController.navigate(Rutas.detalleGranja(granja.id))
                },
                onAlertasClick = { navController.navigate(Rutas.ALERTAS) }
            )
        }

        composable(Rutas.DETALLE_GRANJA) { entrada ->
            val granjaId = entrada.arguments?.getString("granjaId")
            val granja = DatosFalsos.granjas.find { it.id == granjaId }
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

        composable(Rutas.ALERTAS) {
            AlertasScreen(
                granjas = DatosFalsos.granjas,
                onVolver = { navController.popBackStack() }
            )
        }
    }
}