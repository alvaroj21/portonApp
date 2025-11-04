package com.example.portonapp.ui.monitoring

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.portonapp.viewmodel.MonitoringViewModel

/**
 * Pantalla de Monitoreo de Sensores
 * Muestra datos del sensor de distancia y sensor de luz en tiempo real
 */
@Composable
fun MonitoringScreen(
    viewModel: MonitoringViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    val sensorData = uiState.sensorData
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Título
        Text(
            text = "Monitoreo de Sensores",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        // Sensor de Distancia
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Straighten,
                        contentDescription = "Sensor de Distancia",
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Sensor de Distancia",
                        style = MaterialTheme.typography.titleLarge
                    )
                }
                
                Text(
                    text = "${sensorData.distancia.toInt()} cm",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                
                // Barra de progreso visual
                LinearProgressIndicator(
                    progress = { 
                        // Normalizar distancia (0-300 cm) a 0-1
                        (sensorData.distancia.coerceIn(0f, 300f) / 300f)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )
                
                Text(
                    text = "Rango: 0 - 300 cm",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                // Indicador de estado
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = if (sensorData.distancia < 100) 
                            Icons.Default.Warning 
                        else 
                            Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = if (sensorData.distancia < 100) 
                            MaterialTheme.colorScheme.error 
                        else 
                            MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = if (sensorData.distancia < 100) 
                            "Objeto cercano detectado" 
                        else 
                            "Distancia normal",
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (sensorData.distancia < 100) 
                            MaterialTheme.colorScheme.error 
                        else 
                            MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
        
        // Sensor de Luz
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.WbSunny,
                        contentDescription = "Sensor de Luz",
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Sensor de Luz",
                        style = MaterialTheme.typography.titleLarge
                    )
                }
                
                Text(
                    text = "${sensorData.nivelLuz.toInt()}%",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                
                // Barra de progreso visual
                LinearProgressIndicator(
                    progress = { sensorData.nivelLuz / 100f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )
                
                Text(
                    text = "Rango: 0 - 100%",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                // Indicador de estado
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = if (sensorData.nivelLuz < 30) 
                            Icons.Default.NightsStay 
                        else if (sensorData.nivelLuz > 70) 
                            Icons.Default.WbSunny 
                        else 
                            Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = when {
                            sensorData.nivelLuz < 30 -> MaterialTheme.colorScheme.secondary
                            sensorData.nivelLuz > 70 -> MaterialTheme.colorScheme.primary
                            else -> MaterialTheme.colorScheme.primary
                        },
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = when {
                            sensorData.nivelLuz < 30 -> "Luz baja (Noche)"
                            sensorData.nivelLuz > 70 -> "Luz alta (Día)"
                            else -> "Luz moderada"
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
        
        // Información de última actualización
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Update,
                        contentDescription = "Actualización",
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "Actualización en tiempo real",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                
                if (uiState.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}

