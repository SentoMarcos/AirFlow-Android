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
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.example.smariba_upv.airflow.LOGIC.NotificationSensorUserUtil;
import com.example.smariba_upv.airflow.LOGIC.Utilidades;
import com.example.smariba_upv.airflow.POJO.TramaIBeacon;
import com.example.smariba_upv.airflow.PRESENTACION.LogInActivity;

import java.util.List;

public class ArduinoGetterService extends Service {
    private static final String ETIQUETA_LOG = "ArduinoGetterService"; // Etiqueta de log
    private static final String BEACON_UUID = "EPSG-GTI-PROY-3D"; // UUID que estamos buscando
    private static final int CODIGO_PETICION_PERMISOS = 11223344; ///< Código para la solicitud de permisos.
    private static final String CHANNEL_ID = "ArduinoGetterServiceChannel";

    private BluetoothLeScanner elEscanner; ///< Escáner de dispositivos BTLE.
    private ScanCallback callbackDelEscaneo = null; ///< Callback para el proceso de escaneo.

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
        startForegroundService();
        inicializarBlueTooth();
        if (elEscanner != null) {
            this.buscarEsteDispositivoBTLE("EPSG-GTI-PROY-3D");
        } else {
            Log.e(ETIQUETA_LOG, "BluetoothLeScanner is null. Cannot start scanning.");
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Arduino Getter Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }

    private void startForegroundService() {
        Intent notificationIntent = new Intent(this, LogInActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Arduino Getter Service")
                .setContentText("Service is running in the foreground")
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentIntent(pendingIntent);

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
        NotificationSensorUserUtil.limitcheck(this, Utilidades.bytesToInt(tib.getMinor()));
    }

    private void buscarEsteDispositivoBTLE(final String dispositivoBuscado) {
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
                    mostrarInformacionDispositivoBTLE(resultado);
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
            // Handle permission request
            return;
        }
        this.elEscanner.startScan(this.callbackDelEscaneo);
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
}