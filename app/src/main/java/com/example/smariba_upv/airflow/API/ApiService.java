/********************************************
 * @file ApiService.java
 * @brief Interfaz para la API REST utilizada en la aplicación, donde se definen las llamadas GET y POST.
 * @version 1.0
 * @date 2024
 *******************************************/

package com.example.smariba_upv.airflow.API;

import com.example.smariba_upv.airflow.POJO.Medicion;

import com.example.smariba_upv.airflow.POJO.SensorRequest;
import com.example.smariba_upv.airflow.POJO.User;


import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;

/********************************************
 * @interface ApiService
 * @brief Interfaz que define las operaciones que se realizarán contra el servidor usando Retrofit.
 *******************************************/
public interface ApiService {

    /**
     * @fn Call<User> login(@Body User user)
     * @brief Método que realiza una petición POST al servidor para iniciar sesión.
     * @param[in] user Objeto de tipo User que contiene los datos de inicio de sesión.
     * **/
    @POST("/usuarios/login")  // Si usas un prefijo en el servidor
    Call<User> login(@Body User user);

    @PUT("/usuarios/editUsuario")
    Call<User> editUsuario(@Body User user);
    @POST("/insertar")
    Call<Void> insertarMedicion(@Body Medicion medicion);

    @POST("/usuarios/registrar-sensor")
    Call<ResponseBody> registrarSensor(@Body SensorRequest sensorRequest);

    //actualizar sensor
    @PUT("/usuarios/actualizar-sensor")
    Call<ResponseBody> actualizarSensor(@Body SensorRequest sensorRequest);
}



