package com.example.smariba_upv.airflow.API;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.smariba_upv.airflow.API.MODELS.SensorResponse;
import com.example.smariba_upv.airflow.POJO.Medicion;
import com.example.smariba_upv.airflow.POJO.SensorObject;
import com.example.smariba_upv.airflow.API.MODELS.SensorRequest;
import com.example.smariba_upv.airflow.POJO.User;
import com.example.smariba_upv.airflow.PRESENTACION.LandActivity;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
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
        String uuidTrimmed = sensorObject.getUUID().trim();
        SensorRequest sensorRequest = new SensorRequest(id_usuario, sensorObject.getEstado(), sensorObject.getNum_ref(), uuidTrimmed, sensorObject.getNombre(), sensorObject.isConexion(), sensorObject.getBateria());
        //ver datos del sensor
        //Log.d(TAG, "Sensor: " + sensorObject.getId() + " - " + sensorObject.getEstado() + " - " + sensorObject.getNum_ref() + " - " + uuidTrimmed + " - " + sensorObject.getNombre() + " - " + sensorObject.isConexion() + " - " + sensorObject.getBateria());
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
    // EnviarPeticionesUser.java
    public void obtenerMisSensores(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        int id_usuario = sharedPreferences.getInt("id", 0); // Retrieve user ID
        Log.d(TAG, "ID del usuario: " + id_usuario);

        RetrofitClient.getApiService().getMisSensores(id_usuario).enqueue(new Callback<List<SensorResponse>>() {
            @Override
            public void onResponse(Call<List<SensorResponse>> call, Response<List<SensorResponse>> response) {
                if (response.isSuccessful()) {
                    List<SensorResponse> sensorResponses = response.body();
                    List<SensorObject> sensorObjects = new ArrayList<>();

                    Log.d(TAG, "Sensores: " + sensorResponses.toString());
                    for (SensorResponse sensorResponse : sensorResponses) {
                        SensorResponse.Sensor sensor = sensorResponse.getSensor();
                        SensorObject sensorObject = new SensorObject(
                                sensor.getIdSensor(),
                                sensor.getEstado(),
                                sensor.getNumReferencia(),
                                sensor.getUuid(),
                                sensor.getNombre(),
                                sensor.isConexion(),
                                sensor.getBateria()
                        );
                        sensorObjects.add(sensorObject);
                    }

                    //Log.d(TAG,sensorObjects.toString());

                    // Save sensorObjects in SharedPreferences as JSON
                    Gson gson = new Gson();
                    String jsonSensores = gson.toJson(sensorObjects);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("sensores", jsonSensores);
                    editor.apply();
                } else {
                    Log.e(TAG, "Error en la petición. Código de estado: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<SensorResponse>> call, Throwable t) {
                Log.e(TAG, "onFailure:", t);
            }
        });
    }

    public void createMedicion(Medicion medicion) {
        Log.d(TAG, "Creando medición: " + medicion);
        RetrofitClient.getApiService().createMedicion(medicion).enqueue(new Callback<Medicion>() {
            @Override
            public void onResponse(Call<Medicion> call, Response<Medicion> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "Medición creada correctamente: " + response.body());
                } else {
                    Log.e(TAG, "Error en la petición. Código de estado: " + response.code());
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "Sin cuerpo de error";
                        Log.e(TAG, "Cuerpo del error: " + errorBody);
                        if (response.code() == 400) {
                            // Manejar errores específicos de validación
                            Log.e(TAG, "Error de validación: Verifica los parámetros enviados.");
                        }
                    } catch (IOException e) {
                        Log.e(TAG, "Error al leer el cuerpo del error", e);
                    }
                }
            }

            @Override
            public void onFailure(Call<Medicion> call, Throwable t) {
                if (t instanceof IOException) {
                    Log.e(TAG, "Fallo de red o timeout:", t);
                } else {
                    Log.e(TAG, "Error inesperado:", t);
                }
            }
        });
    }

}