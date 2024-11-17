package com.example.smariba_upv.airflow.Services;

import android.Manifest;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.example.smariba_upv.airflow.API.RetrofitClient;
import com.example.smariba_upv.airflow.LOGIC.Utilidades;
import com.example.smariba_upv.airflow.POJO.Medicion;
import com.example.smariba_upv.airflow.POJO.TramaIBeacon;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ArduinoGetterService extends Service {
    private static final String ETIQUETA_LOG = "ArduinoGetterService"; // Etiqueta de log
    private static final String BEACON_UUID = "EPSG-GTI-PROY-3D"; // UUID que estamos buscando

    private BluetoothLeScanner bluetoothLeScanner;  // Escáner Bluetooth LE
    private ScanCallback scanCallback;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(ETIQUETA_LOG, "Servicio iniciado");

        // Inicializar Bluetooth
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            Log.d(ETIQUETA_LOG, "Bluetooth no disponible o no habilitado");
            stopSelf();  // Detener servicio si no hay Bluetooth disponible
            return;
        }

        // Obtener el escáner de Bluetooth LE
        bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();

        // Verificar permisos y comenzar escaneo
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
            Log.d(ETIQUETA_LOG, "No tengo permisos para escanear.");
            return;
        }

        // Configurar callback del escaneo
        scanCallback = new ScanCallback() {
            @Override
            public void onScanResult(int callbackType, ScanResult result) {
                super.onScanResult(callbackType, result);
                String name = null;
                if (ActivityCompat.checkSelfPermission(ArduinoGetterService.this, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
                    name = result.getDevice().getName();
                }
                Log.d(ETIQUETA_LOG, "Dispositivo detectado: " + name);

                // Llamar método para verificar si es el dispositivo con el UUID esperado
                verificarBeacon(result);
            }

            @Override
            public void onBatchScanResults(List<ScanResult> results) {
                super.onBatchScanResults(results);
                // Procesar lotes de resultados si es necesario
            }

            @Override
            public void onScanFailed(int errorCode) {
                super.onScanFailed(errorCode);
                Log.d(ETIQUETA_LOG, "Error en el escaneo, código de error: " + errorCode);
            }
        };

        // Iniciar escaneo
        bluetoothLeScanner.startScan(scanCallback);
    }

    /**
     * Verifica si el UUID del beacon detectado coincide con el UUID esperado.
     * @param result Resultado del escaneo
     */
    private void verificarBeacon(ScanResult result) {
        BluetoothDevice bluetoothDevice = result.getDevice();
        byte[] scanRecord = result.getScanRecord().getBytes();

        // Extraer UUID del beacon (de acuerdo con tu implementación en `TramaIBeacon`)
        TramaIBeacon beacon = new TramaIBeacon(scanRecord);
        String uuid = Utilidades.bytesToString(beacon.getUUID());

        // Comparar con el UUID que estamos buscando
        if (uuid.equals(BEACON_UUID)) {
            Log.d(ETIQUETA_LOG, "Beacon encontrado con UUID: " + uuid);
            // Si encontramos el beacon correcto, realiza la acción que necesites
            // Por ejemplo, puedes realizar una inserción en la base de datos o cualquier otra operación
            procesarMedicion(result);
        }
    }

    /**
     * Método para procesar la medición una vez encontrado el beacon.
     * @param result El resultado del escaneo
     */
    private void procesarMedicion(ScanResult result) {
        // Aquí puedes insertar la medición en la base de datos o hacer cualquier otra operación.
        int major = Utilidades.bytesToInt(new TramaIBeacon(result.getScanRecord().getBytes()).getMajor());
        Log.d(ETIQUETA_LOG, "Valor de Major: " + major);

        // Realiza la operación, por ejemplo, enviar a un servidor
        insertarMedicion(major);
    }

    /**
     * Inserta la medición en el servidor utilizando Retrofit.
     */
    private void insertarMedicion(int major) {
        RetrofitClient.getApiService().insertarMedicion(new Medicion("Living Room", "CO2", major))
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            Log.d(ETIQUETA_LOG, "Medición insertada correctamente");
                        } else {
                            Log.d(ETIQUETA_LOG, "Error en la respuesta: " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Log.e(ETIQUETA_LOG, "Error al conectar con el servidor", t);
                    }
                });
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(ETIQUETA_LOG, "Servicio destruido");

        // Detener el escaneo cuando el servicio se destruye
        if (bluetoothLeScanner != null) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED) {
                bluetoothLeScanner.stopScan(scanCallback);
            }
        }
    }
}