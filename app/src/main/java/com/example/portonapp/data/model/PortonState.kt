package com.example.portonapp.data.model

/**
 * Estado del portón
 */
data class PortonState(
    val estaAbierto: Boolean = false,
    val modoManipulacion: ManipulationMode = ManipulationMode.MANUAL,
    val tiempoCierreSegundos: Int = 30,
    val ultimaActualizacion: Long = System.currentTimeMillis(),
    val segundosRestantesCierreAuto: Int = 0 // Tiempo restante para cierre automático (0 = no activo)
) {
    // Constructor vacío OBLIGATORIO para Firebase
    constructor() : this(false, ManipulationMode.MANUAL, 30, System.currentTimeMillis(), 0)
}

/**
 * Modo de manipulación del portón
 */
enum class ManipulationMode {
    AUTOMATIC,  // Automático
    MANUAL,     // Manual
    DISABLED    // Desactivado
}

