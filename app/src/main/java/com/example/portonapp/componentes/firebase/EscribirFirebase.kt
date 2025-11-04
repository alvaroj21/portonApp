package com.example.portonapp.componentes.firebase

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.database.database

/**
 * Función para escribir datos en Firebase
 * @param field Campo de Firebase donde escribir
 * @param value Valor a escribir (debe ser un data class compatible con Firebase)
 * @param onSuccess Callback ejecutado en caso de éxito
 * @param onError Callback ejecutado en caso de error
 */
fun <T> escribirFirebase(
    field: String,
    value: T,
    onSuccess: () -> Unit = {},
    onError: (String) -> Unit = {}
) {
    val database = Firebase.database
    val myRef = database.getReference(field)

    Log.d("FirebaseWrite", "Iniciando escritura en: $field")
    Log.d("FirebaseWrite", "Objeto a escribir: $value")

    myRef.setValue(value)
        .addOnSuccessListener {
            Log.d("FirebaseWrite", "✅ Datos escritos exitosamente en $field")
            onSuccess()
        }
        .addOnFailureListener { error ->
            Log.e("FirebaseWrite", "❌ Error escribiendo en $field", error)
            onError("Error: ${error.message}")
        }
}
