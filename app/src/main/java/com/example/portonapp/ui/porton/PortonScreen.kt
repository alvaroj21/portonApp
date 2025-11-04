package com.example.portonapp.ui.porton

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.portonapp.data.model.ManipulationMode
import com.example.portonapp.viewmodel.ControlViewModel

/**
 * Pantalla de Portón
 * Permite abrir/cerrar el portón y muestra el contador de cierre automático
 */
@Composable
fun PortonScreen(
    viewModel: ControlViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    val portonState = uiState.portonState
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Título
        Text(
            text = "Control de Portón",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        // Estado actual del portón
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Estado:",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = if (portonState.estaAbierto) "ABIERTO" else "CERRADO",
                        style = MaterialTheme.typography.titleLarge,
                        color = if (portonState.estaAbierto) 
                            MaterialTheme.colorScheme.primary 
                        else 
                            MaterialTheme.colorScheme.error
                    )
                }
                
                // Mostrar contador de cierre automático
                if (portonState.estaAbierto && 
                    portonState.modoManipulacion == ManipulationMode.AUTOMATIC &&
                    portonState.segundosRestantesCierreAuto > 0) {
                    Divider(modifier = Modifier.padding(vertical = 8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Cierre automático en:",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "${portonState.segundosRestantesCierreAuto}s",
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
        
        // Botones para abrir/cerrar portón
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = { viewModel.openPorton() },
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp),
                enabled = !uiState.isLoading 
                    && portonState.modoManipulacion != ManipulationMode.DISABLED
                    && !portonState.estaAbierto,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text("Abrir Portón")
            }
            
            Button(
                onClick = { viewModel.closePorton() },
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp),
                enabled = !uiState.isLoading 
                    && portonState.modoManipulacion != ManipulationMode.DISABLED
                    && portonState.estaAbierto,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text("Cerrar Portón")
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
