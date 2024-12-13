package com.example.smariba_upv.airflow.Services;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

public class DistanceTrackingService extends Service {

    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationCallback locationCallback;

    private double lastLatitude = 0;
    private double lastLongitude = 0;
    private double totalDistance = 0;

    private final Handler handler = new Handler();
    private static final long UPDATE_INTERVAL = 10000; // 30 segundos

    @Override
    public void onCreate() {
        super.onCreate();

        // Inicializar el cliente de ubicación
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        // Configurar la actualización de ubicación
        setupLocationUpdates();

        // Iniciar las actualizaciones periódicas
        handler.postDelayed(updateRunnable, UPDATE_INTERVAL);
    }

    private void setupLocationUpdates() {
        LocationRequest locationRequest = LocationRequest.create()
                .setInterval(5000) // Intervalo para obtener ubicación
                .setFastestInterval(2000)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                for (Location location : locationResult.getLocations()) {
                    if (lastLatitude != 0 && lastLongitude != 0) {
                        Location lastLocation = new Location("");
                        lastLocation.setLatitude(lastLatitude);
                        lastLocation.setLongitude(lastLongitude);

                        double distance = lastLocation.distanceTo(location);
                        totalDistance += distance;

                        // Log para depuración
                        Log.d("DistanceTrackingService", String.format("Nueva distancia: %.2f m (Acumulada: %.2f m)", distance, totalDistance));
                    }
                    lastLatitude = location.getLatitude();
                    lastLongitude = location.getLongitude();

                    // Log para depuración
                    Log.d("DistanceTrackingService", String.format("Nueva ubicación: lat=%.5f, lon=%.5f", lastLatitude, lastLongitude));
                }
            }
        };

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, getMainLooper());
        } else {
            Log.e("DistanceTrackingService", "Permisos de ubicación no concedidos.");
        }
    }

    private void enviarActualizacion() {
        Intent intent = new Intent("ACTUALIZAR_DISTANCIA");
        intent.putExtra("totalDistance", totalDistance);

        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);

        // Log para depuración
        Log.d("DistanceTrackingService", "Broadcast enviado. Distancia total: " + totalDistance);
    }


    private final Runnable updateRunnable = new Runnable() {
        @Override
        public void run() {
            enviarActualizacion();
            handler.postDelayed(this, UPDATE_INTERVAL);
        }
    };



    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
        if (fusedLocationProviderClient != null) {
            fusedLocationProviderClient.removeLocationUpdates(locationCallback);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null; // No usamos comunicación directa con el servicio
    }
}