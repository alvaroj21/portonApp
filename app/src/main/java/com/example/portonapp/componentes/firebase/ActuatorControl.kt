package com.example.portonapp.componentes.firebase

/**
 * Modelo de datos para el control del actuador
 */
data class ActuatorControl(
    var habilitado: Boolean = false,
    var intensidad: Int = 0,
    var ultimaActualizacion: Int = 0,
    var modo: String = "manual"
) {
    // Constructor vacío OBLIGATORIO para Firebase
    constructor() : this(false, 0, 0, "manual")
}
