package com.example.lab10.view

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
import java.io.IOException

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
                val errorBody = response.errorBody()?.string() ?: "Cuerpo de error vacío."
                Log.e("SERIE-LISTADO", "Error al obtener series: ${response.code()} - $errorBody")
                errorMessage = "Error del servidor al cargar: ${response.code()} - $errorBody"
                listaSeries = emptyList()
            }
        } catch (e: Exception) {
            val errorMsg = if (e is IOException) "Error de red/conexión: ${e.message}" else "Excepción desconocida: ${e.message}"
            Log.e("SERIE-LISTADO", errorMsg)
            errorMessage = errorMsg
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
                                    navController.navigate("serieVer/${item.id}")
                                },
                            ) {
                                Icon(imageVector = Icons.Outlined.Edit, contentDescription = "Ver/Editar")
                            }

                            // Botón Eliminar
                            IconButton(
                                onClick = {
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
    var loadError by remember { mutableStateOf<String?>(null) } // Para errores de carga/guardado
    // Eliminado: var dateError by remember { mutableStateOf<String?>(null) }
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
                    ratingText = objSerie?.rating?.toString() ?: ""
                    category = objSerie?.category ?: ""
                    loadError = null
                } else {
                    val errorBody = response.errorBody()?.string() ?: "Cuerpo de error vacío."
                    loadError = "No se pudo cargar la serie (Código: ${response.code()}): $errorBody"
                }
            } catch (e: Exception) {
                val errorMsg = if (e is IOException) "Error de red/conexión al cargar." else "Excepción desconocida: ${e.message}"
                Log.e("SERIE-EDITAR", errorMsg)
                loadError = errorMsg
            }
        }
    }

    // ELIMINADA: La función de validación de fecha (validateDate)

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

        // **AQUÍ SE MUESTRA EL ERROR DETALLADO DEL SERVIDOR**
        if (loadError != null) {
            Text(
                loadError!!,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(vertical = 16.dp),
                fontSize = 12.sp
            )
        }

        TextField(
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
        // ** CAMPO DE FECHA (SIN VALIDACIÓN) **
        TextField(
            value = releaseDate,
            onValueChange = {
                // Se mantiene el filtro para solo permitir dígitos y guiones
                releaseDate = it.filter { c -> c.isDigit() || c == '-' }
                // ELIMINADO: dateError = validateDate(releaseDate)
            },
            label = { Text("Release Date:") },
            placeholder = { Text("FORMATO: YYYY-MM-DD") },
            // ELIMINADO: isError = dateError != null,
            supportingText = {
                // Solo se muestra el texto de ejemplo, sin lógica de error
                Text("Ejemplo: 2024-06-15", color = MaterialTheme.colorScheme.onSurfaceVariant)
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), // Ayuda a la entrada numérica
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
        )
        // ** FIN CAMPO DE FECHA **
        TextField(
            value = ratingText,
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

        // El botón solo estará habilitado si los campos están llenos (sin depender de dateError)
        val isFormValid = name.isNotEmpty() && releaseDate.isNotEmpty() && !isSaving

        Button(
            onClick = {
                if (isFormValid) {
                    isSaving = true
                    loadError = null

                    // ELIMINADO: Lógica de revalidación de fecha

                    scope.launch {
                        // 1. Procesar la entrada (manejo de punto/coma)
                        val cleanRatingText = ratingText.replace(',', '.')
                        val finalRating = cleanRatingText.toFloatOrNull() ?: 0.0f

                        // 2. Construir el objeto Modelo
                        val serieGuardar = SerieModel(
                            id = if (pid == 0) 0 else pid,
                            name = name,
                            release_date = releaseDate, // Se envía la fecha sin validación adicional
                            rating = finalRating,
                            category = category
                        )

                        Log.d("SERIE-DEBUG", "Objeto a enviar: $serieGuardar")

                        // 3. Realizar la llamada API
                        try {
                            val response = if (pid == 0) {
                                // CREATE
                                servicio.insertSerie(serieGuardar)
                            } else {
                                // UPDATE
                                servicio.updateSerie(pid, serieGuardar)
                            }

                            if (response.isSuccessful) {
                                // Éxito: Navegamos de vuelta
                                navController.navigate("series?refresh=true") {
                                    popUpTo("series") { inclusive = true }
                                }
                            } else {
                                // FALLO: Capturamos el error de Django
                                val errorBody = response.errorBody()?.string() ?: "Cuerpo de error vacío."
                                Log.e("SERIE-GRABAR", "Error en respuesta del servidor: ${response.code()} - $errorBody")
                                loadError = "Fallo al guardar (${response.code()}): ${errorBody}"
                            }
                        } catch (e: Exception) {
                            val errorMsg = if (e is IOException) "Error de red/conexión." else "Excepción al grabar: ${e.message}"
                            Log.e("SERIE-GRABAR", errorMsg)
                            loadError = errorMsg
                        }
                        isSaving = false
                    }
                }
            },
            enabled = isFormValid,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (isSaving) "Grabando..." else "Grabar", fontSize = 16.sp)
        }

        // Indicador de estado del formulario
        if (!isFormValid && !isSaving && name.isEmpty()) {
            Text("Complete todos los campos requeridos.", color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f))
        }
    }
}

