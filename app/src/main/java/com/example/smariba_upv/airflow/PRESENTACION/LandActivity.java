package com.example.smariba_upv.airflow.PRESENTACION;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.example.smariba_upv.airflow.POJO.SensorObject;
import com.example.smariba_upv.airflow.R;
import com.example.smariba_upv.airflow.Services.ArduinoGetterService;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import android.Manifest;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.util.Log;
import android.view.MenuItem;

public class LandActivity extends AppCompatActivity {
    private static final int REQUEST_BLUETOOTH_PERMISSIONS = 1;
    private BottomNavigationView bottomNavigationView;

    //Bundle:savedInstanceState => onCreate():void
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
    //requestCode:int, permissions:String[], grantResults:int[] => onRequestPermissionsResult():void
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

    private void connectSensors() {

    }
    //item:MenuItem => onNavigationItemSelected()=>boolean
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
    //Fragment:fragment => loadFragment()=>boolean
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