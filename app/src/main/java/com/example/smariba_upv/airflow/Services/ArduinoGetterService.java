package com.example.smariba_upv.airflow.Services;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.Intent;
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

import com.example.smariba_upv.airflow.LOGIC.NotificationSensorUserUtil;
import com.example.smariba_upv.airflow.LOGIC.Utilidades;
import com.example.smariba_upv.airflow.POJO.SensorObject;
import com.example.smariba_upv.airflow.POJO.TramaIBeacon;
import com.example.smariba_upv.airflow.PRESENTACION.LogInActivity;
import com.example.smariba_upv.airflow.R;

import java.util.Date;
import java.util.List;

public class ArduinoGetterService extends Service {
    private static final String ETIQUETA_LOG = "ArduinoGetterService"; // Etiqueta de log
    private static final String BEACON_UUID = "EPSG-GTI-PROY-3D"; // UUID que estamos buscando
    private static final int CODIGO_PETICION_PERMISOS = 11223344; ///< Código para la solicitud de permisos.
    private static final String CHANNEL_ID = "ArduinoGetterServiceChannel";
    private long lastNotificationTime = 0;
    private static final long NOTIFICATION_INTERVAL = 60000;
    private BluetoothLeScanner elEscanner; ///< Escáner de dispositivos BTLE.
    private ScanCallback callbackDelEscaneo = null; ///< Callback para el proceso de escaneo.
    private static final int TIEMPO_DESCONEXION = 30000; // 30 segundos
    private Handler handler;
    private Runnable temporizador;
    private boolean dispositivoActualmenteConectado = false; // Estado actual de conexión.

    private boolean dispositivoDetectado = false;

    private int minuto = 60000; ///< 1 minuto en milisegundos.

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel(); // Asegúrate de crear el canal antes de usarlo
        startForegroundService();
        inicializarBlueTooth();
        buscarEsteDispositivoBTLE(BEACON_UUID);

        // Configuración del temporizador para notificaciones
        handler = new Handler();
        temporizador = new Runnable() {
            @Override
            public void run() {
                if (!dispositivoDetectado) {
                    enviarNotificacionDesconexion();
                }
                dispositivoDetectado = false; // Resetear para la siguiente verificación
                handler.postDelayed(this, TIEMPO_DESCONEXION);
            }
        };
        handler.post(temporizador); // Iniciar el temporizador
    }

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


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (handler != null) {
            handler.removeCallbacks(temporizador); // Detener el temporizador al detener el servicio
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

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

    private void mostrarInformacionDispositivoBTLE(ScanResult resultado) {
        BluetoothDevice bluetoothDevice = resultado.getDevice();
        byte[] bytes = resultado.getScanRecord().getBytes();
        int rssi = resultado.getRssi();

        Log.d(ETIQUETA_LOG, " ****************************************************");
        Log.d(ETIQUETA_LOG, " ****** DISPOSITIVO DETECTADO BTLE ****************** ");
        Log.d(ETIQUETA_LOG, " ****************************************************");
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            Log.d(ETIQUETA_LOG, "  mostrarInformacionDispositivoBTLE(): NO tengo permisos para conectar ");
            // Handle permission request
            return;
        }
        Log.d(ETIQUETA_LOG, " nombre = " + bluetoothDevice.getName());
        Log.d(ETIQUETA_LOG, " toString = " + bluetoothDevice.toString());
        Log.d(ETIQUETA_LOG, " dirección = " + bluetoothDevice.getAddress());
        Log.d(ETIQUETA_LOG, " rssi = " + rssi);
        Log.d(ETIQUETA_LOG, " bytes = " + new String(bytes));
        Log.d(ETIQUETA_LOG, " bytes (" + bytes.length + ") = " + Utilidades.bytesToHexString(bytes));

        TramaIBeacon tib = new TramaIBeacon(bytes);

        Log.d(ETIQUETA_LOG, " ----------------------------------------------------");
        Log.d(ETIQUETA_LOG, " prefijo  = " + Utilidades.bytesToHexString(tib.getPrefijo()));
        Log.d(ETIQUETA_LOG, "          advFlags = " + Utilidades.bytesToHexString(tib.getAdvFlags()));
        Log.d(ETIQUETA_LOG, "          advHeader = " + Utilidades.bytesToHexString(tib.getAdvHeader()));
        Log.d(ETIQUETA_LOG, "          companyID = " + Utilidades.bytesToHexString(tib.getCompanyID()));
        Log.d(ETIQUETA_LOG, "          iBeacon type = " + Integer.toHexString(tib.getiBeaconType()));
        Log.d(ETIQUETA_LOG, "          iBeacon length 0x = " + Integer.toHexString(tib.getiBeaconLength()) + " ( "
                + tib.getiBeaconLength() + " ) ");
        Log.d(ETIQUETA_LOG, " uuid  = " + Utilidades.bytesToHexString(tib.getUUID()));
        Log.d(ETIQUETA_LOG, " uuid  = " + Utilidades.bytesToString(tib.getUUID()));
        Log.d(ETIQUETA_LOG, " major  = " + Utilidades.bytesToHexString(tib.getMajor()) + "( "
                + Utilidades.bytesToInt(tib.getMajor()) + " ) ");
        Log.d(ETIQUETA_LOG, " minor  = " + Utilidades.bytesToHexString(tib.getMinor()) + "( "
                + Utilidades.bytesToInt(tib.getMinor()) + " ) ");
        Log.d(ETIQUETA_LOG, " txPower  = " + Integer.toHexString(tib.getTxPower()) + " ( " + tib.getTxPower() + " )");
        Log.d(ETIQUETA_LOG, " ****************************************************");
    }

    public SensorObject buscarEsteDispositivoBTLE(final String dispositivoBuscado) {
        this.callbackDelEscaneo = new ScanCallback() {
            @Override
            public void onScanResult(int callbackType, ScanResult resultado) {
                super.onScanResult(callbackType, resultado);
                if (ContextCompat.checkSelfPermission(ArduinoGetterService.this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }

                byte[] bytes = resultado.getScanRecord().getBytes();
                TramaIBeacon tib = new TramaIBeacon(bytes);
                if (Utilidades.bytesToString(tib.getUUID()).equals(dispositivoBuscado)) {
                    dispositivoDetectado = true; // El dispositivo ha sido detectado, reiniciar estado
                    if (!dispositivoActualmenteConectado) {
                        dispositivoActualmenteConectado = true; // Cambiar el estado
                        enviarNotificacionConexion(); // Notificar conexión
                    }
                    String uuid = Utilidades.bytesToString(tib.getUUID());
                    String name = resultado.getDevice().getName();
                    String typegas = "Unknown"; // Asumiendo que typegas no está disponible
                    int measure = Utilidades.bytesToInt(tib.getMinor());
                    String date = new Date().toString();
                    int battery = Utilidades.bytesToInt(tib.getMajor());

                    SensorObject sensor = new SensorObject(uuid, name, typegas, measure, date, battery);
                    handleSensorObject(sensor);
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

        ScanFilter sf = new ScanFilter.Builder().setDeviceName(dispositivoBuscado).build();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
            // Manejar solicitud de permisos
            return null;
        }
        this.elEscanner.startScan(this.callbackDelEscaneo);
        return null;
    }

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

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case CODIGO_PETICION_PERMISOS:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(ETIQUETA_LOG, "onRequestPermissionResult(): Permisos concedidos");
                    inicializarBlueTooth();
                } else {
                    Log.d(ETIQUETA_LOG, "onRequestPermissionResult(): Permisos NO concedidos");
                }
                return;
        }
    }

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

    private void handleSensorObject(SensorObject sensor) {
        NotificationSensorUserUtil.TakeAllDataOfSensor(this, sensor.getUuid(), sensor.getName(), sensor.getTypegas(), sensor.getMeasure(), sensor.getDate(), sensor.getBattery());
        limitcheck(sensor);
    }
    private String getLocation() {
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
                        return "Lat: " + latitude + ", Long: " + longitude;
                    }
                }
            } catch (SecurityException e) {
                e.printStackTrace();
            }
        }

        // Si no se puede obtener la ubicación, devolver un mensaje de error
        return "Ubicación no disponible";
    }

    // Comprobar límite de exceso de gas y enviar notificación con el nombre del sensor, la hora, el tipo de gas y la medida
