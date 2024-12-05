package com.example.smariba_upv.airflow.API;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.smariba_upv.airflow.API.MODELS.SensorResponse;
import com.example.smariba_upv.airflow.POJO.SensorObject;
import com.example.smariba_upv.airflow.API.MODELS.SensorRequest;
import com.example.smariba_upv.airflow.POJO.User;
import com.example.smariba_upv.airflow.PRESENTACION.LandActivity;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EnviarPeticionesUser {
    /**
     * @param TAG     Etiqueta para los mensajes de log
     * @param context Contexto de la aplicación
     */
    private static final String TAG = "EnviarPeticionesUser";
    private Context context;

    /**
     * Constructor de la clase
     *
     * @param context Contexto de la aplicación
     * Context:context => EnviarPeticionesUser()
     */
    public EnviarPeticionesUser(Context context) {
        this.context = context;
    }

    /**
     * Constructor de la clase
     */
    public EnviarPeticionesUser() {
    }
    /**
     * @brief Método para registrar un usuario
     *
     * @param email     Correo electrónico del usuario
     * @param password  Contraseña del usuario
     *  Texto:email, Texto:password => login() => VoF
     * **/
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

                        Intent intent = new Intent(context, LandActivity.class);
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

    /**
     * @brief Método para editar un usuario
     * @param id        ID del usuario
     * @param nombre    Nombre del usuario
     * @param apellidos Apellidos del usuario
     * @param email     Correo electrónico del usuario
     * @param telefono  Teléfono del usuario
     *  N:id, Texto:nombre, Texto:apellidos, Texto:email, Texto:telefono => registrarUsuario()
     * **/
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

    /**
     * @brief Método para registrar un sensor
     * @param id_usuario ID del usuario
     * @param sensorObject Objeto del sensor
     *  N:id_usuario, Objeto:sensorObject => registrarSensor()
     * **/
    public void registrarSensor(int id_usuario, SensorObject sensorObject) {
        SensorRequest sensorRequest = new SensorRequest(id_usuario, sensorObject.getId(), sensorObject.getEstado(), sensorObject.getNum_ref(), sensorObject.getUUID(), sensorObject.getNombre(), sensorObject.isConexion(), sensorObject.getBateria());
        RetrofitClient.getApiService().registrarSensor(sensorRequest).enqueue(new Callback<SensorRequest>() {
            @Override
            public void onResponse(Call<SensorRequest> call, Response<SensorRequest> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "Sensor registrado correctamente");
                } else {
                    Log.e(TAG, "Error en la petición. Código de estado: " + response.code() + " - " + response.message());
                }
            }

            @Override
            public void onFailure(Call<SensorRequest> call, Throwable t) {
                Log.e(TAG, "onFailure:", t);
            }
        });
    }

    /**
     * @brief Método para actualizar un sensor
     * @param id_sensor ID del sensor
     * @param estado    Estado del sensor
     * @param conexion  Conexión del sensor
     * @param bateria   Batería del sensor
     *  N:id_sensor, Texto:estado, VoF:conexion, N:bateria => actualizarSensor()
     * **/
    public void actualizarSensor(int id_sensor, String estado, boolean conexion, int bateria) {
        SensorRequest sensorRequest = new SensorRequest(id_sensor, estado, conexion, bateria);
        RetrofitClient.getApiService().actualizarSensor(sensorRequest).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "Sensor actualizado correctamente");
                } else {
                    Log.e(TAG, "Error en la petición. Código de estado: " + response.code() + " - " + response.message());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e(TAG, "onFailure:", t);
            }
        });
    }
    /**
     * @brief Método para obtener los sensores de un usuario
     * @param context   Contexto de la aplicación
     *  Context:context => obtenerMisSensores()
     * **/

    public void obtenerMisSensores(Context context) {
        RetrofitClient.getApiService().getSensoresUser().enqueue(new Callback<List<SensorObject>>() {
            @Override
            public void onResponse(Call<List<SensorObject>> call, Response<List<SensorObject>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<SensorObject> sensores = response.body();
                    // Guardar la lista de sensores en SharedPreferences
                    SharedPreferences sharedPreferences = context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    Gson gson = new Gson();
                    String jsonSensores = gson.toJson(sensores);
                    editor.putString("sensores", jsonSensores);
                    editor.apply();
                    Log.d(TAG, "Sensores obtenidos y almacenados: " + sensores.size());
                } else {
                    Log.e(TAG, "Error en la petición. Código de estado: " + response.code() + " - " + response.message());
                }
            }

            @Override
            public void onFailure(Call<List<SensorObject>> call, Throwable t) {
                Log.e(TAG, "onFailure:", t);
            }
        });
    }
}