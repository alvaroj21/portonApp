package com.example.portonapp.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.portonapp.componentes.firebase.escribirFirebase
import com.example.portonapp.data.model.SensorData
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay

/**
 * ViewModel para el monitoreo de sensores
 * Usa los componentes base de Firebase (LeerFirebase/escribirFirebase)
 */
class MonitoringViewModel : ViewModel() {
    
    private val _uiState = MutableStateFlow(MonitoringUiState())
    val uiState: StateFlow<MonitoringUiState> = _uiState.asStateFlow()
    
    // Referencia a Firebase siguiendo el patrón de los componentes
    private val database = Firebase.database
    private val sensorsRef = database.getReference("sensors")
    
    // Flag para evitar bucles cuando simulamos sensores
    private var isSimulatingLocally = false
    
    init {
        observeSensorData()
        // Comentado: La simulación debe correr en el hardware real (ESP32/Arduino)
        // Solo descomentar para pruebas sin hardware
        // startSensorSimulation()
    }
    
    /**
     * Observa cambios en tiempo real desde Firebase
     * Sigue el patrón establecido en LeerFirebase.kt
     */
    private fun observeSensorData() {
        Log.d("MonitoringViewModel", "Iniciando observación de sensors")
        
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                try {
                    // Solo actualizar si no estamos simulando localmente
                    if (!isSimulatingLocally) {
                        Log.d("MonitoringViewModel", "Datos recibidos de Firebase")
                        val data = snapshot.getValue(SensorData::class.java) ?: SensorData()
                        
                        _uiState.value = _uiState.value.copy(
                            sensorData = data,
                            isLoading = false,
                            errorMessage = null
                        )
                        
                        Log.d("MonitoringViewModel", "Sensores actualizados: $data")
                    }
                } catch (e: Exception) {
                    Log.e("MonitoringViewModel", "Error parseando datos", e)
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Error: ${e.message}"
                    )
                }
            }
            
            override fun onCancelled(error: DatabaseError) {
                Log.w("MonitoringViewModel", "Lectura cancelada", error.toException())
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Error: ${error.message}"
                )
            }
        }
        
        sensorsRef.addValueEventListener(listener)
    }
    
    /**
     * Simular actualizaciones de sensores (solo para pruebas sin hardware)
     * Usa escribirFirebase siguiendo el patrón de EscribirFirebase.kt
     * NOTA: En producción, los sensores reales (ESP32/Arduino) deben actualizar Firebase
     */
    fun startSensorSimulation() {
        viewModelScope.launch {
            Log.d("MonitoringViewModel", "⚠️ Iniciando simulación de sensores (modo prueba)")
            
            while (true) {
                delay(3000) // Cada 3 segundos (evita sobrecarga)
                isSimulatingLocally = true
                
                val newData = SensorData(
                    distancia = (50..300).random().toFloat(),
                    nivelLuz = (0..100).random().toFloat(),
                    marcaTiempo = System.currentTimeMillis()
                )
                
                escribirFirebase(
                    field = "sensors",
                    value = newData,
                    onSuccess = {
                        Log.d("MonitoringViewModel", "✅ Datos de sensor simulados escritos")
                    },
                    onError = { error ->
                        Log.e("MonitoringViewModel", "❌ Error escribiendo sensores: $error")
                        _uiState.value = _uiState.value.copy(errorMessage = error)
                    }
                )
                
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
        Log.d("MonitoringViewModel", "Deteniendo simulación de sensores")
        isSimulatingLocally = false
    }
    
    /**
     * Limpia el mensaje de error
     */
    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}

/**
 * Estado de la UI de monitoreo
 * Sigue el patrón de Triple<T?, Boolean, String?> de LeerFirebase
 */
data class MonitoringUiState(
    val sensorData: SensorData = SensorData(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)

