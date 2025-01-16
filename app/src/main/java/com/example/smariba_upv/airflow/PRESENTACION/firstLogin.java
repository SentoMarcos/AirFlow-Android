package com.example.smariba_upv.airflow.PRESENTACION;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smariba_upv.airflow.R;

public class firstLogin extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_scan);

        // Referencia a los botones
        Button scanQRButton = findViewById(R.id.scanQRButton);
        Button skipButton = findViewById(R.id.skipButton);

        // Acción del botón "Escanear QR"
        scanQRButton.setOnClickListener(v -> {

            // Por ejemplo, puedes abrir una nueva actividad para manejar el escaneo
            startActivity(new Intent(this, QRreader.class));
        });

        // Acción del botón "Saltar"
        skipButton.setOnClickListener(v -> {


            // Ejemplo de navegación
            startActivity(new Intent(this, LandActivity.class));
            finish(); // Cierra la actividad actual
        });
    }
}
