# AirFlow - Android
> #### Proyecto Aplicaciones de Biometría y Medio Ambiente
---

## Descripción
AirFlow es una aplicación para Android diseñada para integrar la autenticación biométrica con el monitoreo ambiental. La aplicación aprovecha las tecnologías biométricas modernas para garantizar un acceso seguro y proporciona datos en tiempo real sobre la calidad del aire y otros factores ambientales.

## Especificaciones
- **Lenguajes**: Java, Kotlin
- **Frameworks**: Android SDK, AndroidX
- **Min SDK**: 26
- **Target SDK**: 34
- **Compile SDK**: 35

## **Instalación**
Para ejecutar este proyecto en tu entorno local, sigue estos pasos:

1. Clona este repositorio:
   ```bash
   git clone https://github.com/SentoMarcos/AirFlow-Android.git
   ```

2. Abre el proyecto en Android Studio.

3. Sincroniza Gradle y compila el proyecto.

4. Ejecuta la aplicación en un emulador o dispositivo físico con Bluetooth habilitado.

## Características principales

- **Gestión de sensores:** Registro, actualización y visualización de sensores utilizando el protocolo BTLE.
- **Autenticación biométrica:** Soporte para autenticación segura mediante huellas digitales u otros medios biométricos.
- **Gestión de usuarios:** Perfiles editables para cada usuario, con soporte para carga y edición de datos.
- **Análisis de datos de sensores:** Muestra valores y gráficos relacionados con mediciones ambientales.
- **Lectura de códigos QR:** Escaneo y decodificación de información útil para la interacción con sensores.
- **Notificaciones en tiempo real:** Soporte para notificaciones relacionadas con los dispositivos y sensores.

## Implementaciones
#### Biometría
```kotlin
implementation("androidx.biometric:biometric:1.2.0")
implementation("androidx.biometric:biometric-ktx:1.4.0-alpha02")
```

## Requisitos previos

Antes de empezar, asegúrate de tener lo siguiente:

- Android Studio instalado (versión recomendada: `Arctic Fox 2020.3.1` o posterior).
- JDK 8 o superior.
- Un dispositivo Android o emulador configurado.
- Conexión a un servidor compatible con las APIs definidas.

## Estructura del Proyecto

La estructura principal de los paquetes es la siguiente:

- **`API`**:
    - Interfaces y clientes para interactuar con las APIs REST, como `ApiService` y `RetrofitClient`.
- **`LOGIC`**:
    - Lógica de negocio y utilidades, incluyendo clases como `BiometricUtil` y `PeticionesUserUtil`.
- **`POJO`**:
    - Modelos de datos simples para manejar objetos como `Medicion`, `SensorObject`, y `User`.
- **`PRESENTACION`**:
    - Actividades y fragmentos para la interfaz gráfica del usuario, como `LogInActivity`, `HomeFragment`, y `MapFragment`.
- **`Services`**:
    - Servicios de fondo como `ArduinoGetterService` para gestionar dispositivos BTLE.

### Clases destacadas

1. **`ApiService`**: Define las operaciones para interactuar con el servidor, como login, edición de usuario y registro de sensores.
2. **`ArduinoGetterService`**: Servicio que escanea dispositivos BTLE y gestiona las conexiones.
3. **`BiometricUtil`**: Maneja la autenticación biométrica en dispositivos compatibles.

## Autores
- [SentoMarcos](https://github.com/SentoMarcos "SentoMarcos")
- [ferrangv](https://github.com/ferrangv "ferrangv")
- [pRebollo02](https://github.com/pRebollo02 "pRebollo02")
- [RogersRogersRogers](https://github.com/RogersRogersRogers "RogersRogersRogers")
- [RubyGa22](https://github.com/RubyGa22 "RubyGa22")

## Documentación
La documentación completa del proyecto se encuentra en el repositorio de GitHub, incluyendo guías de instalación, uso y contribución.

## Proyectos Relacionados
- [**AirFlow Web**](https://github.com/SentoMarcos/AirFlow-Web "**AirFlow Web**")
- [**AirFlow Arduino**](https://github.com/SentoMarcos/AirFlow-Arduino "**AirFlow Arduino**")

## Licencia

Este proyecto está licenciado bajo [nombre de la licencia]. Consulta el archivo `LICENSE` para más detalles.
