package com.example.smariba_upv.airflow.API;

import com.example.smariba_upv.airflow.LOGIC.DateTypeAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Date;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class RetrofitClient {

    private static Retrofit retrofitLocalJson = null;   // Para JSON
    private static Retrofit retrofitLocalHtml = null;   // Para HTML
    private static final String LOCAL_BASE_URL = "http://192.168.1.158:3000/"; // URL del servidor local

    // Configura Retrofit para el servidor local (JSON)
    public static ApiService getLocalApiService() {
        if (retrofitLocalJson == null) {
            // Crear un objeto Gson si necesitas usarlo para la conversión de JSON
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(Date.class, new DateTypeAdapter())
                    .setLenient()
                    .create();

            // Configuración para manejar respuestas JSON
            retrofitLocalJson = new Retrofit.Builder()
                    .baseUrl(LOCAL_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson))  // Solo para JSON
                    .build();
        }
        return retrofitLocalJson.create(ApiService.class);
    }

    // Configura Retrofit para el servidor local (HTML)
    public static ApiService getLocalApiServiceHtml() {
        if (retrofitLocalHtml == null) {
            // Configuración para manejar respuestas HTML (texto plano)
            retrofitLocalHtml = new Retrofit.Builder()
                    .baseUrl(LOCAL_BASE_URL)
                    .addConverterFactory(ScalarsConverterFactory.create())   // Solo para HTML
                    .build();
        }
        return retrofitLocalHtml.create(ApiService.class);
    }
}
