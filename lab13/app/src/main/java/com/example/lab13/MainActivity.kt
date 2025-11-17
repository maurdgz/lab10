package com.example.lab13

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp // Importación necesaria para el tamaño de fuente
import com.example.lab13.ui.theme.Lab13Theme

// =========================================================================
// ARCHIVO: MainActivity.kt
// Contiene la pantalla principal y los ejercicios 1, 2, 3, 4 y 5 (Final)
// =========================================================================

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Lab13Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen()
                }
            }
        }
    }
}

// Enumeración para manejar la navegación simple (EJERCICIO 5 AÑADIDO)
enum class Screen {
    MENU, EJERCICIO1, EJERCICIO2, EJERCICIO3, EJERCICIO4, EJERCICIO5
}

// Definición de los estados para el Ejercicio 4
sealed class LoadState {
    data object Loading : LoadState()
    data class Content(val data: String) : LoadState()
    data object Error : LoadState()
}

@Composable
fun MainScreen() {
    var currentScreen by remember { mutableStateOf(Screen.MENU) }

    Column(modifier = Modifier.fillMaxSize()) {
        // Barra de navegación con corrección de UI para pantallas pequeñas
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.DarkGray)
                .padding(vertical = 8.dp, horizontal = 4.dp), // Reducimos el padding
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            // Se usa Modifier.weight(1f) y se reduce el tamaño del texto para asegurar que encajen
            Button(onClick = { currentScreen = Screen.MENU }, modifier = Modifier.weight(1f).padding(horizontal = 2.dp)) { Text("Menú", fontSize = 10.sp) }
            Button(onClick = { currentScreen = Screen.EJERCICIO1 }, modifier = Modifier.weight(1f).padding(horizontal = 2.dp)) { Text("Eje. 1", fontSize = 10.sp) }
            Button(onClick = { currentScreen = Screen.EJERCICIO2 }, modifier = Modifier.weight(1f).padding(horizontal = 2.dp)) { Text("Eje. 2", fontSize = 10.sp) }
            Button(onClick = { currentScreen = Screen.EJERCICIO3 }, modifier = Modifier.weight(1f).padding(horizontal = 2.dp)) { Text("Eje. 3", fontSize = 10.sp) }
            Button(onClick = { currentScreen = Screen.EJERCICIO4 }, modifier = Modifier.weight(1f).padding(horizontal = 2.dp)) { Text("Eje. 4", fontSize = 10.sp) }
            Button(onClick = { currentScreen = Screen.EJERCICIO5 }, modifier = Modifier.weight(1f).padding(horizontal = 2.dp)) { Text("Eje. 5", fontSize = 10.sp) } // Nuevo botón
        }

        // Contenido de la pantalla
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            when (currentScreen) {
                Screen.MENU -> Text(
                    "Selecciona un ejercicio de la barra superior.",
                    style = MaterialTheme.typography.titleLarge
                )
                Screen.EJERCICIO1 -> Ejercicio1_AnimatedVisibility()
                Screen.EJERCICIO2 -> Ejercicio2_AnimateColorAsState()
                Screen.EJERCICIO3 -> Ejercicio3_SizeAndPosition()
                Screen.EJERCICIO4 -> Ejercicio4_AnimatedContent()
                Screen.EJERCICIO5 -> Ejercicio5_GamePrototype() // Llamada al Eje. 5
            }
        }
    }
}


/**
 * Composable para el Ejercicio 1: Animación de Visibilidad con AnimatedVisibility
 */
@Composable
fun Ejercicio1_AnimatedVisibility() {
    Text(
        "Ejercicio 1: AnimatedVisibility (Fade In/Out)",
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier.padding(bottom = 20.dp)
    )
    var isVisible by remember { mutableStateOf(false) }
    Button(
        onClick = { isVisible = !isVisible },
        modifier = Modifier.size(width = 200.dp, height = 50.dp)
    ) {
        Text(if (isVisible) "Ocultar Cuadro" else "Mostrar Cuadro")
    }
    Spacer(modifier = Modifier.height(20.dp))
    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        Box(
            modifier = Modifier
                .size(150.dp)
                .background(Color.Blue)
        )
    }
}

/**
 * Composable para el Ejercicio 2: Cambio de Color con animateColorAsState
 */
