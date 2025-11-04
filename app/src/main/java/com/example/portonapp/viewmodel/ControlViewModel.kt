package com.example.portonapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.portonapp.componentes.firebase.escribirFirebase
import com.example.portonapp.data.model.ManipulationMode
import com.example.portonapp.data.model.PortonState
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay

/**
 * ViewModel simplificado - usa Firebase directamente
 */
class ControlViewModel : ViewModel() {
    
    private val _uiState = MutableStateFlow(ControlUiState())
    val uiState: StateFlow<ControlUiState> = _uiState.asStateFlow()
    
    // Job para el temporizador de cierre automático
    private var autoCloseJob: Job? = null
    
    private val database = FirebaseDatabase.getInstance().reference
    
    init {
        observePortonState()
    }
    
    // Escuchar cambios en tiempo real desde Firebase
    private fun observePortonState() {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val state = snapshot.getValue(PortonState::class.java) ?: PortonState()
                _uiState.value = _uiState.value.copy(
                    portonState = state,
                    isLoading = false
                )
                handleAutoClose(state)
            }
            
            override fun onCancelled(error: DatabaseError) {
                // Manejar error si es necesario
            }
        }
        database.child("porton").addValueEventListener(listener)
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
            
            // Actualizar el estado inicial con el tiempo restante
            updateAutoCloseTimer(remainingSeconds)
            
            autoCloseJob = viewModelScope.launch {
                // Contar hacia atrás cada segundo
                while (remainingSeconds > 0) {
                    delay(1000L) // Esperar 1 segundo
                    remainingSeconds--
                    
                    // Actualizar el contador en Firebase cada segundo
                    updateAutoCloseTimer(remainingSeconds)
                    
                    // Verificar que aún esté abierto y en modo automático
                    val currentState = _uiState.value.portonState
                    if (!currentState.estaAbierto || 
                        currentState.modoManipulacion != ManipulationMode.AUTOMATIC) {
                        // Cancelar si cambió el estado o modo
                        updateAutoCloseTimer(0)
                        return@launch
                    }
                }
                
                // Cuando llega a 0, cerrar automáticamente
                val currentState = _uiState.value.portonState
                if (currentState.estaAbierto && 
                    currentState.modoManipulacion == ManipulationMode.AUTOMATIC) {
                    updateAutoCloseTimer(0)
                    // Cerrar automáticamente
                    closePorton()
                }
            }
        } else {
            // Si no está en modo automático o está cerrado, resetear el contador
            updateAutoCloseTimer(0)
        }
    }
    
    // Actualizar contador en Firebase
    private fun updateAutoCloseTimer(remainingSeconds: Int) {
        val currentState = _uiState.value.portonState
        val updatedState = currentState.copy(
            segundosRestantesCierreAuto = remainingSeconds,
            ultimaActualizacion = System.currentTimeMillis()
        )
        escribirFirebase("porton", updatedState)
    }
    
    fun openPorton() {
        val currentState = _uiState.value.portonState
        if (!currentState.estaAbierto) {
            val newState = currentState.copy(
                estaAbierto = true,
                ultimaActualizacion = System.currentTimeMillis()
            )
            escribirFirebase("porton", newState)
        }
    }
    
    fun closePorton() {
        val currentState = _uiState.value.portonState
        if (currentState.estaAbierto) {
            autoCloseJob?.cancel()
            autoCloseJob = null
            
            val newState = currentState.copy(
                estaAbierto = false,
                ultimaActualizacion = System.currentTimeMillis()
            )
            escribirFirebase("porton", newState)
        }
    }
    
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
        escribirFirebase("porton", newState)
    }
    
    fun setClosingTime(seconds: Int) {
        val currentState = _uiState.value.portonState
        val newState = currentState.copy(
            tiempoCierreSegundos = seconds,
            ultimaActualizacion = System.currentTimeMillis()
        )
        escribirFirebase("porton", newState)
    }
}

/**
 * Estado de la UI de control
 */
data class ControlUiState(
    val portonState: PortonState = PortonState(),
    val isLoading: Boolean = false
)

