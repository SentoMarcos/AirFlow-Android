package com.example.smariba_upv.airflow.PRESENTACION;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);





        // Configurar el video para reproducirse en bucle
        videoView.setOnPreparedListener(mp -> {
            mp.setLooping(true); // Repetir el video continuamente
            mp.setPlaybackParams(mp.getPlaybackParams().setSpeed(2.0f)); // Velocidad normal
            videoView.start();  // Iniciar la reproducción
        });

        // Cambiar de pantalla después de 5 segundos
        new Handler().postDelayed(() -> {
            videoView.stopPlayback(); // Detener el video
            if (isLoggedIn) {
                // El usuario ya está logueado, ir directamente a MainActivity
                Intent intent = new Intent(this, LandActivity.class);
                startActivity(intent);
                finish(); // Cierra la actividad actual
            } else {
                // El usuario no está logueado, mostrar la pantalla de inicio de sesión
                Intent intent = new Intent(this, PasaralainfoAndroid.class);
                startActivity(intent);
                finish(); // Cierra la actividad actual
            }
        }, TIEMPO);
    }
}