@Composable
fun Ejercicio2_AnimateColorAsState() {
    Text(
        "Ejercicio 2: animateColorAsState (Tween)",
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier.padding(bottom = 20.dp)
    )
    var isBlue by remember { mutableStateOf(true) }
    val targetColor = if (isBlue) Color(0xFF2196F3) else Color(0xFF4CAF50)
    val animatedColor by animateColorAsState(
        targetValue = targetColor,
        animationSpec = tween(durationMillis = 1000),
        label = "ColorChangeAnimation"
    )
    Button(
        onClick = { isBlue = !isBlue },
        modifier = Modifier.size(width = 250.dp, height = 50.dp)
    ) {
        Text(if (isBlue) "Cambiar a Verde" else "Cambiar a Azul")
    }
    Spacer(modifier = Modifier.height(30.dp))
    Box(
        modifier = Modifier
            .size(150.dp)
            .background(animatedColor)
    )
}

/**
 * Composable para el Ejercicio 3: Animación de Tamaño y Posición
 */
@Composable
fun Ejercicio3_SizeAndPosition() {
    Text(
        "Ejercicio 3: Tamaño y Posición (Spring)",
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier.padding(bottom = 20.dp)
    )
    var isExpanded by remember { mutableStateOf(false) }
    val targetSize: Dp = if (isExpanded) 250.dp else 100.dp
    val targetOffset: Dp = if (isExpanded) 50.dp else 0.dp

    val animatedSize: Dp by animateDpAsState(
        targetValue = targetSize,
        animationSpec = spring(
            dampingRatio = 0.4f,
            stiffness = 50f
        ),
        label = "SizeAnimation"
    )

    val animatedOffsetX: Dp by animateDpAsState(
        targetValue = targetOffset,
        animationSpec = spring(),
        label = "OffsetAnimationX"
    )

    Button(
        onClick = { isExpanded = !isExpanded },
        modifier = Modifier.size(width = 250.dp, height = 50.dp)
    ) {
        Text(if (isExpanded) "Reducir y Centrar" else "Expandir y Mover")
    }

    Spacer(modifier = Modifier.height(30.dp))

    Box(
        modifier = Modifier
            .offset(x = animatedOffsetX, y = animatedOffsetX)
            .size(animatedSize)
            .background(Color.Red)
    )

    Text(
        "El orden de los modificadores es crucial.",
        style = MaterialTheme.typography.bodySmall,
        modifier = Modifier.padding(top = 20.dp)
    )
    Text(
        "El Box se mueve (" + animatedOffsetX.value.toInt() + "dp) y luego se dimensiona.",
        style = MaterialTheme.typography.bodySmall,
    )
}

/**
 * Composable para el Ejercicio 4: Cambio de Contenido con AnimatedContent
 */
