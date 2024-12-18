package com.example.smariba_upv.airflow.PRESENTACION;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.smariba_upv.airflow.R;
//intent import


public class PaginaDeCarga extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_pagina_de_carga);
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);



        if (isLoggedIn) {
            // El usuario ya está logueado, ir directamente a MainActivity
            Intent intent = new Intent(this, LandActivity.class);
            startActivity(intent);
            finish(); // Cierra la actividad actual
        } else {
            // El usuario no está logueado, mostrar la pantalla de inicio de sesión
            Intent intent = new Intent(this, LogInActivity.class);
            startActivity(intent);
            finish(); // Cierra la actividad actual
        }

    }
}