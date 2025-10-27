package com.example.lab10.view

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp // <-- ¡ESTA IMPORTACIÓN ES CLAVE!
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.lab10.data.SerieApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// --- COMPOSABLE DE INICIO (Necesario para la navegación) ---
@Composable
fun ScreenInicio() {
    Box(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {
        // La referencia 'sp' ahora está resuelta
        Text("Página de Inicio. Usa la barra inferior para ir a Series.", fontSize = 20.sp)
    }
}

// --- APP PRINCIPAL ---
@Composable
fun SeriesApp() {
    // IMPORTANTE: Usa tu IP (172.23.5.206)
    val urlBase = "http://172.23.5.206:8000/"

    // Inicialización simple de Retrofit
    val retrofit = Retrofit.Builder().baseUrl(urlBase)
        .addConverterFactory(GsonConverterFactory.create()).build()

    // Creación del servicio API
    val servicio = retrofit.create(SerieApiService::class.java)
    val navController = rememberNavController()

    Scaffold(
        topBar = { BarraSuperior() },
        bottomBar = { BarraInferior(navController) },
        floatingActionButton = { BotonFAB(navController) },
        content = { paddingValues -> Contenido(paddingValues, navController, servicio) }
    )
}

@Composable
fun BotonFAB(navController: NavHostController) {
    val cbeState by navController.currentBackStackEntryAsState()
    val rutaActual = cbeState?.destination?.route

    if (rutaActual == "series") {
        FloatingActionButton(
            containerColor = Color.Magenta,
            contentColor = Color.White,
            onClick = { navController.navigate("serieNuevo") }
        ) {
            Icon(Icons.Filled.Add, contentDescription = "Add")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BarraSuperior() {
    CenterAlignedTopAppBar(
        title = {
            Text(text = "SERIES APP", color = Color.White, fontWeight = FontWeight.Bold)
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary
        )
    )
}

@Composable
fun BarraInferior(navController: NavHostController) {
    NavigationBar(containerColor = Color.LightGray) {
        NavigationBarItem(
            icon = { Icon(Icons.Outlined.Home, contentDescription = "Inicio") },
            label = { Text("Inicio") },
            selected = navController.currentDestination?.route == "inicio",
            onClick = { navController.navigate("inicio") }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Outlined.Favorite, contentDescription = "Series") },
            label = { Text("Series") },
            selected = navController.currentDestination?.route == "series",
            onClick = { navController.navigate("series") }
        )
    }
}

@Composable
fun Contenido(pv: PaddingValues, navController: NavHostController, servicio: SerieApiService) {
    Box(modifier = Modifier
        .fillMaxSize()
        .padding(pv)) {
        NavHost(navController = navController, startDestination = "series") {
            composable("inicio") { ScreenInicio() }
            composable("series") { ContenidoSeriesListado(navController, servicio) }
            composable("serieNuevo") {
                ContenidoSerieEditar(navController, servicio, 0)
            }
            // Uso seguro del argumento para editar
            composable("serieVer/{id}", arguments = listOf(
                navArgument("id") { type = NavType.IntType }
            )) { backStackEntry ->
                ContenidoSerieEditar(navController, servicio, backStackEntry.arguments?.getInt("id") ?: 0)
            }
            // Uso seguro del argumento para eliminar
            composable("serieDel/{id}", arguments = listOf(
                navArgument("id") { type = NavType.IntType }
            )) { backStackEntry ->
                ContenidoSerieEliminar(navController, servicio, backStackEntry.arguments?.getInt("id") ?: 0)
            }
        }
    }
}
