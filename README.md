# Aplicación de Control de Portón

Aplicación Android desarrollada con Kotlin y Jetpack Compose para el control y monitoreo de un portón automatizado.

## Características

- ✅ **Pantalla de Login**: Autenticación con usuario y contraseña
- ✅ **Control de Portón**: 
  - Abrir/Cerrar portón
  - Cambio de modo de manipulación (Automático/Manual/Desactivar)
  - Configuración de tiempo de cierre (5-120 segundos)
- ✅ **Monitoreo de Sensores**:
  - Sensor de distancia en tiempo real
  - Sensor de luz en tiempo real
- ✅ **Integración con Firebase**: Preparado para base de datos en tiempo real
- ✅ **Material Design**: UI moderna siguiendo las guías de Material Design 3

## Estructura del Proyecto

```
app/src/main/java/com/example/portonapp/
├── data/
│   ├── model/
│   │   ├── PortonState.kt      # Estado del portón
│   │   ├── SensorData.kt       # Datos de sensores
│   │   └── User.kt             # Modelo de usuario
│   └── repository/
│       ├── PortonRepository.kt # Repositorio para portón y sensores
│       └── AuthRepository.kt   # Repositorio para autenticación
├── viewmodel/
│   ├── AuthViewModel.kt        # ViewModel para Login
│   ├── ControlViewModel.kt     # ViewModel para Control
│   └── MonitoringViewModel.kt  # ViewModel para Monitoreo
├── ui/
│   ├── login/
│   │   └── LoginScreen.kt      # Pantalla de Login
│   ├── control/
│   │   └── ControlScreen.kt    # Pantalla de Control
│   ├── monitoring/
│   │   └── MonitoringScreen.kt  # Pantalla de Monitoreo
│   ├── navigation/
│   │   └── Navigation.kt       # Configuración de navegación
│   └── theme/                  # Temas Material Design
└── MainActivity.kt              # Actividad principal
```

## Configuración

### 1. Configurar Firebase

Para habilitar Firebase en la aplicación:

1. Crea un proyecto en [Firebase Console](https://console.firebase.google.com/)
2. Agrega una aplicación Android con el package name: `com.example.portonapp`
3. Descarga el archivo `google-services.json`
4. Colócalo en `app/google-services.json`

La aplicación funcionará con datos simulados si Firebase no está configurado.

### 2. Credenciales de Prueba

La aplicación incluye credenciales de prueba para desarrollo:

- **Usuario**: `admin`
- **Contraseña**: `admin123`

Otras credenciales disponibles:
- `usuario` / `usuario123`
- `test` / `test123`

## Tecnologías Utilizadas

- **Kotlin**: Lenguaje de programación
- **Jetpack Compose**: Framework de UI moderna
- **Material Design 3**: Sistema de diseño
- **Navigation Compose**: Navegación entre pantallas
- **ViewModel**: Gestión de estado y lógica de negocio
- **Firebase**: Base de datos en tiempo real y autenticación
- **Coroutines**: Programación asíncrona

## Criterios de Evaluación Cumplidos

### 2.1.1.1. Pantalla de Login ✅
- Implementa pantalla de Login con credenciales (usuario y contraseña)
- Utiliza herramientas de desarrollo móvil específicas para Android

### 2.1.1.2. Pantallas de Monitoreo y Activador ✅
- Desarrolla pantallas aplicando Material Design
- Muestra datos recibidos y permite enviar datos mediante elementos interactivos:
  - EditTexts (Login)
  - Sliders (Tiempo de cierre)
  - RadioButtons (Modo de manipulación)
  - Switches implícitos (Abrir/Cerrar portón)

### Preparado para:
- Conexiones inalámbricas (Firebase como base para Bluetooth/Wi-Fi)
- Medidas de seguridad (estructura lista para implementar ISO/IEC 27001)
- Interconexión entre aplicaciones (arquitectura preparada)

## Desarrollo Futuro

- [ ] Conexión real con Arduino vía Bluetooth/Wi-Fi
- [ ] Implementación de seguridad ISO/IEC 27001
- [ ] Interconexión entre múltiples aplicaciones
- [ ] Notificaciones push
- [ ] Historial de eventos

## Autor

Proyecto universitario - Sistema de Control de Portón IoT

