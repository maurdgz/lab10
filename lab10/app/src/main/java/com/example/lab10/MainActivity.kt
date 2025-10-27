package com.example.lab10

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.lab10.data.ApiClient
import com.example.lab10.ui.theme.Lab10Theme
import com.example.lab10.view.ContenidoSerieEditar
import com.example.lab10.view.ContenidoSerieEliminar
import com.example.lab10.view.ContenidoSeriesListado

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Lab10Theme {
                // Una superficie contenedora que utiliza el color 'background' del tema
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Llama al host de navegación principal
                    SerieAppNavigation()
                }
            }
        }
    }
}

/**
 * Define el NavHost para la aplicación, gestionando las rutas entre pantallas.
 */
@Composable
fun SerieAppNavigation() {
    val navController = rememberNavController()
    // Obtenemos la instancia del servicio API de forma global.
    val serieApiService = ApiClient.apiService

    NavHost(
        navController = navController,
        startDestination = "series"
    ) {
        // 1. Pantalla de LISTADO (READ) y punto de entrada
        composable(
            route = "series?refresh={refresh}",
            arguments = listOf(navArgument("refresh") {
                type = NavType.StringType
                defaultValue = "false"
                nullable = true
            })
        ) {
            ContenidoSeriesListado(navController, serieApiService)
        }

        // 2. Pantalla de CREAR nueva serie
        composable(route = "serieAdd") {
            // Pasamos pid=0 para indicar que es una creación
            ContenidoSerieEditar(navController, serieApiService, pid = 0)
        }

        // 3. Pantalla de VER/EDITAR serie existente
        composable(
            route = "serieVer/{pid}",
            arguments = listOf(navArgument("pid") { type = NavType.IntType })
        ) { backStackEntry ->
            val pid = backStackEntry.arguments?.getInt("pid") ?: 0
            ContenidoSerieEditar(navController, serieApiService, pid = pid)
        }

        // 4. Pantalla de ELIMINAR serie
        composable(
            route = "serieDel/{id}",
            arguments = listOf(navArgument("id") { type = NavType.IntType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt("id") ?: 0
            ContenidoSerieEliminar(navController, serieApiService, id = id)
        }
    }
}