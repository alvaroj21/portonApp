package com.example.portonapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.portonapp.componentes.firebase.escribirFirebase
import com.example.portonapp.data.model.SensorData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay

/**
 * ViewModel simplificado - usa Firebase directamente
 */
class MonitoringViewModel : ViewModel() {
    
    private val _uiState = MutableStateFlow(MonitoringUiState())
    val uiState: StateFlow<MonitoringUiState> = _uiState.asStateFlow()
    
    private val database = FirebaseDatabase.getInstance().reference
    
    // Flag para evitar bucles cuando simulamos sensores
    private var isSimulatingLocally = false
    
    init {
        observeSensorData()
        // Comentado: La simulación debe correr en el hardware, no en la app
        // startSensorSimulation()
    }
    
    // Escuchar cambios en tiempo real desde Firebase
    private fun observeSensorData() {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // Solo actualizar si no estamos simulando localmente
                if (!isSimulatingLocally) {
                    val data = snapshot.getValue(SensorData::class.java) ?: SensorData()
                    _uiState.value = _uiState.value.copy(
                        sensorData = data,
                        isLoading = false
                    )
                }
            }
            
            override fun onCancelled(error: DatabaseError) {
                // Manejar error si es necesario
            }
        }
        database.child("sensors").addValueEventListener(listener)
    }
    
    /**
     * Simular actualizaciones de sensores (solo para pruebas)
     * NOTA: En producción, los sensores reales deben actualizar Firebase
     */
    fun startSensorSimulation() {
        viewModelScope.launch {
            while (true) {
                delay(3000) // Cada 3 segundos (aumentado para evitar sobrecarga)
                isSimulatingLocally = true
                
                val newData = SensorData(
                    distancia = (50..300).random().toFloat(),
                    nivelLuz = (0..100).random().toFloat(),
                    marcaTiempo = System.currentTimeMillis()
                )
                escribirFirebase("sensors", newData)
                
                // Esperar un poco antes de permitir que el listener actualice
                delay(200)
                isSimulatingLocally = false
            }
        }
    }
    
    /**
     * Detener la simulación de sensores
     */
    fun stopSensorSimulation() {
        isSimulatingLocally = false
    }
}

/**
 * Estado de la UI de monitoreo
 */
data class MonitoringUiState(
    val sensorData: SensorData = SensorData(),
    val isLoading: Boolean = false
)

