package com.example.portonapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.portonapp.data.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel simplificado - autenticación local
 */
class AuthViewModel : ViewModel() {
    
    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()
    
    // Credenciales válidas (podrías moverlas a Firebase si lo necesitas)
    private val validCredentials = mapOf(
        "admin" to "admin123",
        "usuario" to "usuario123",
        "test" to "test123"
    )
    
    fun login(username: String, password: String) {
        if (username.isBlank() || password.isBlank()) {
            _uiState.value = _uiState.value.copy(
                errorMessage = "Por favor complete todos los campos"
            )
            return
        }
        
        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
        
        // Validar credenciales
        if (validCredentials[username.lowercase()] == password) {
            val user = User(
                username = username,
                email = "$username@example.com",
                isAuthenticated = true
            )
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                isAuthenticated = true,
                currentUser = user,
                errorMessage = null
            )
        } else {
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                isAuthenticated = false,
                errorMessage = "❌ Datos incorrectos. Por favor, ingrese los datos nuevamente."
            )
        }
    }
    
    fun logout() {
        _uiState.value = _uiState.value.copy(
            isAuthenticated = false,
            currentUser = null
        )
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}

/**
 * Estado de la UI de autenticación
 */
data class AuthUiState(
    val isLoading: Boolean = false,
    val isAuthenticated: Boolean = false,
    val currentUser: User? = null,
    val errorMessage: String? = null
)

