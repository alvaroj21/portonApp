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
    
    init {
        observeSensorData()
        startSensorSimulation()
    }
    
    // Escuchar cambios en tiempo real desde Firebase
    private fun observeSensorData() {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val data = snapshot.getValue(SensorData::class.java) ?: SensorData()
                _uiState.value = _uiState.value.copy(
                    sensorData = data,
                    isLoading = false
                )
            }
            
            override fun onCancelled(error: DatabaseError) {
                // Manejar error si es necesario
            }
        }
        database.child("sensors").addValueEventListener(listener)
    }
    
    // Simular actualizaciones de sensores
    private fun startSensorSimulation() {
        viewModelScope.launch {
            while (true) {
                delay(2000) // Cada 2 segundos
                val newData = SensorData(
                    distancia = (50..300).random().toFloat(),
                    nivelLuz = (0..100).random().toFloat(),
                    marcaTiempo = System.currentTimeMillis()
                )
                escribirFirebase("sensors", newData)
            }
        }
    }
}

/**
 * Estado de la UI de monitoreo
 */
data class MonitoringUiState(
    val sensorData: SensorData = SensorData(),
    val isLoading: Boolean = false
)

