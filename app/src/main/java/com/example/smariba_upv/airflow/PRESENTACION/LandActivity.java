package com.example.smariba_upv.airflow.PRESENTACION;
/**
 * @file LandActivity.java
 * @brief Clase que permite visualizar la pantalla principal de la aplicación
 * @details Clase que permite visualizar la pantalla principal de la aplicación y navegar entre las diferentes opciones de la aplicación
 */

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.smariba_upv.airflow.POJO.SensorObject;
import com.example.smariba_upv.airflow.PRESENTACION.Helpers.SensorFragment;
import com.example.smariba_upv.airflow.R;
import com.example.smariba_upv.airflow.Services.ArduinoGetterService;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.util.Log;
import android.view.MenuItem;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

public class LandActivity extends AppCompatActivity {
    /**
     * @param REQUEST_BLUETOOTH_PERMISSIONS Permisos de Bluetooth
     * @param bottomNavigationView Vista de la barra de navegación
     */
    private static final int REQUEST_BLUETOOTH_PERMISSIONS = 1;
    private BottomNavigationView bottomNavigationView;

    /**
     * @function onCreate
     * @brief Método que se ejecuta al crear la actividad
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_land);

        bottomNavigationView = findViewById(R.id.navtabgroup);
        bottomNavigationView.setOnNavigationItemSelectedListener(this::onNavigationItemSelected);

        if (savedInstanceState == null) {
            loadFragment(new HomeFragment());
            bottomNavigationView.setSelectedItemId(R.id.item_1);
        }

        // Check Bluetooth permissions
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.BLUETOOTH_CONNECT,
                    Manifest.permission.BLUETOOTH_SCAN
            }, REQUEST_BLUETOOTH_PERMISSIONS);
        } else {
            connectSensors();
        }
    }

    /**
     * @function onRequestPermissionsResult
     * @param requestCode
     * @param permissions
     * @param grantResults
     * @brief Método que se ejecuta al recibir
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_BLUETOOTH_PERMISSIONS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                connectSensors();
            } else {
                Log.e("LandActivity", "Bluetooth permissions denied.");
            }
        }
    }

    /**
     * @function connectSensors
     * @brief Método que conecta los sensores
     * @details Método que conecta los sensores a la aplicación
     */
    private void connectSensors() {
        // Get sensor data from SharedPreferences
        SharedPreferences sharedPreferences = this.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        String jsonSensores = sharedPreferences.getString("sensores", null);
        if (jsonSensores != null) {
            try {
                Gson gson = new Gson();
                Type type = new TypeToken<List<SensorObject>>() {}.getType();
                List<SensorObject> sensores = gson.fromJson(jsonSensores, type);
                for (SensorObject sensor : sensores) {
                    startSensorService();
                }
            } catch (Exception e) {
                Log.e("LandActivity", "Error parsing sensor data.");
            }
        }
    }
    /**
     * @function startSensorService
     * @brief Método que inicia el servicio de sensores
     * @details Método que inicia el servicio de sensores
     */
    private void startSensorService() {
        Log.d("LandActivity", "Starting ArduinoGetterService...");
        Intent serviceIntent = new Intent(this, ArduinoGetterService.class);
        ContextCompat.startForegroundService(this, serviceIntent);
        Log.d("LandActivity", "ArduinoGetterService started.");
    }
    /**
     * @function onNavigationItemSelected
     * @param item
     * @return
     * @brief Método que se ejecuta al seleccionar un item de la barra de navegación
     */
    private boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment selectedFragment = null;

        if (item.getItemId() == R.id.item_1) {
            selectedFragment = new HomeFragment();
        } else if (item.getItemId() == R.id.item_2) {
            selectedFragment = new MapFragment();
        } else if (item.getItemId() == R.id.item_3) {
            selectedFragment = new SaludFragment();
        } else if (item.getItemId() == R.id.item_4) {
            selectedFragment = new SensorFragment();
        } else if (item.getItemId() == R.id.item_5) {
            selectedFragment = new PerfilFragment();
        }

        return loadFragment(selectedFragment);
    }
    /**
     * @function loadFragment
     * @param fragment
     * @return
     * @brief Método que carga un fragmento
     */
    private boolean loadFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
            return true;
        }
        return false;
    }
}
