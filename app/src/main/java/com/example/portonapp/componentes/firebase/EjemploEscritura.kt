package com.example.portonapp.componentes.firebase

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.KeyboardType

/**
 * Ejemplo de composable para escribir datos en Firebase
 */
@Composable
fun EjemploEscritura() {
    var valor by rememberSaveable { mutableStateOf(0) }
    Column {
        OutlinedTextField(
            value = valor.toString(),
            onValueChange = { newValue ->
                val intValue = newValue.toIntOrNull()
                if (intValue != null && intValue in 0..100) {
                    valor = intValue
                }
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        Button(
            onClick = {
                val valorEnviar = ActuatorControl(intensidad = valor)
                escribirFirebase(field = "actuator_control", value = valorEnviar)
            }
        ) {
            Text("Enviar")
        }
    }
}
