package com.example.smariba_upv.airflow.API;

import com.example.smariba_upv.airflow.LOGIC.DateTypeAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Date;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/********************************************
 * @class RetrofitClient
 * @brief Clase que proporciona la instancia de Retrofit configurada para realizar llamadas a la API REST.
 *******************************************/
public class RetrofitClient {

    private static final String BASE_URL = "http://192.168.1.19:3000/"; // Ensure this is correct
    private static Retrofit retrofit = null;

    public static ApiService getApiService() {
        if (retrofit == null) {
            // Configurar Gson con el adaptador para fechas
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(Date.class, new DateTypeAdapter())
                    .create();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }
        return retrofit.create(ApiService.class);
    }
}
