/********************************************
 * @file RetrofitClient.java
 * @brief Clase encargada de gestionar la instancia de Retrofit para interactuar con el servidor.
 * @version 1.0
 * @date 2024
 *******************************************/

package com.example.smariba_upv.airflow.API;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/********************************************
 * @class RetrofitClient
 * @brief Clase que proporciona la instancia de Retrofit configurada para realizar llamadas a la API REST.
 *******************************************/
public class RetrofitClient {

    private static final String BASE_URL = "http://192.168.1.72:3000/"; // Ensure this is correct
    private static Retrofit retrofit = null;

    public static ApiService getApiService() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit.create(ApiService.class);
    }
}