// --- FUNCIÓN PARA ELIMINAR (DELETE) ---
@Composable
fun ContenidoSerieEliminar(navController: NavHostController, servicio: SerieApiService, id: Int) {
    var showDialog by remember { mutableStateOf(true) }
    var isDeleting by remember { mutableStateOf(false) }
    var deleteError by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()


    // Mostrar diálogo de confirmación
    if (showDialog && !isDeleting) {
        AlertDialog(
            onDismissRequest = {
                showDialog = false
                navController.navigate("series") // Cancelar y volver
            },
            title = { Text(text = "Confirmar Eliminación") },
            text = {
                Column {
                    Text("¿Está seguro de eliminar la Serie ID $id?")
                    if (deleteError != null) {
                        Text(deleteError!!, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(top = 8.dp))
                    }
                }
            },
            confirmButton = {
                Button(onClick = {
                    showDialog = false
                    isDeleting = true // Activa la lógica de borrado
                    scope.launch {
                        try {
                            val response = servicio.deleteSerie(id)
                            if (response.isSuccessful) {
                                Log.d("SERIE-ELIMINAR", "Serie $id eliminada con éxito.")
                                // Éxito: Regresar al listado y forzar recarga
                                navController.navigate("series?refresh=true") {
                                    popUpTo("series") { inclusive = true }
                                }
                            } else {
                                // FALLO: Capturamos el error de Django
                                val errorBody = response.errorBody()?.string() ?: "Cuerpo de error vacío."
                                Log.e("SERIE-ELIMINAR", "Error al eliminar serie: ${response.code()} - $errorBody")
                                deleteError = "Fallo al eliminar (${response.code()}): $errorBody"
                                isDeleting = false // Permitimos reintentar o cancelar
                                showDialog = true // Mantenemos el diálogo abierto para mostrar el error
                            }
                        } catch (e: Exception) {
                            val errorMsg = if (e is IOException) "Error de red/conexión." else "Excepción al eliminar: ${e.message}"
                            Log.e("SERIE-ELIMINAR", errorMsg)
                            deleteError = errorMsg
                            isDeleting = false // Permitimos reintentar o cancelar
                            showDialog = true // Mantenemos el diálogo abierto para mostrar el error
                        }
                    }
                }, enabled = !isDeleting) {
                    Text(if (isDeleting) "Eliminando..." else "Aceptar")
                }
            },
            dismissButton = {
                Button(onClick = {
                    showDialog = false
                    navController.navigate("series") // Cancelar y volver
                }, enabled = !isDeleting) {
                    Text("Cancelar")
                }
            }
        )
    }
}