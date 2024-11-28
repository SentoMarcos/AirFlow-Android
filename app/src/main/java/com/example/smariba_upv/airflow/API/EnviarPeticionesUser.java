package com.example.smariba_upv.airflow.API;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.util.Log;

import com.example.smariba_upv.airflow.API.MODELS.SensorResponse;
import com.example.smariba_upv.airflow.API.MODELS.UsuarioSensor;
import com.example.smariba_upv.airflow.POJO.SensorObject;
import com.example.smariba_upv.airflow.API.MODELS.SensorRequest;
import com.example.smariba_upv.airflow.POJO.User;
import com.example.smariba_upv.airflow.PRESENTACION.LandActivity;

import java.io.IOException;
import java.util.List;

import okhttp3.ResponseBody;
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

    public void registrarSensor(int id_usuario, SensorObject sensorObject) {
        SensorRequest sensorRequest = new SensorRequest(
                id_usuario,
                sensorObject.getId(),
                sensorObject.getEstado(),
                sensorObject.getNum_referencia(),
                sensorObject.getUuid(),
                sensorObject.getNombre(),
                sensorObject.isConexion(),
                sensorObject.getBateria()
        );

        RetrofitClient.getApiService().registrarSensor(sensorRequest).enqueue(new Callback<SensorResponse>() {
            @Override
            public void onResponse(Call<SensorResponse> call, Response<SensorResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    int sensorId = response.body().getSensorId();
                    //guardar id_sensor en shared preferences
                    SharedPreferences sharedPreferences = context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putInt("id_sensor", sensorId);
                    editor.apply();
                    Log.d(TAG, "Sensor registrado correctamente con ID: " + sensorId);
                } else {
                    Log.e(TAG, "Error en la petición. Código de estado: " + response.code() + " - " + response.message());
                }
            }

            @Override
            public void onFailure(Call<SensorResponse> call, Throwable t) {
                Log.e(TAG, "onFailure:", t);
            }
        });
    }


    // Método para actualizar sensor
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

    public void obtenerMisSensores(int id_usuario, Context context) {
        RetrofitClient.getApiService().getMisSensores(id_usuario).enqueue(new Callback<List<UsuarioSensor>>() {
            @Override
            public void onResponse(Call<List<UsuarioSensor>> call, Response<List<UsuarioSensor>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<UsuarioSensor> usuarioSensores = response.body();

                    // Procesar los sensores recibidos
                    for (UsuarioSensor usuarioSensor : usuarioSensores) {
                        Sensor sensor = usuarioSensor.getSensor();
                        Log.d("SensorInfo", "ID: " + sensor.getId() + ", Nombre: " + sensor.getName());
                    }

                } else {
                    Log.e("Error", "Error en la petición. Código: " + response.code() + " - " + response.message());
                }
            }

            @Override
            public void onFailure(Call<List<UsuarioSensor>> call, Throwable t) {
                Log.e("Error", "Fallo en la solicitud:", t);
            }
        });
    }

}