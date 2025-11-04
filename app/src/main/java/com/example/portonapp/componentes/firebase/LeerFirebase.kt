package com.example.portonapp.componentes.firebase

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import kotlinx.coroutines.awaitCancellation

/**
 * Composable para leer datos de Firebase en tiempo real
 * @param field Campo de Firebase a leer
 * @param valueType Clase del tipo de datos a leer
 * @return Triple con el valor actual, estado de carga y mensaje de error
 */
@Composable
fun <T> LeerFirebase(
    field: String,
    valueType: Class<T>
): Triple<T?, Boolean, String?> {
    var currentValue by rememberSaveable { mutableStateOf<T?>(null) }
    var isLoading by rememberSaveable { mutableStateOf(true) }
    var errorMessage by rememberSaveable { mutableStateOf<String?>(null) }

    val database = Firebase.database
    val myRef = database.getReference(field)

    LaunchedEffect(field) {
        Log.d("FirebaseRead", "Iniciando lectura de: $field")

        val valueEventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                try {
                    Log.d("FirebaseRead", "Datos recibidos de $field")
                    val value = snapshot.getValue(valueType)
                    currentValue = value
                    isLoading = false
                    errorMessage = null
                    Log.d("FirebaseRead", "Objeto actualizado: $value")
                } catch (e: Exception) {
                    errorMessage = "Error parsing data: ${e.message}"
                    isLoading = false
                    Log.e("FirebaseRead", "Error parseando datos", e)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                errorMessage = "Error: ${error.message}"
                isLoading = false
                Log.w("FirebaseRead", "Lectura cancelada", error.toException())
            }
        }

        myRef.addValueEventListener(valueEventListener)
        awaitCancellation()
    }

    return Triple(currentValue, isLoading, errorMessage)
}
