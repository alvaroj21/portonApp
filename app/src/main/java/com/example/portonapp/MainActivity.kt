package com.example.portonapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.portonapp.ui.navigation.AppNavigation
import com.example.portonapp.ui.navigation.Screen
import com.example.portonapp.ui.theme.PortonAppTheme
import com.example.portonapp.viewmodel.AuthViewModel
import com.example.portonapp.viewmodel.ControlViewModel
import com.example.portonapp.viewmodel.MonitoringViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        // Inicializar ViewModels
        val authViewModel = AuthViewModel()
        val controlViewModel = ControlViewModel()
        val monitoringViewModel = MonitoringViewModel()
        
        setContent {
            PortonAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    
                    AppNavigation(
                        navController = navController,
                        authViewModel = authViewModel,
                        controlViewModel = controlViewModel,
                        monitoringViewModel = monitoringViewModel,
                        startDestination = Screen.Login.route
                    )
                }
            }
        }
    }
}
