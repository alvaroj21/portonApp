package com.example.portonapp.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.portonapp.componentes.firebase.escribirFirebase
import com.example.portonapp.data.model.ManipulationMode
import com.example.portonapp.data.model.PortonState
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay

/**
 * ViewModel para el control del portón
 * Usa los componentes base de Firebase (LeerFirebase/escribirFirebase)
 */
class ControlViewModel : ViewModel() {
    
    private val _uiState = MutableStateFlow(ControlUiState())
    val uiState: StateFlow<ControlUiState> = _uiState.asStateFlow()
    
    // Job para el temporizador de cierre automático
    private var autoCloseJob: Job? = null
    
    // Referencia a Firebase siguiendo el patrón de los componentes
    private val database = Firebase.database
    private val portonRef = database.getReference("porton")
    
    init {
        observePortonState()
    }
    
    /**
     * Observa cambios en tiempo real desde Firebase
     * Sigue el patrón establecido en LeerFirebase.kt
     */
    private fun observePortonState() {
        Log.d("ControlViewModel", "Iniciando observación de porton")
        
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                try {
                    Log.d("ControlViewModel", "Datos recibidos de Firebase")
                    val state = snapshot.getValue(PortonState::class.java) ?: PortonState()
                    
                    _uiState.value = _uiState.value.copy(
                        portonState = state,
                        isLoading = false,
                        errorMessage = null
                    )
                    
                    Log.d("ControlViewModel", "Estado actualizado: $state")
                    handleAutoClose(state)
                    
                } catch (e: Exception) {
                    Log.e("ControlViewModel", "Error parseando datos", e)
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Error: ${e.message}"
                    )
                }
            }
            
            override fun onCancelled(error: DatabaseError) {
                Log.w("ControlViewModel", "Lectura cancelada", error.toException())
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Error: ${error.message}"
                )
            }
        }
        
        portonRef.addValueEventListener(listener)
    }
    
    /**
     * Maneja el cierre automático cuando el modo es AUTOMATIC
     */
    private fun handleAutoClose(state: PortonState) {
        // Cancelar cualquier temporizador anterior
        autoCloseJob?.cancel()
        autoCloseJob = null
        
        // Si el portón está abierto y el modo es AUTOMATIC, iniciar temporizador
        if (state.estaAbierto && state.modoManipulacion == ManipulationMode.AUTOMATIC) {
            val closingTime = state.tiempoCierreSegundos
            var remainingSeconds = closingTime
            
            // Actualizar el estado local inicial con el tiempo restante
            updateLocalTimer(remainingSeconds)
            
            autoCloseJob = viewModelScope.launch {
                // Contar hacia atrás cada segundo
                while (remainingSeconds > 0) {
                    delay(1000L) // Esperar 1 segundo
                    remainingSeconds--
                    
                    // Actualizar solo el estado LOCAL, no Firebase (evita bucle)
                    updateLocalTimer(remainingSeconds)
                    
                    // Verificar que aún esté abierto y en modo automático
                    val currentState = _uiState.value.portonState
                    if (!currentState.estaAbierto || 
                        currentState.modoManipulacion != ManipulationMode.AUTOMATIC) {
                        // Cancelar si cambió el estado o modo
                        updateLocalTimer(0)
                        return@launch
                    }
                }
                
                // Cuando llega a 0, cerrar automáticamente
                val currentState = _uiState.value.portonState
                if (currentState.estaAbierto && 
                    currentState.modoManipulacion == ManipulationMode.AUTOMATIC) {
                    updateLocalTimer(0)
                    // Cerrar automáticamente
                    closePorton()
                }
            }
        } else {
            // Si no está en modo automático o está cerrado, resetear el contador
            updateLocalTimer(0)
        }
    }
    
    // Actualizar contador SOLO localmente (no escribe a Firebase)
    private fun updateLocalTimer(remainingSeconds: Int) {
        val currentState = _uiState.value.portonState
        val updatedState = currentState.copy(
            segundosRestantesCierreAuto = remainingSeconds
        )
        _uiState.value = _uiState.value.copy(portonState = updatedState)
    }
    
    /**
     * Abre el portón
     * Usa escribirFirebase siguiendo el patrón de EscribirFirebase.kt
     */
    fun openPorton() {
        val currentState = _uiState.value.portonState
        if (!currentState.estaAbierto) {
            val newState = currentState.copy(
                estaAbierto = true,
                ultimaActualizacion = System.currentTimeMillis()
            )
            
            escribirFirebase(
                field = "porton",
                value = newState,
                onSuccess = {
                    Log.d("ControlViewModel", "✅ Portón abierto exitosamente")
                },
                onError = { error ->
                    Log.e("ControlViewModel", "❌ Error abriendo portón: $error")
                    _uiState.value = _uiState.value.copy(errorMessage = error)
                }
            )
        }
    }
    
    /**
     * Cierra el portón
     * Usa escribirFirebase siguiendo el patrón de EscribirFirebase.kt
     */
    fun closePorton() {
        val currentState = _uiState.value.portonState
        if (currentState.estaAbierto) {
            autoCloseJob?.cancel()
            autoCloseJob = null
            
            val newState = currentState.copy(
                estaAbierto = false,
                ultimaActualizacion = System.currentTimeMillis()
            )
            
            escribirFirebase(
                field = "porton",
                value = newState,
                onSuccess = {
                    Log.d("ControlViewModel", "✅ Portón cerrado exitosamente")
                },
                onError = { error ->
                    Log.e("ControlViewModel", "❌ Error cerrando portón: $error")
                    _uiState.value = _uiState.value.copy(errorMessage = error)
                }
            )
        }
    }
    
    /**
     * Cambia el modo de manipulación
     * Usa escribirFirebase siguiendo el patrón de EscribirFirebase.kt
     */
    fun setManipulationMode(mode: ManipulationMode) {
        if (mode != ManipulationMode.AUTOMATIC) {
            autoCloseJob?.cancel()
            autoCloseJob = null
        }
        
        val currentState = _uiState.value.portonState
        val newState = currentState.copy(
            modoManipulacion = mode,
            ultimaActualizacion = System.currentTimeMillis()
        )
        
        escribirFirebase(
            field = "porton",
            value = newState,
            onSuccess = {
                Log.d("ControlViewModel", "✅ Modo cambiado a $mode exitosamente")
            },
            onError = { error ->
                Log.e("ControlViewModel", "❌ Error cambiando modo: $error")
                _uiState.value = _uiState.value.copy(errorMessage = error)
            }
        )
    }
    
    /**
     * Cambia el tiempo de cierre automático
     * Usa escribirFirebase siguiendo el patrón de EscribirFirebase.kt
     */
    fun setClosingTime(seconds: Int) {
        val currentState = _uiState.value.portonState
        val newState = currentState.copy(
            tiempoCierreSegundos = seconds,
            ultimaActualizacion = System.currentTimeMillis()
        )
        
        escribirFirebase(
            field = "porton",
            value = newState,
            onSuccess = {
                Log.d("ControlViewModel", "✅ Tiempo de cierre cambiado a $seconds segundos")
            },
            onError = { error ->
                Log.e("ControlViewModel", "❌ Error cambiando tiempo: $error")
                _uiState.value = _uiState.value.copy(errorMessage = error)
            }
        )
    }
    
    /**
     * Limpia el mensaje de error
     */
    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}

/**
 * Estado de la UI de control
 * Sigue el patrón de Triple<T?, Boolean, String?> de LeerFirebase
 */
data class ControlUiState(
    val portonState: PortonState = PortonState(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)

