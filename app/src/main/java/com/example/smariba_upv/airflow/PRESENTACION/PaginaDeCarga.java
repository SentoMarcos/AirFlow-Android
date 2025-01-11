package com.example.smariba_upv.airflow.PRESENTACION;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smariba_upv.airflow.R;

public class PaginaDeCarga extends AppCompatActivity {

    private final int TIEMPO = 5000; // Tiempo en milisegundos (5 segundos)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pagina_de_carga);

        // Configurar el VideoView
        VideoView videoView = findViewById(R.id.videoView);
        Uri videoUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.flow3);
        videoView.setVideoURI(videoUri);

        // Configurar el video para reproducirse en bucle
        videoView.setOnPreparedListener(mp -> {
            mp.setLooping(true); // Repetir el video continuamente
            videoView.start();  // Iniciar la reproducción
        });

        // Cambiar de pantalla después de 5 segundos
        new Handler().postDelayed(() -> {
            videoView.stopPlayback(); // Detener el video
            Intent intent = new Intent(PaginaDeCarga.this, LogInActivity.class); // Cambia LogInActivity a tu actividad de destino
            startActivity(intent);
            finish(); // Finalizar la actividad actual
        }, TIEMPO);
    }
}
