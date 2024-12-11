package com.example.smariba_upv.airflow.Services;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.media.AudioAttributes;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.smariba_upv.airflow.API.EnviarPeticionesUser;
import com.example.smariba_upv.airflow.LOGIC.Utilidades;
import com.example.smariba_upv.airflow.POJO.Medicion;
import com.example.smariba_upv.airflow.POJO.SensorObject;
import com.example.smariba_upv.airflow.POJO.TramaIBeacon;
import com.example.smariba_upv.airflow.PRESENTACION.LogInActivity;
import com.example.smariba_upv.airflow.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class ArduinoGetterService extends Service {

    /**
     * @brief Constantes de la clase
     * @details Constantes de la clase
     * @li ETIQUETA_LOG: Etiqueta de log
     * @li BEACON_UUID: UUID que estamos buscando
     * @li CODIGO_PETICION_PERMISOS: Código para la solicitud de permisos.
     * @li CHANNEL_ID: ID del canal de notificación
     * @li NOTIFICATION_INTERVAL: Intervalo de notificación
     *
     * @li lastNotificationTime: Última vez que se envió una notificación
     *
     * @li elEscanner: Escáner de dispositivos BTLE.
     * @li callbackDelEscaneo: Callback para el proceso de escaneo.
     * @li TIEMPO_DESCONEXION: Tiempo de desconexión
     * @li handler: Manejador
     * @li temporizador: Temporizador
     * @li enviarPeticionesUser: Objeto para enviar peticiones
     * @li dispositivoActualmenteConectado: Estado actual de conexión.
     *
     * @li dispositivoDetectado: Dispositivo detectado
     * */
    private static final String ETIQUETA_LOG = "ArduinoGetterService"; // Etiqueta de log
    private static final String BEACON_UUID = "EPSG-GTI-PROY-3D"; // UUID que estamos buscando
    private static final int CODIGO_PETICION_PERMISOS = 11223344; ///< Código para la solicitud de permisos.
    private static final String CHANNEL_ID = "ArduinoGetterServiceChannel";
    private static final long NOTIFICATION_INTERVAL = 1 * 60 * 1000; // 1 minutos
    private long lastNotificationTime = 0;

    private BluetoothLeScanner elEscanner; ///< Escáner de dispositivos BTLE.
    private ScanCallback callbackDelEscaneo = null; ///< Callback para el proceso de escaneo.
    private static final int TIEMPO_DESCONEXION = 30000; // 30 segundos
    private Handler handler;
    private Runnable temporizador;
    private EnviarPeticionesUser enviarPeticionesUser = new EnviarPeticionesUser();
    private boolean dispositivoActualmenteConectado = false; // Estado actual de conexión.

    private boolean dispositivoDetectado = false;

    //private int minuto = 60000; ///< 1 minuto en milisegundos.

    /**
     * @function onCreate
     * @brief Método onCreate
     * @details llamado cuando el servicio se crea
     * @details crea el canal de notificación y llama a startForegroundService
     * @details inicializa el bluetooth y busca el dispositivo
     * @details Configura el temporizador para las notificaciones
     * @details handler.post(temporizador) inicia el temporizado
     * */
    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel(); // Asegúrate de crear el canal antes de usarlo
        startForegroundService();
        inicializarBlueTooth();
        //introducir el uuid del sensor de shared preferences
        SharedPreferences sharedPreferences = this.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        String sensor = sharedPreferences.getString("sensores", "");
        // Obtener el UUID del sensor de shared preferences
        if (!sensor.equals("")) {
            try {
                List<String> dispositivosBuscados = new ArrayList<>();
                if (sensor.startsWith("[")) {
                    // Es un array
                    JSONArray jsonArray = new JSONArray(sensor);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject sensorObject = jsonArray.getJSONObject(i);
                        String uuid = sensorObject.getString("uuid");
                        dispositivosBuscados.add(uuid);
                    }
                } else {
                    // Es un objeto único
                    JSONObject jsonObject = new JSONObject(sensor);
                    String uuid = jsonObject.getString("uuid");
                    dispositivosBuscados.add(uuid);
                }

                // Iniciar búsqueda con la lista de dispositivos
                buscarDispositivosBTLE(dispositivosBuscados);

            } catch (JSONException e) {
                Log.e(ETIQUETA_LOG, "Error al obtener el UUID del sensor de shared preferences", e);
            }
        }



        // Configuración del temporizador para notificaciones
        // Configuración del temporizador para manejar desconexión
        handler = new Handler();
        temporizador = new Runnable() {
            @Override
            public void run() {
                if (!dispositivoDetectado) {
                    enviarNotificacionDesconexion(); // Enviar notificación
                    actualizarEstadoDesconexion(); // Actualizar estado
                }
                dispositivoDetectado = false; // Resetear para la siguiente verificación
                handler.postDelayed(this, TIEMPO_DESCONEXION); // Repetir el temporizador
            }
        };
        handler.post(temporizador); // Iniciar el temporizador

    }private void actualizarEstadoDesconexion() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        String sensores = sharedPreferences.getString("sensores", "");

        if (sensores != null && !sensores.isEmpty()) {
            try {
                if (sensores.startsWith("[")) {
                    JSONArray jsonArray = new JSONArray(sensores);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        int idSensor = jsonObject.getInt("id_sensor");
                        String nombre = jsonObject.getString("nombre");

                        // Actualizar en la base de datos
                        enviarPeticionesUser.actualizarSensor(idSensor, "Desconectado", false, 0);

                        // Actualizar localmente
                        SensorObject sensor = new SensorObject(idSensor, "Desconectado", "1234", jsonObject.getString("uuid"), nombre, false, 0);
                        notificarNuevaMedicion(sensor, null); // Notificar la desconexión
                    }
                } else {
                    JSONObject jsonObject = new JSONObject(sensores);
                    int idSensor = jsonObject.getInt("id_sensor");
                    String nombre = jsonObject.getString("nombre");

                    // Actualizar en la base de datos
                    enviarPeticionesUser.actualizarSensor(idSensor, "Desconectado", false, 0);

                    // Actualizar localmente
                    SensorObject sensor = new SensorObject(idSensor, "Desconectado", "1234", jsonObject.getString("uuid"), nombre, false, 0);
                    notificarNuevaMedicion(sensor, null); // Notificar la desconexión
                }
            } catch (JSONException e) {
                Log.e(ETIQUETA_LOG, "Error al procesar sensores para desconexión", e);
            }
        }
    }



    private void manejarDispositivoDetectado(ScanResult resultado, String uuid) {
        byte[] bytes = resultado.getScanRecord().getBytes();
        TramaIBeacon tib = new TramaIBeacon(bytes);
        @SuppressLint("MissingPermission") String name = resultado.getDevice().getName();
        int rssi = resultado.getRssi();
        int txPower = tib.getTxPower();
        int minor = Utilidades.bytesToInt(tib.getMinor());

        // Calcular distancia y asignarla como batería temporalmente
        double distancia = calcularDistancia(rssi, txPower);
        int battery = (int) distancia; // Enviar distancia como valor de batería para pruebas

        Log.d("Distancia", "Distancia aproximada: " + distancia + " metros");

        // Resto del código existente
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        String sensores = sharedPreferences.getString("sensores", "");
        int idSensor = -1;
        try {
            if (sensores.startsWith("[")) {
                JSONArray jsonArray = new JSONArray(sensores);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    if (jsonObject.getString("uuid").equals(uuid)) {
                        idSensor = jsonObject.getInt("id_sensor");
                    }
                }
            } else {
                JSONObject jsonObject = new JSONObject(sensores);
                if (jsonObject.getString("uuid").equals(uuid)) {
                    idSensor = jsonObject.getInt("id_sensor");
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        SensorObject sensor = new SensorObject(idSensor, "Conectado", "1234", uuid, name, true, battery, distancia);
        Medicion medicion = new Medicion(0, idSensor, "Unknown", 0.0, 0.0, minor);
        notificarNuevaMedicion(sensor, medicion);
    }

    private void notificarNuevaMedicion(SensorObject sensor, @Nullable Medicion medicion) {
        String sensorJson = new Gson().toJson(sensor);
        String medicionJson = medicion != null ? new Gson().toJson(medicion) : null;

        Log.d("notificarNuevaMedicion", "Sensor JSON: " + sensorJson);
        if (medicionJson != null) {
            Log.d("notificarNuevaMedicion", "Medicion JSON: " + medicionJson);
        } else {
            Log.d("notificarNuevaMedicion", "Medicion: null (desconexión)");
        }

        Intent intent = new Intent("NUEVA_MEDICION");
        intent.putExtra("sensor", sensorJson);
        if (medicionJson != null) {
            intent.putExtra("medicion", medicionJson);
        }
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }




    /**
     * @function createNotificationChannel
     * @brief Método createNotificationChannel
     * @details Crea el canal de notificación
     * @details Verifica si la versión de Android es 8.0 (API nivel 26) o superior
     * @details Crea un objeto AudioAttributes para definir las características del sonido
     * @details Supongamos que el archivo se llama 'notisound.wav'
     * @details Registrar el canal de notificación
     * */
    private void createNotificationChannel() {
        // Verificar si la versión de Android es 8.0 (API nivel 26) o superior
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Crear un objeto AudioAttributes para definir las características del sonido
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();

            // Supongamos que el archivo se llama 'notisound.wav'
            Uri soundUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.notisound);

            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Gas Notification Channel", NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("Canal para notificaciones de nivel de gas");
            channel.setSound(soundUri, audioAttributes);

            // Registrar el canal de notificación
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    /**
     * @function onDestroy
     * @brief Método onDestroy
     * @details llamado cuando el servicio se destruye
     * @details Detiene el temporizador al detener el servicio
     * */
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (handler != null) {
            handler.removeCallbacks(temporizador); // Detener el temporizador al detener el servicio
        }
    }

    /**
     * @function onBind
     * @brief Método onBind
     * @details Método onBind
     * @details No se utiliza en este servicio
     * Intent intent => onBlind() => IBinder:null
     * */
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    /**
     * @function startForegroundService
     * @brief Método startForegroundService
     * @details Inicia el servicio en primer plano
     * @details Crea una notificación para el servicio
     * @details Intent para abrir una actividad al interactuar con la notificación
     * */
    private void startForegroundService() {
        // Intent para abrir una actividad al interactuar con la notificación
        Intent notificationIntent = new Intent(this, LogInActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                0,
                notificationIntent,
                PendingIntent.FLAG_IMMUTABLE // Obligatorio en Android 12+ para mayor seguridad
        );

        // Crear la notificación
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Arduino Getter Service")
                .setContentText("El servicio está corriendo en primer plano")
                .setSmallIcon(android.R.drawable.ic_dialog_info) // Asegúrate de que sea un ícono válido
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT) // Prioridad adecuada para servicios en segundo plano
                .setOngoing(true); // Marca la notificación como persistente

        // Inicia el servicio en primer plano con la notificación válida
        startForeground(1, builder.build());
    }


    /**
     * @function inicializarBlueTooth
     * @brief Método inicializarBlueTooth
     * @details Inicializa el adaptador Bluetooth
     * @details Habilita el adaptador Bluetooth si no está habilitado
     * @details Obtiene el escáner BTLE
     * */
    private void inicializarBlueTooth() {
        Log.d(ETIQUETA_LOG, "inicializarBlueTooth(): obtenemos adaptador BT");

        BluetoothAdapter bta = BluetoothAdapter.getDefaultAdapter();

        if (bta == null) {
            Log.e(ETIQUETA_LOG, "BluetoothAdapter is null. Device does not support Bluetooth.");
            return;
        }

        Log.d(ETIQUETA_LOG, "inicializarBlueTooth(): habilitamos adaptador BT");

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            Log.d(ETIQUETA_LOG, "inicializarBlueTooth(): NO tengo permisos para conectar");
            // Handle permission request
            return;
        }

        if (!bta.isEnabled()) {
            bta.enable();
            Log.d(ETIQUETA_LOG, "inicializarBlueTooth(): habilitado = " + bta.isEnabled());
        }

        Log.d(ETIQUETA_LOG, "inicializarBlueTooth(): obtenemos escaner btle");

        this.elEscanner = bta.getBluetoothLeScanner();

        if (this.elEscanner == null) {
            Log.e(ETIQUETA_LOG, "inicializarBlueTooth(): NO hemos obtenido escaner btle");
        }
    }
    /**
     * @function buscarEsteDispositivoBTLE
     * @brief Método buscarEsteDispositivoBTLE
     * @details Busca un dispositivo BTLE con el nombre proporcionado
     * @details Crea un ScanFilter con el nombre del dispositivo
     * @details Inicia el escaneo
     * @details Si el dispositivo es detectado, se crea un objeto SensorObject y se maneja
     * @details Si el dispositivo no es detectado, se envía una notificación de desconexión
     * @param dispositivoBuscado Nombre del dispositivo a buscar
     * @return SensorObject
     * Texto:dispositivoBuscado => buscarEsteDispositivoBTLE() => SensorObject
     * */
    public void buscarDispositivosBTLE(List<String> dispositivosBuscados) {
        //Log.d(ETIQUETA_LOG, "buscarDispositivosBTLE(): buscando dispositivos BTLE");
        this.callbackDelEscaneo = new ScanCallback() {
            @Override
            public void onScanResult(int callbackType, ScanResult resultado) {
                super.onScanResult(callbackType, resultado);
                if (ContextCompat.checkSelfPermission(ArduinoGetterService.this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }

                byte[] bytes = resultado.getScanRecord().getBytes();
                TramaIBeacon tib = new TramaIBeacon(bytes);
                String uuid = Utilidades.bytesToString(tib.getUUID());
               // Log.d(ETIQUETA_LOG, "UUID detectado: " + uuid);

                // Verificar si el UUID está en la lista de dispositivos buscados
                if (dispositivosBuscados.contains(uuid)) {
                    //Log.d(ETIQUETA_LOG, "Dispositivo detectado con UUID esperado: " + uuid);
                    dispositivoDetectado = true;

                    if (!dispositivoActualmenteConectado) {
                        dispositivoActualmenteConectado = true; // Cambiar estado
                        enviarNotificacionConexion(); // Notificar conexión
                    }

                    // Procesar datos del dispositivo detectado
                    manejarDispositivoDetectado(resultado, uuid);
                }
            }

            @Override
            public void onBatchScanResults(List<ScanResult> results) {
                super.onBatchScanResults(results);
            }

            @Override
            public void onScanFailed(int errorCode) {
                super.onScanFailed(errorCode);
            }
        };

        // Configurar filtros (opcional, si no quieres filtrar por nombre)
        List<ScanFilter> filters = new ArrayList<>();
        ScanSettings scanSettings = new ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                .build();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
            Log.e(ETIQUETA_LOG, "No tienes permisos para escanear dispositivos BLE.");
            return;
        }

        this.elEscanner.startScan(filters, scanSettings, this.callbackDelEscaneo);
    }


    /**
     * @function enviarNotificacionDesconexion
     * @brief Método enviarNotificacionDesconexion
     * @details Envía una notificación de desconexión
     * @details Crea una notificación con el mensaje de desconexión
     * @details Envia la notificación
     * @details Cambia el estado del dispositivo a desconectado
     * */
    private void enviarNotificacionDesconexion() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Desconexión de dispositivo")
                .setContentText("El dispositivo no se ha detectado en los últimos " + (TIEMPO_DESCONEXION / 1000) + " segundos.")
                .setSmallIcon(android.R.drawable.ic_dialog_alert)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (manager != null) {
            manager.notify(2, builder.build());
        }
        Log.d(ETIQUETA_LOG, "Notificación de desconexión enviada.");
        dispositivoActualmenteConectado = false; // Cambiar el estado


    }

    /**
     * @function enviarNotificacionConexion
     * @brief Método enviarNotificacionConexion
     * @details Envía una notificación de conexión
     * @details Crea una notificación con el mensaje de conexión
     * @details Envia la notificación
     *  enviarNotificacionConexion() => void
     * */
    private void enviarNotificacionConexion(){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Conexión de dispositivo")
                .setContentText("El dispositivo ha sido detectado.")
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (manager != null) {
            manager.notify(3, builder.build());
        }
        Log.d(ETIQUETA_LOG, "Notificación de conexión enviada.");
    }
    /**
     * @return String
     * getLocation() => String
     * @function getLocation
     * @brief Método getLocation
     * @details Obtiene la ubicación del dispositivo
     * @details Crea un LocationManager para acceder a los servicios de ubicación
     * @details Verifica si el LocationManager está disponible
     * @details Comprueba si tenemos permiso para acceder a la ubicación
     * @details Obtener la ubicación más reciente del proveedor de ubicación
     * @details Si se obtiene la ubicación, devolverla como una cadena
     * @details Si no se puede obtener la ubicación, devolver un mensaje de error
     */
    private double[] getLocation() {
        // Crear un LocationManager para acceder a los servicios de ubicación
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        // Verificar si el LocationManager está disponible
        if (locationManager != null) {
            try {
                // Comprobamos si tenemos permiso para acceder a la ubicación
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                        ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                    // Obtener la ubicación más reciente del proveedor de ubicación
                    Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                    if (location != null) {
                        // Si se obtiene la ubicación, devolverla como una cadena
                        double latitude = location.getLatitude();
                        double longitude = location.getLongitude();
                        return new double[]{latitude, longitude};
                    }
                }
            } catch (SecurityException e) {
                e.printStackTrace();
            }
        }

        // Si no se puede obtener la ubicación, devolver un mensaje de error
        return new double[]{0.0, 0.0};
    }



    // Método robusto y preciso para calcular la distancia
    private double calcularDistancia(int rssi, int txPower) {
        if (rssi == 0 || txPower == 0) {
            return -1.0; // No se puede calcular la distancia
        }

        // Constantes ajustables para calibración
        double environmentalFactor = 2.2; // Ajusta según el entorno (2 para espacios abiertos, >3 para interiores)
        int calibratedTxPower = calibrarTxPower(txPower); // Si es necesario, calibra el Tx Power aquí

        // Filtrar valores anómalos antes de suavizar
        rssi = filtrarValoresAnomalos(rssi);

        // Suavizado del RSSI para reducir fluctuaciones
        rssi = calcularRSSISuavizado(rssi);

        // Calcular la distancia basada en RSSI y Tx Power calibrado
        double ratio = rssi * 1.0 / calibratedTxPower;
        double distance;

        if (ratio < 1.0) {
            distance = Math.pow(ratio, 10);
        } else {
            distance = Math.pow(10, (calibratedTxPower - rssi) / (10 * environmentalFactor));
        }

        // Limitar la distancia a dos decimales
        return Math.round(distance * 100.0) / 100.0;
    }

    // Método para calibrar el Tx Power si es necesario
    private int calibrarTxPower(int txPower) {
        // Si tienes un valor calibrado basado en pruebas específicas, devuélvelo aquí
        // Por defecto, devuelve el mismo valor recibido
        return txPower;
    }

    // Método para suavizar los valores RSSI utilizando una media móvil
    private int calcularRSSISuavizado(int nuevoRSSI) {
        final int WINDOW_SIZE = 15; // Tamaño de la ventana para la media móvil
        LinkedList<Integer> rssiWindow = new LinkedList<>();

        // Añadir el nuevo RSSI a la ventana
        rssiWindow.add(nuevoRSSI);

        // Eliminar el valor más antiguo si excede el tamaño de la ventana
        if (rssiWindow.size() > WINDOW_SIZE) {
            rssiWindow.poll();
        }

        // Calcular la media de los valores en la ventana
        int sum = 0;
        for (int value : rssiWindow) {
            sum += value;
        }

        return sum / rssiWindow.size();
    }

    // Método adicional para detectar y corregir valores anómalos
    private int filtrarValoresAnomalos(int rssi) {
        final int THRESHOLD = 10; // Límite para considerar una fluctuación como anómala
        Integer ultimoRSSI = null;

        if (ultimoRSSI != null && Math.abs(rssi - ultimoRSSI) > THRESHOLD) {
            // Si el valor actual varía demasiado del último valor, usa el último como referencia
            return ultimoRSSI;
        }

        // Actualizar el último valor y devolver el actual
        ultimoRSSI = rssi;
        return rssi;
    }


    /**
     * @function limitcheck
     * @brief Método limitcheck
     * @details Comprueba si la medida del sensor supera un límite
     * @details Crea una notificación con el nombre del sensor, la hora, el tipo de gas y la medida
     * @details Cambia el color de fondo de la notificación según el valor de la medida
     * @details Crea y muestra la notificación usando el canal personalizado
     * @details Actualiza el tiempo de la última notificación
     * @param sensor SensorObject
     * SensorObject:sensor => limitcheck() => void
     * TODO: Cambiar funcion a NotificationSensorUserUtil
     * */
    private void limitcheck(SensorObject sensor,Medicion medicion,int idSensor) {
        double value = medicion.getValor();
        long currentTime = System.currentTimeMillis();


        //Log.d(ETIQUETA_LOG, "SensorObject: " + idSensor);
        sensor.setId(idSensor);

        // Check if enough time has passed since the last notification
        if (currentTime - lastNotificationTime < NOTIFICATION_INTERVAL) {
            //Log.d(ETIQUETA_LOG, "Skipping notification to avoid spamming.");
            return;
        }

        //Log.d(ETIQUETA_LOG, "Changing background color, value: " + value);
        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.custom_notification_layout);
        remoteViews.setTextViewText(R.id.notification_title, "Nivel de gas");


        remoteViews.setTextViewText(R.id.notification_text, "Sensor: " + sensor.getNombre() + "\nHora: " + medicion.getFecha() + "\nTipo de gas: " + medicion.getTipoGas() + "\nMedida: " + medicion.getValor() + "\nUbicación: " + medicion.getLatitud() + ", " + medicion.getLongitud());
        remoteViews.setImageViewResource(R.id.notification_icon, android.R.drawable.ic_dialog_alert);

        int color;
        if (value > 200) {
            color = ContextCompat.getColor(this, R.color.RojoPeligroso);
            remoteViews.setTextViewText(R.id.notification_title, "Error en la medición");
            enviarPeticionesUser.actualizarSensor(sensor.getId(), "error en la medicion",true,sensor.getBateria());
        } else if (value > 100) {
            color = ContextCompat.getColor(this, R.color.RojoPeligroso);
            remoteViews.setTextViewText(R.id.notification_title, "Exceso de gas detectado");
            enviarPeticionesUser.actualizarSensor(sensor.getId(), "Exceso de gas detectado",true,sensor.getBateria());


        } else if (value > 75) {
            color = ContextCompat.getColor(this, R.color.NaranjaMalo);
            remoteViews.setTextViewText(R.id.notification_title, "Alerta de gas");
            enviarPeticionesUser.actualizarSensor(sensor.getId(), "Alerta de gas",true,sensor.getBateria());
        } else if (value > 50) {
            color = ContextCompat.getColor(this, R.color.AmarilloMedio);
            remoteViews.setTextViewText(R.id.notification_title, "Advertencia de gas");
            enviarPeticionesUser.actualizarSensor(sensor.getId(), "Advertencia de gas",true,sensor.getBateria());
        } else if (value > 25) {
            color = ContextCompat.getColor(this, R.color.VerdeBueno);
            remoteViews.setTextViewText(R.id.notification_title, "Nivel de gas aceptable");
            enviarPeticionesUser.actualizarSensor(sensor.getId(), "Nivel de gas aceptable",true,sensor.getBateria());
        } else {
            color = ContextCompat.getColor(this, R.color.RosaExcelente);
            remoteViews.setTextViewText(R.id.notification_title, "Nivel de gas excelente");
            enviarPeticionesUser.actualizarSensor(sensor.getId(), "Nivel de gas excelente",true,sensor.getBateria());
        }

        remoteViews.setInt(R.id.custom_notification_layout, "setBackgroundColor", color);

        // Crear y mostrar la notificación usando el canal personalizado
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_alert)
                .setCustomContentView(remoteViews)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setColor(color)
                .setStyle(new NotificationCompat.DecoratedCustomViewStyle());

        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (manager != null) {
            manager.notify(4, builder.build());
        }
        Log.d(ETIQUETA_LOG, "Notificación de nivel de gas enviada.");

        // Update the last notification time
        lastNotificationTime = currentTime;
    }
}