package com.example.portonapp.ui.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.portonapp.ui.porton.PortonScreen
import com.example.portonapp.ui.config.ConfigScreen
import com.example.portonapp.ui.login.LoginScreen
import com.example.portonapp.ui.monitoring.MonitoringScreen
import com.example.portonapp.viewmodel.AuthViewModel
import com.example.portonapp.viewmodel.ControlViewModel
import com.example.portonapp.viewmodel.MonitoringViewModel

/**
 * Rutas de navegación de la aplicación
 */
sealed class Screen(val route: String, val title: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    object Login : Screen("login", "Login", Icons.Default.Person)
    object Porton : Screen("porton", "Portón", Icons.Default.DoorFront)
    object Config : Screen("config", "Configuración", Icons.Default.Settings)
    object Monitoring : Screen("monitoring", "Monitoreo", Icons.Default.Visibility)
}

/**
 * Navegación principal de la aplicación
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    controlViewModel: ControlViewModel,
    monitoringViewModel: MonitoringViewModel,
    startDestination: String = Screen.Login.route
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: Screen.Login.route
    
    val uiState by authViewModel.uiState.collectAsState()
    val isAuthenticated = uiState.isAuthenticated
    
    Scaffold(
        topBar = {
            if (isAuthenticated && currentRoute != Screen.Login.route) {
                TopAppBar(
                    title = { 
                        Text(
                            when (currentRoute) {
                                Screen.Porton.route -> Screen.Porton.title
                                Screen.Config.route -> Screen.Config.title
                                Screen.Monitoring.route -> Screen.Monitoring.title
                                else -> "Control de Portón"
                            }
                        )
                    },
                    actions = {
                        IconButton(onClick = { 
                            authViewModel.logout()
                            navController.navigate(Screen.Login.route) {
                                popUpTo(0) { inclusive = true }
                            }
                        }) {
                            Icon(
                                imageVector = Icons.Default.Logout,
                                contentDescription = "Cerrar Sesión"
                            )
                        }
                    }
                )
            }
        },
        bottomBar = {
            if (isAuthenticated && currentRoute != Screen.Login.route) {
                NavigationBar {
                    NavigationBarItem(
                        icon = { Icon(Screen.Porton.icon, contentDescription = Screen.Porton.title) },
                        label = { Text(Screen.Porton.title) },
                        selected = currentRoute == Screen.Porton.route,
                        onClick = {
                            navController.navigate(Screen.Porton.route) {
                                popUpTo(Screen.Porton.route) { inclusive = true }
                            }
                        }
                    )
                    NavigationBarItem(
                        icon = { Icon(Screen.Config.icon, contentDescription = Screen.Config.title) },
                        label = { Text(Screen.Config.title) },
                        selected = currentRoute == Screen.Config.route,
                        onClick = {
                            navController.navigate(Screen.Config.route) {
                                popUpTo(Screen.Config.route) { inclusive = true }
                            }
                        }
                    )
                    NavigationBarItem(
                        icon = { Icon(Screen.Monitoring.icon, contentDescription = Screen.Monitoring.title) },
                        label = { Text(Screen.Monitoring.title) },
                        selected = currentRoute == Screen.Monitoring.route,
                        onClick = {
                            navController.navigate(Screen.Monitoring.route) {
                                popUpTo(Screen.Monitoring.route) { inclusive = true }
                            }
                        }
                    )
                }
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(Screen.Login.route) {
                LoginScreen(
                    viewModel = authViewModel,
                    onLoginSuccess = {
                        navController.navigate(Screen.Porton.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    }
                )
            }
            
            composable(Screen.Porton.route) {
                PortonScreen(viewModel = controlViewModel)
            }
            
            composable(Screen.Config.route) {
                ConfigScreen(viewModel = controlViewModel)
            }
            
            composable(Screen.Monitoring.route) {
                MonitoringScreen(viewModel = monitoringViewModel)
            }
        }
    }
}