// Color de fondo personalizado para cada límite

    private void limitcheck(SensorObject sensor) {
        int value = sensor.getMeasure();
        long currentTime = System.currentTimeMillis();

        // Check if enough time has passed since the last notification
        if (currentTime - lastNotificationTime < NOTIFICATION_INTERVAL) {
            Log.d(ETIQUETA_LOG, "Skipping notification to avoid spamming.");
            return;
        }

        Log.d(ETIQUETA_LOG, "Changing background color, value: " + value);
        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.custom_notification_layout);
        remoteViews.setTextViewText(R.id.notification_title, "Nivel de gas");

        // Obtener la ubicación del dispositivo móvil
        String location = getLocation();  // Llamada al método para obtener la ubicación

        remoteViews.setTextViewText(R.id.notification_text, "Sensor: " + sensor.getName() + "\nHora: " + sensor.getDate() + "\nTipo de gas: " + sensor.getTypegas() + "\nMedida: " + sensor.getMeasure() + "\nUbicación: " + location);
        remoteViews.setImageViewResource(R.id.notification_icon, android.R.drawable.ic_dialog_alert);

        int color;
        if (value > 200) {
            color = ContextCompat.getColor(this, R.color.RojoPeligroso);
            remoteViews.setTextViewText(R.id.notification_title, "Error en la medición");
        } else if (value > 100) {
            color = ContextCompat.getColor(this, R.color.RojoPeligroso);
            remoteViews.setTextViewText(R.id.notification_title, "Exceso de gas detectado");
        } else if (value > 75) {
            color = ContextCompat.getColor(this, R.color.NaranjaMalo);
            remoteViews.setTextViewText(R.id.notification_title, "Alerta de gas");
        } else if (value > 50) {
            color = ContextCompat.getColor(this, R.color.AmarilloMedio);
            remoteViews.setTextViewText(R.id.notification_title, "Advertencia de gas");
        } else if (value > 25) {
            color = ContextCompat.getColor(this, R.color.VerdeBueno);
            remoteViews.setTextViewText(R.id.notification_title, "Nivel de gas aceptable");
        } else {
            color = ContextCompat.getColor(this, R.color.RosaExcelente);
            remoteViews.setTextViewText(R.id.notification_title, "Nivel de gas excelente");
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