@Composable
fun Ejercicio4_AnimatedContent() {
    // 1. Título
    Text(
        "Ejercicio 4: AnimatedContent (Cambio de Contenido)",
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier.padding(bottom = 20.dp)
    )

    // 1. Estado actual, inicializado en Cargando
    var currentState by remember { mutableStateOf<LoadState>(LoadState.Loading) }

    // Función para simular el ciclo de vida de carga: Loading -> Content -> Error -> Loading
    val changeState: () -> Unit = {
        currentState = when (currentState) {
            LoadState.Loading -> LoadState.Content("¡Datos cargados con éxito!")
            is LoadState.Content -> LoadState.Error
            LoadState.Error -> LoadState.Loading
        }
    }

    // Botón para simular la interacción
    Button(
        onClick = changeState,
        modifier = Modifier.size(width = 250.dp, height = 50.dp)
    ) {
        Text("Cambiar Estado")
    }

    Spacer(modifier = Modifier.height(30.dp))

    // 2. Uso de AnimatedContent para la transición de contenido
    AnimatedContent(
        targetState = currentState,
        // 3. Configuración de la transición: fadeOut rápido, seguido de un fadeIn más lento
        transitionSpec = {
            (fadeIn(animationSpec = tween(700, delayMillis = 50))
                    togetherWith
                    fadeOut(animationSpec = tween(300)))
        },
        label = "ContentStateTransition"
    ) { targetState -> // El valor de 'targetState' es el estado que AnimatedContent está mostrando
        // Contenido del Box que cambia según el estado
        Box(
            modifier = Modifier
                .size(250.dp, 100.dp)
                .background(
                    when (targetState) {
                        LoadState.Loading -> Color.Gray
                        is LoadState.Content -> Color(0xFF4CAF50) // Verde
                        LoadState.Error -> Color(0xFFF44336)     // Rojo
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            // Muestra el mensaje correspondiente al estado
            Text(
                text = when (targetState) {
                    LoadState.Loading -> "Cargando..."
                    is LoadState.Content -> targetState.data
                    LoadState.Error -> "¡Error de carga!"
                },
                color = Color.White,
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

// =========================================================================
// EJERCICIO FINAL (5): Animaciones Combinadas - Prototipo de Videojuego
// =========================================================================
enum class GameState { RUNNING, GAME_OVER }

@Composable
fun Ejercicio5_GamePrototype() {
    Text(
        "Ejercicio 5: Animaciones Combinadas (Space Shooter Prototype)",
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier.padding(bottom = 20.dp)
    )

    var gameState by remember { mutableStateOf(GameState.RUNNING) }
    var shipHealth by remember { mutableStateOf(3) } // 3 de vida
    var currentPositionX by remember { mutableStateOf(0.dp) }

    // --- Animación 1: Movimiento de nave (animateDpAsState) ---
    val animatedShipX by animateDpAsState(
        targetValue = currentPositionX,
        animationSpec = spring(dampingRatio = 0.5f, stiffness = 100f),
        label = "ShipMovement"
    )

    // --- Animación 2: Color de la nave según la vida (animateColorAsState) ---
    val targetShipColor = when (shipHealth) {
        3 -> Color(0xFF4CAF50) // Verde (Full Health)
        2 -> Color(0xFFFF9800) // Naranja
        1 -> Color(0xFFF44336) // Rojo (Low Health)
        else -> Color(0xFF212121) // Oscuro (Destroyed)
    }
    val animatedShipColor by animateColorAsState(
        targetValue = targetShipColor,
        animationSpec = tween(400),
        label = "ShipColor"
    )

    // Contenedor principal del juego
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
            .background(Color(0xFF0D1B2A).copy(alpha = 0.9f)) // Fondo espacial
            .padding(16.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        // --- Animación 3: Cambio de Contenido (AnimatedContent) ---
        AnimatedContent(
            targetState = gameState,
            transitionSpec = {
                fadeIn(tween(800, delayMillis = 200)) togetherWith fadeOut(tween(500))
            },
            label = "GameStateTransition"
        ) { targetState ->
            when (targetState) {
                GameState.RUNNING -> {
                    // Contenido del juego: Nave
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        // Texto de vida
                        Text(
                            text = "VIDA: $shipHealth",
                            color = Color.White,
                            fontSize = 18.sp
                        )
                        Spacer(Modifier.height(4.dp))
                        // La Nave, combinando animaciones de color y posición
                        Box(
                            modifier = Modifier
                                .offset(x = animatedShipX) // Movimiento horizontal
                                .size(60.dp)
                                .background(animatedShipColor, MaterialTheme.shapes.extraSmall) // Color animado
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "🚀", fontSize = 30.sp) // Representación simple de la nave
                        }
                    }
                }
                GameState.GAME_OVER -> {
                    // Pantalla de Game Over
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.8f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "¡GAME OVER!",
                            color = Color.Red,
                            fontSize = 32.sp,
                            style = MaterialTheme.typography.titleLarge
                        )
                    }
                }
            }
        }
    }

    Spacer(Modifier.height(20.dp))

    // Controles
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Button(onClick = { currentPositionX -= 20.dp }, enabled = gameState == GameState.RUNNING) {
            Text("Mover Izquierda")
        }
        Button(onClick = { currentPositionX += 20.dp }, enabled = gameState == GameState.RUNNING) {
            Text("Mover Derecha")
        }
    }
    Spacer(Modifier.height(10.dp))
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Button(
            onClick = {
                if (shipHealth > 1) {
                    shipHealth-- // El color cambiará animadamente
                } else {
                    shipHealth = 0
                    gameState = GameState.GAME_OVER // Transición a Game Over (AnimatedContent)
                }
            },
            enabled = gameState == GameState.RUNNING
        ) {
            Text("Recibir Daño (Hit!)")
        }
        Button(
            onClick = {
                shipHealth = 3
                currentPositionX = 0.dp
                gameState = GameState.RUNNING
            }
        ) {
            Text("Reiniciar Juego")
        }
    }
}


@Preview(showBackground = true)
@Composable
fun AnimacionesPreview() {
    Lab13Theme {
        MainScreen()
    }
}