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
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.example.smariba_upv.airflow.MainActivity;
import com.example.smariba_upv.airflow.LOGIC.Utilidades;
import com.example.smariba_upv.airflow.POJO.TramaIBeacon;
import com.example.smariba_upv.airflow.R;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ArduinoGetterService extends Service {

    private static final String TAG = "ArduinoGetterService";
    private static final String CHANNEL_ID = "ALERTS_CHANNEL";
    private static final int NOTIFICATION_ID = 1;

    private BluetoothLeScanner bluetoothLeScanner;
    private ScanCallback scanCallback;
    private static final int LIMITE_VALOR = 100; // Define el límite según tus necesidades
    private static final String TARGET_UUID = "EPSG-GTI-PROY-3D"; // Sustituye con la dirección MAC del dispositivo objetivo.

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
        startForegroundService();
        initializeBluetooth();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Alertas de Gas";
            String description = "Notificaciones para alertas de gas";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void startForegroundService() {
        // Crear una notificación que se mostrará mientras el servicio esté en ejecución.
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Escaneando dispositivos BLE")
                .setContentText("El servicio de escaneo está en ejecución.")
                .setSmallIcon(R.drawable.ic_launcher_foreground) // Usa tu ícono
                .setContentIntent(pendingIntent)
                .setOngoing(true); // Notificación continua

        startForeground(NOTIFICATION_ID, notificationBuilder.build());
    }

    private void initializeBluetooth() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            Log.e(TAG, "El dispositivo no soporta Bluetooth");
            stopSelf();
            return;
        }

        bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();

        if (bluetoothLeScanner == null) {
            Log.e(TAG, "No se pudo obtener el escáner BLE");
            stopSelf();
            return;
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
            Log.e(TAG, "Falta el permiso BLUETOOTH_SCAN");
            stopSelf();
            return;
        }

        startScanning();
    }

    private void startScanning() {
        // Crear filtro para buscar el dispositivo específico
        List<ScanFilter> filters = new ArrayList<>();
        ScanFilter filter = new ScanFilter.Builder()
                .setDeviceAddress(TARGET_UUID) // Filtrar por dirección MAC
                .build();
        filters.add(filter);

        // Configuración de escaneo
        ScanSettings settings = new ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                .build();

        scanCallback = new ScanCallback() {
            @Override
            public void onScanResult(int callbackType, ScanResult result) {
                super.onScanResult(callbackType, result);
                processScanResult(result);
            }

            @Override
            public void onBatchScanResults(List<ScanResult> results) {
                super.onBatchScanResults(results);
                for (ScanResult result : results) {
                    processScanResult(result);
                }
            }

            @Override
            public void onScanFailed(int errorCode) {
                Log.e(TAG, "El escaneo falló con el código de error: " + errorCode);
            }
        };

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
            Log.e(TAG, "Falta el permiso BLUETOOTH_SCAN");
            stopSelf();
            return;
        }

        bluetoothLeScanner.startScan(filters, settings, scanCallback);
        Log.d(TAG, "Escaneo BLE iniciado");
    }

    private void processScanResult(ScanResult result) {
        BluetoothDevice device = result.getDevice();
        byte[] scanRecord = result.getScanRecord() != null ? result.getScanRecord().getBytes() : null;

        if (scanRecord != null) {
            // Crear una instancia de TramaIBeacon usando los datos escaneados
            TramaIBeacon iBeacon = new TramaIBeacon(scanRecord);

            // Extraer el UUID de la trama
            byte[] uuidBytes = iBeacon.getUUID();

            // Convertir los bytes a un UUID
            UUID uuid = UUID.nameUUIDFromBytes(uuidBytes);

            // Verificar si el UUID coincide con el UUID que buscamos
            if (uuid.equals(TARGET_UUID)) {
                Log.d(TAG, "Beacon con UUID coincidente detectado:");
                Log.d(TAG, "UUID: " + Utilidades.bytesToString(iBeacon.getUUID()));
                Log.d(TAG, "Major: " + Utilidades.bytesToInt(iBeacon.getMajor()));
                Log.d(TAG, "Minor: " + Utilidades.bytesToInt(iBeacon.getMinor()));

                // Supongamos que el valor medido está en el campo Major
                int valorMedido = Utilidades.bytesToInt(iBeacon.getMajor());

                // Verifica si el valor medido supera el límite y lanza una notificación
                if (valorMedido > LIMITE_VALOR) {
                    lanzarNotificacion("Alerta de Gas", "El valor medido ha superado el límite: " + valorMedido);
                }
            }
        }
    }


    private void lanzarNotificacion(String titulo, String mensaje) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground) // Cambia por tu propio ícono
                .setContentTitle(titulo)
                .setContentText(mensaje)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, builder.build());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (bluetoothLeScanner != null && scanCallback != null) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED) {
                bluetoothLeScanner.stopScan(scanCallback);
                Log.d(TAG, "Escaneo BLE detenido");
            }
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null; // No soportamos comunicación con otros componentes.
    }
}