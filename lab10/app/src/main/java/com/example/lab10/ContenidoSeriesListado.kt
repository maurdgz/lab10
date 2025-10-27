package com.example.lab10

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.lab10.data.SerieApiService
import com.example.lab10.data.SerieModel
import kotlinx.coroutines.launch
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit

// --- FUNCIÓN PARA LISTAR (READ) ---
@Composable
fun ContenidoSeriesListado(navController: NavHostController, servicio: SerieApiService) {
    // 1. Estado para la lista y la carga
    var listaSeries by remember { mutableStateOf<List<SerieModel>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) } // Para errores de conexión

    // 2. Clave de refresco. Se usa el argumento 'refresh' de la navegación
    val refreshKey = navController.currentBackStackEntry?.arguments?.getString("refresh")

    // 3. Efecto que se ejecuta al inicio y cuando se navega con el argumento 'refresh'
    LaunchedEffect(servicio, refreshKey) {
        isLoading = true
        errorMessage = null
        try {
            val response = servicio.selectSeries()
            if (response.isSuccessful) {
                listaSeries = response.body() ?: emptyList()
            } else {
                Log.e("SERIE-LISTADO", "Error al obtener series: ${response.code()}")
                errorMessage = "Error del servidor: ${response.code()}"
                listaSeries = emptyList()
            }
        } catch (e: Exception) {
            Log.e("SERIE-LISTADO", "Excepción al obtener series: ${e.message}")
            errorMessage = "Error de conexión: ${e.message}"
            listaSeries = emptyList()
        }
        isLoading = false
    }

    // Contenido de la pantalla de listado
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = "LISTADO DE SERIES",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
        )
        if (isLoading) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator(Modifier.padding(24.dp))
                Text("Cargando series...", style = MaterialTheme.typography.bodyLarge)
            }
        } else if (errorMessage != null) {
            Text(errorMessage!!, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(24.dp))
        } else if (listaSeries.isEmpty()) {
            Text("No hay series para mostrar.", modifier = Modifier.padding(24.dp))
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(top = 8.dp, bottom = 8.dp)
            ) {
                // Encabezados
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "ID", fontSize = 16.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(0.1f))
                        Text(text = "SERIE", fontSize = 16.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(0.5f))
                        Text(text = "Accion", fontSize = 16.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(0.4f), textAlign = TextAlign.Center)
                    }
                }

                // Elementos de la lista
                items(listaSeries) { item ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "${item.id}", fontSize = 14.sp, modifier = Modifier.weight(0.1f))
                        Text(text = item.name, fontSize = 14.sp, modifier = Modifier.weight(0.5f))

                        Row(modifier = Modifier.weight(0.4f), horizontalArrangement = Arrangement.Center) {
                            // Botón Editar
                            IconButton(
                                onClick = {
                                    // Aseguramos que el ID se pasa como Int
                                    navController.navigate("serieVer/${item.id}")
                                },
                            ) {
                                Icon(imageVector = Icons.Outlined.Edit, contentDescription = "Ver/Editar")
                            }

                            // Botón Eliminar
                            IconButton(
                                onClick = {
                                    // Aseguramos que el ID se pasa como Int
                                    navController.navigate("serieDel/${item.id}")
                                },
                            ) {
                                Icon(imageVector = Icons.Outlined.Delete, contentDescription = "Eliminar")
                            }
                        }
                    }
                }
            }
        }
    }
}

