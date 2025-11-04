package com.example.portonapp.data.model

/**
 * Modelo de usuario para autenticación
 */
data class User(
    val username: String = "",
    val email: String = "",
    val isAuthenticated: Boolean = false
)

