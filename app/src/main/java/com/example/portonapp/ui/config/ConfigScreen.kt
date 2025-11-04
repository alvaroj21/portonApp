package com.example.portonapp.ui.config

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.portonapp.data.model.ManipulationMode
import com.example.portonapp.viewmodel.ControlViewModel

/**
 * Pantalla de Configuración
 * Permite configurar el tiempo de cierre y el modo de manipulación
 */
@Composable
fun ConfigScreen(
    viewModel: ControlViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    val portonState = uiState.portonState
    
    // Estado local para el input del tiempo de cierre
    var timeInput by remember { mutableStateOf(portonState.tiempoCierreSegundos.toString()) }
    
    // Sincronizar el input cuando cambie el estado del portón
    LaunchedEffect(portonState.tiempoCierreSegundos) {
        timeInput = portonState.tiempoCierreSegundos.toString()
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Título
        Text(
            text = "Configuración",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        // Configuración de tiempo de cierre
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Tiempo de Cierre Automático",
                    style = MaterialTheme.typography.titleMedium
                )
                
                Text(
                    text = "Define el tiempo en segundos para el cierre automático del portón",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                OutlinedTextField(
                    value = timeInput,
                    onValueChange = { newValue ->
                        // Solo permitir números
                        if (newValue.isEmpty() || newValue.all { it.isDigit() }) {
                            timeInput = newValue
                        }
                    },
                    label = { Text("Tiempo (segundos)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !uiState.isLoading,
                    supportingText = {
                        Text("Rango válido: 5 a 120 segundos")
                    },
                    isError = timeInput.isNotEmpty() && 
                              (timeInput.toIntOrNull()?.let { it !in 5..120 } ?: true)
                )
                
                Button(
                    onClick = {
                        val seconds = timeInput.toIntOrNull()
                        if (seconds != null && seconds in 5..120) {
                            viewModel.setClosingTime(seconds)
                        }
                    },
                    modifier = Modifier.align(Alignment.End),
                    enabled = !uiState.isLoading && 
                              timeInput.isNotEmpty() && 
                              timeInput.toIntOrNull()?.let { it in 5..120 } ?: false
                ) {
                    Text("Guardar Tiempo")
                }
            }
        }
        
        // Modo de manipulación
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Modo de Manipulación",
                    style = MaterialTheme.typography.titleMedium
                )
                
                Text(
                    text = "Selecciona cómo se debe comportar el portón",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                ManipulationMode.values().forEach { mode ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = portonState.modoManipulacion == mode,
                            onClick = { viewModel.setManipulationMode(mode) },
                            enabled = !uiState.isLoading
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = when (mode) {
                                    ManipulationMode.AUTOMATIC -> "Automático"
                                    ManipulationMode.MANUAL -> "Manual"
                                    ManipulationMode.DISABLED -> "Desactivado"
                                },
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Text(
                                text = when (mode) {
                                    ManipulationMode.AUTOMATIC -> "El portón se cierra automáticamente después del tiempo configurado"
                                    ManipulationMode.MANUAL -> "El portón debe cerrarse manualmente"
                                    ManipulationMode.DISABLED -> "El portón está desactivado"
                                },
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
        
        // Indicador de carga
        if (uiState.isLoading) {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
    }
}