// --- FUNCIÓN PARA EDITAR/CREAR (CREATE/UPDATE) ---
@Composable
fun ContenidoSerieEditar(navController: NavHostController, servicio: SerieApiService, pid: Int) {
    // Estados de la UI
    var id by remember { mutableIntStateOf(pid) }
    var name by remember { mutableStateOf("") }
    var releaseDate by remember { mutableStateOf("") }
    var ratingText by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var isSaving by remember { mutableStateOf(false) }
    var loadError by remember { mutableStateOf<String?>(null) } // Para errores de carga
    val scope = rememberCoroutineScope()

    // Cargar datos si es edición (pid != 0)
    LaunchedEffect(pid) {
        if (pid != 0) {
            try {
                val response = servicio.selectSerie(pid)
                if (response.isSuccessful) {
                    val objSerie = response.body()
                    id = objSerie?.id ?: pid
                    name = objSerie?.name ?: ""
                    releaseDate = objSerie?.release_date ?: ""
                    // El campo 'rating' se recibe como Float del API y se convierte a String para el TextField
                    ratingText = objSerie?.rating?.toString() ?: ""
                    category = objSerie?.category ?: ""
                    loadError = null
                } else {
                    loadError = "No se pudo cargar la serie (Código: ${response.code()})"
                }
            } catch (e: Exception) {
                Log.e("SERIE-EDITAR", "Error al cargar serie: ${e.message}")
                loadError = "Error de conexión al cargar datos."
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = if (pid == 0) "Nueva Serie" else "Editar Serie (ID: $pid)",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (loadError != null) {
            Text(loadError!!, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(vertical = 16.dp))
        }

        TextField(
            // id es Int, se convierte a String para mostrar.
            value = id.toString(),
            onValueChange = { /* ID read-only */ },
            label = { Text("ID (solo lectura)") },
            readOnly = true,
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
        )
        TextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Name: ") },
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
        )
        TextField(
            value = releaseDate,
            onValueChange = { releaseDate = it },
            label = { Text("Release Date (YYYY-MM-DD):") },
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
        )
        TextField(
            // Se permite ingresar números, punto decimal y coma
            value = ratingText,
            // Filtra para solo permitir dígitos, punto decimal y COMA
            onValueChange = { ratingText = it.filter { c -> c.isDigit() || c == '.' || c == ',' } },
            label = { Text("Rating (e.g., 8.5):") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
        )
        TextField(
            value = category,
            onValueChange = { category = it },
            label = { Text("Category:") },
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
        )

        Button(
            onClick = {
                if (!isSaving) {
                    isSaving = true
                    scope.launch {
                        // **PASO 1: Procesar la entrada del usuario**
                        // Reemplazamos coma por punto antes de convertir a Float para compatibilidad.
                        val cleanRatingText = ratingText.replace(',', '.')
                        val finalRating = cleanRatingText.toFloatOrNull() ?: 0.0f

                        // **PASO 2: Construir el objeto Modelo**
                        val serieGuardar = SerieModel(
                            id = if (pid == 0) 0 else pid,
                            name = name,
                            release_date = releaseDate,
                            rating = finalRating, // Enviamos el Float directamente
                            category = category
                        )
                        
                        // LOG DE DEBUGGING: Imprime el objeto que se enviará para verificar el tipo y valor del rating
                        Log.d("SERIE-DEBUG", "Objeto a enviar: $serieGuardar")
                        
                        // **PASO 3: Realizar la llamada API**
                        try {
                            val response = if (pid == 0) {
                                // CREATE
                                servicio.insertSerie(serieGuardar)
                            } else {
                                // UPDATE
                                servicio.updateSerie(pid, serieGuardar)
                            }

                            if (response.isSuccessful) {
                                // Regresar al listado y forzar la recarga
                                navController.navigate("series?refresh=true") {
                                    popUpTo("series") { inclusive = true }
                                }
                            } else {
                                // Muestra el código de error y el cuerpo de error si existe
                                val errorBody = response.errorBody()?.string() ?: "Cuerpo de error vacío."
                                Log.e("SERIE-GRABAR", "Error en respuesta del servidor: ${response.code()} - $errorBody")
                                loadError = "Fallo al guardar (${response.code()}): $errorBody"
                            }
                        } catch (e: Exception) {
                            Log.e("SERIE-GRABAR", "Error de conexión/excepción: ${e.message}")
                            loadError = "Error de red al guardar: ${e.message}"
                        }
                        isSaving = false
                    }
                }
            },
            enabled = !isSaving,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (isSaving) "Grabando..." else "Grabar", fontSize = 16.sp)
        }
    }
}

// --- FUNCIÓN PARA ELIMINAR (DELETE) ---
@Composable
fun ContenidoSerieEliminar(navController: NavHostController, servicio: SerieApiService, id: Int) {
    var showDialog by remember { mutableStateOf(true) }
    var isDeleting by remember { mutableStateOf(false) }

    // Lógica de borrado (DELETE)
    LaunchedEffect(isDeleting) {
        if (isDeleting) {
            try {
                val response = servicio.deleteSerie(id)
                if (response.isSuccessful) {
                    Log.d("SERIE-ELIMINAR", "Serie $id eliminada con éxito.")
                } else {
                    Log.e("SERIE-ELIMINAR", "Error al eliminar serie: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("SERIE-ELIMINAR", "Error de conexión al eliminar serie: ${e.message}")
            }
            isDeleting = false
            // Regresar al listado y forzar recarga
            navController.navigate("series?refresh=true") {
                popUpTo("series") { inclusive = true }
            }
        }
    }

    // Mostrar diálogo de confirmación
    if (showDialog && !isDeleting) {
        AlertDialog(
            onDismissRequest = {
                showDialog = false
                navController.navigate("series") // Cancelar y volver
            },
            title = { Text(text = "Confirmar Eliminación") },
            text = { Text("¿Está seguro de eliminar la Serie ID $id?") },
            confirmButton = {
                Button(onClick = {
                    showDialog = false
                    isDeleting = true // Activa el LaunchedEffect
                }) {
                    Text("Aceptar")
                }
            },
            dismissButton = {
                Button(onClick = {
                    showDialog = false
                    navController.navigate("series") // Cancelar y volver
                }) {
                    Text("Cancelar")
                }
            }
        )
    }
}
