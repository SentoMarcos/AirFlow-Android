package com.example.smariba_upv.airflow.API;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.smariba_upv.airflow.MainActivity;
import com.example.smariba_upv.airflow.POJO.User;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EnviarPeticionesUser {
    static String TAG = "EnviarPeticionesUser";
    private Context context;

    public EnviarPeticionesUser(Context context) {
        this.context = context;
    }

    public EnviarPeticionesUser() {

    }


    public void login(String email, String password) {
        RetrofitClient.getApiService().login(new User(email, password)).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    // El usuario ha sido logueado correctamente
                    User user = response.body();
                    Log.d(TAG, "Usuario logueado: " + user.getNombre());

                    // Almacenar los datos en SharedPreferences
                    SharedPreferences sharedPreferences = context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("email", user.getEmail()); // Almacena el email
                    editor.putString("nombre", user.getNombre()); // Almacena el nombre
                    editor.putBoolean("isLoggedIn", true); // Marca que el usuario está logueado
                    editor.apply(); // Guarda los cambios

                    // Iniciar la nueva actividad
                    Intent intent = new Intent(context, MainActivity.class);
                    context.startActivity(intent);
                } else {
                    Log.e(TAG, "Error en la petición. Código de estado: " + response.code() + " - " + response.message());
                    try {
                        Log.e(TAG, "Cuerpo del error: " + response.errorBody().string());
                    } catch (IOException e) {
                        Log.e(TAG, "Error al leer el cuerpo del error", e);
                    }
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.e(TAG, "onFailure:", t);
            }
        });
    }
}