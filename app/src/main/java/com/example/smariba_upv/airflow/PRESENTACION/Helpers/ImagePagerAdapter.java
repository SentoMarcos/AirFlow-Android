package com.example.smariba_upv.airflow.PRESENTACION.Helpers;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smariba_upv.airflow.PRESENTACION.LogInActivity;
import com.example.smariba_upv.airflow.R;

import java.util.List;

public class ImagePagerAdapter extends RecyclerView.Adapter<ImagePagerAdapter.ViewHolder> {

    private final List<Integer> images;
    private final Context context;
    private static final int PERMISSION_REQUEST_CODE = 100;

    public ImagePagerAdapter(Context context, List<Integer> images) {
        this.context = context;
        this.images = images;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_image, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.d("ImagePagerAdapter", "onBindViewHolder called for position: " + position);

        holder.imageView.setImageResource(images.get(position));

        // Muestra el botón "Salir" solo en la última imagen
        if (position == images.size() - 1) {
            Log.d("ImagePagerAdapter", "Last image detected, showing exit button.");
            holder.exitButton.setVisibility(View.VISIBLE);
            holder.exitButton.setOnClickListener(v -> {
                Log.d("ImagePagerAdapter", "Exit button clicked, requesting permissions...");
                requestPermissions(() -> {
                    Log.d("ImagePagerAdapter", "Permissions granted, proceeding to next activity.");
                    proceedToNextActivity();
                });
            });
        } else {
            holder.exitButton.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    private void requestPermissions(Runnable onGranted) {
        Log.d("ImagePagerAdapter", "Requesting permissions...");

        String[] permissions = {
                Manifest.permission.ACCESS_FINE_LOCATION,   // Permiso de ubicación precisa
                Manifest.permission.ACCESS_COARSE_LOCATION, // Permiso de ubicación general
                Manifest.permission.BLUETOOTH_CONNECT,      // Permiso de acceso a dispositivos Bluetooth cercanos
                Manifest.permission.POST_NOTIFICATIONS,     // Permiso para enviar notificaciones
                Manifest.permission.CAMERA,                // Permiso para usar la cámara

        };

        boolean allPermissionsGranted = true;
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                allPermissionsGranted = false;
                Log.d("ImagePagerAdapter", "Permission not granted: " + permission);
                break;
            }
        }

        if (allPermissionsGranted) {
            Log.d("ImagePagerAdapter", "All permissions already granted.");
            onGranted.run(); // Los permisos ya están concedidos
        } else {
            Log.d("ImagePagerAdapter", "Requesting permissions from user...");
            ActivityCompat.requestPermissions(
                    ((Activity) context),
                    permissions,
                    PERMISSION_REQUEST_CODE
            );
        }
    }

    private void proceedToNextActivity() {
        // Cambia a la siguiente actividad
        Intent intent = new Intent(context, LogInActivity.class); // Cambia LogInActivity por tu actividad destino
        context.startActivity(intent);
        ((Activity) context).finish(); // Cierra la actividad actual
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        Button exitButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            exitButton = itemView.findViewById(R.id.exitButton);
        }
    }
}
