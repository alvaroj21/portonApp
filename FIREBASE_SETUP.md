# Configuración de Firebase

## Pasos para configurar Firebase en la aplicación

### 1. Crear proyecto en Firebase Console

1. Ve a [Firebase Console](https://console.firebase.google.com/)
2. Crea un nuevo proyecto o selecciona uno existente
3. Completa el proceso de creación

### 2. Agregar aplicación Android

1. En el proyecto de Firebase, haz clic en "Agregar app" y selecciona Android
2. Ingresa los siguientes datos:
   - **Package name**: `com.example.portonapp`
   - **App nickname** (opcional): Control de Portón
   - **Debug signing certificate SHA-1** (opcional): Puedes obtenerlo con el comando:
     ```
     keytool -list -v -keystore "%USERPROFILE%\.android\debug.keystore" -alias androiddebugkey -storepass android -keypass android
     ```
3. Haz clic en "Registrar app"

### 3. Descargar google-services.json

1. Descarga el archivo `google-services.json`
2. Colócalo en la carpeta `app/` del proyecto (al mismo nivel que `build.gradle.kts`)

### 4. Habilitar servicios de Firebase

En la consola de Firebase, habilita los siguientes servicios:

#### Firebase Realtime Database
1. Ve a "Realtime Database" en el menú lateral
2. Crea una base de datos en modo de prueba
3. Configura las reglas de seguridad (para desarrollo):

```json
{
  "rules": {
    ".read": true,
    ".write": true
  }
}
```

**⚠️ IMPORTANTE**: Estas reglas permiten lectura/escritura a todos. Para producción, configura reglas de seguridad apropiadas.

#### Firebase Authentication (opcional)
1. Ve a "Authentication" en el menú lateral
2. Habilita "Email/Password" como método de autenticación

### 5. Estructura de datos en Firebase

La aplicación espera la siguiente estructura en Realtime Database:

```
{
  "porton": {
    "estaAbierto": false,
    "modoManipulacion": "MANUAL",
    "tiempoCierreSegundos": 30,
    "ultimaActualizacion": 1234567890,
    "segundosRestantesCierreAuto": 0
  },
  "sensors": {
    "distancia": 150.0,
    "nivelLuz": 50.0,
    "marcaTiempo": 1234567890
  },
  "actuator_control": {
    "habilitado": false,
    "intensidad": 0,
    "ultimaActualizacion": 0,
    "modo": "manual"
  }
}
```

**Nota**: Todos los campos están en español para facilitar la comprensión y mantenimiento del código.

### Nota sobre datos simulados

Si no configuras Firebase, la aplicación funcionará con datos simulados automáticamente. Esto es útil para desarrollo y pruebas sin necesidad de una conexión a Firebase.

