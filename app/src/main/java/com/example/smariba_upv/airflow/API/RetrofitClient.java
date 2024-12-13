package com.example.smariba_upv.airflow.API;

import com.example.smariba_upv.airflow.LOGIC.DateTypeAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Date;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    private static Retrofit retrofitLocal = null;
    private static Retrofit retrofitAirVisual = null;

    private static final String LOCAL_BASE_URL = "http://192.168.1.19:3000/"; // URL del servidor local


    // Configura Retrofit para el servidor local
    public static ApiService getLocalApiService() {
        if (retrofitLocal == null) {
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(Date.class, new DateTypeAdapter())
                    .create();

            retrofitLocal = new Retrofit.Builder()
                    .baseUrl(LOCAL_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }
        return retrofitLocal.create(ApiService.class);
    }
}
