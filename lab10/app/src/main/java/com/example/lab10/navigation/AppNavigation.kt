package com.example.lab10.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.lab10.data.SerieApiService
import com.example.lab10.view.ContenidoSerieEditar
import com.example.lab10.view.ContenidoSerieEliminar
import com.example.lab10.view.ContenidoSeriesListado

/**
 * Componente principal de navegación que define las rutas de la aplicación.
 * @param servicio La interfaz del servicio API para las operaciones de series.
 */
@Composable
fun AppNavigation(servicio: SerieApiService) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "series"
    ) {
        // --- RUTA DE LISTADO ---
        composable("series?refresh={refresh}",
            arguments = listOf(
                navArgument("refresh") {
                    defaultValue = null
                    nullable = true
                }
            )
        ) {
            ContenidoSeriesListado(navController, servicio)
        }

        // --- RUTA PARA CREAR (ID = 0) ---
        composable("serieNew") {
            ContenidoSerieEditar(navController, servicio, pid = 0)
        }

        // --- RUTA PARA EDITAR (ID PASADO COMO ARGUMENTO) ---
        composable(
            "serieVer/{id}",
            arguments = listOf(
                navArgument("id") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt("id") ?: 0
            ContenidoSerieEditar(navController, servicio, pid = id)
        }

        // --- RUTA PARA ELIMINAR (ID PASADO COMO ARGUMENTO) ---
        composable(
            "serieDel/{id}",
            arguments = listOf(
                navArgument("id") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt("id") ?: 0
            ContenidoSerieEliminar(navController, servicio, id = id)
        }
    }
}
