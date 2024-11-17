package com.example.smariba_upv.airflow.API;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.smariba_upv.airflow.MainActivity;
import com.example.smariba_upv.airflow.POJO.User;
import com.example.smariba_upv.airflow.PerfilActivity;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EnviarPeticionesUser {
    private static final String TAG = "EnviarPeticionesUser";
    private Context context;

    public EnviarPeticionesUser(Context context) {
        this.context = context;
    }

    public EnviarPeticionesUser() {
    }

    public boolean login(String email, String password) {
        final boolean[] loginSuccessful = {false};

        RetrofitClient.getApiService().login(new User(email, password)).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    User user = response.body();
                    Log.d(TAG, "Usuario logueado: " + user.getNombre());

                    if (context != null) {
                        SharedPreferences sharedPreferences = context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putInt("id", user.getId());
                        editor.putString("email", user.getEmail());
                        editor.putString("nombre", user.getNombre());
                        editor.putString("apellidos", user.getApellidos());
                        editor.putString("Telefono", user.getTelefono());
                        editor.putBoolean("isLoggedIn", true);
                        editor.apply();

                        Intent intent = new Intent(context, PerfilActivity.class);
                        context.startActivity(intent);
                    } else {
                        Log.e(TAG, "Context is null");
                    }
                    loginSuccessful[0] = true;
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

        return loginSuccessful[0];
    }

    // Método para editar usuario sin contraseña
    public void editUsuario(int id, String nombre, String apellidos, String email, String telefono) {
        if (id > 0 && nombre != null && email != null && telefono != null) {
            RetrofitClient.getApiService().editUsuario(new User(id, nombre, apellidos, email, telefono)).enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    if (response.isSuccessful()) {
                        Log.d(TAG, "Usuario actualizado correctamente");
                    } else {
                        Log.e(TAG, "Error en la petición. Código de estado: " + response.code() + " - " + response.message());
                    }
                }

                @Override
                public void onFailure(Call<User> call, Throwable t) {
                    Log.e(TAG, "onFailure:", t);
                }
            });
        } else {
            Log.e(TAG, "Faltan parámetros obligatorios");
        }
    }
}