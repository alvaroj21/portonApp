package com.example.portonapp.data.model

/**
 * Datos de los sensores
 */
data class SensorData(
    val distancia: Float = 0f,  // Distancia en cm
    val nivelLuz: Float = 0f, // Nivel de luz (0-100)
    val marcaTiempo: Long = System.currentTimeMillis()
) {
    // Constructor vacío OBLIGATORIO para Firebase
    constructor() : this(0f, 0f, System.currentTimeMillis())
}

