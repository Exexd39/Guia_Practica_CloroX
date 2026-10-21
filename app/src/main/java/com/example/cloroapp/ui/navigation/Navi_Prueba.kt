package com.example.cloroapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.cloroapp.data.DatosFalsos
import com.example.cloroapp.ui.screen.DashboardScreen
import com.example.cloroapp.ui.screen.DetalleGranjaScreen

object Rutas {
    const val DASHBOARD = "dashboard"
    const val DETALLE_GRANJA = "granja/{granjaId}"
    fun detalleGranja(id: String) = "granja/$id"
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Rutas.DASHBOARD) {
        composable(Rutas.DASHBOARD) {
            DashboardScreen(
                granjas = DatosFalsos.granjas,
                onGranjaClick = { granja ->
                    navController.navigate(Rutas.detalleGranja(granja.id))
                }
            )
        }

        composable(Rutas.DETALLE_GRANJA) { entrada ->
            val granjaId = entrada.arguments?.getString("granjaId")
            val granja = DatosFalsos.granjas.find { it.id == granjaId }
            if (granja != null) {
                DetalleGranjaScreen(
                    granja = granja,
                    onVolver = { navController.popBackStack() }
                )
            }
        }
    }
}