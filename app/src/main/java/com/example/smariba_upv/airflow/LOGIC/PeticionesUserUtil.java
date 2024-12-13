package com.example.smariba_upv.airflow.LOGIC;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.smariba_upv.airflow.API.EnviarPeticionesUser;
import com.example.smariba_upv.airflow.POJO.SensorObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class PeticionesUserUtil {

    private static final String TAG = "PeticionesUserUtil";
    /**
     * @function login
     * @param email
     * @param password
     * @brief Funcion que comprueba si el email y la contraseña cumplen el formato correcto. antres de enciarlos al servidor.
     * @return void
     **/
    // Texto:email, Texto:password, Context:context => login() => VoF
    public static boolean login(String email, String password, Context context) {

        EnviarPeticionesUser enviarPeticionesUser = new EnviarPeticionesUser(context);
        boolean res= enviarPeticionesUser.login(email, password);
        return res;
    }
    // N:id,Texto:nombre, Texto:apellidos, Texto:email, Texto:telefono, Texto:contrasenya, Context:context => editUsuario() => void
    public static void editUsuario(int id, String nombre, String apellidos, String email, String telefono, String contrasenya, Context context) {
        EnviarPeticionesUser enviarPeticionesUser = new EnviarPeticionesUser(context);
        enviarPeticionesUser.editUsuario(id, nombre, apellidos, email, telefono);
        Log.d(TAG, "editUsuario: ");
    }

    public static void registrarSensor(String jsonqr, Context context) {
        EnviarPeticionesUser enviarPeticionesUser = new EnviarPeticionesUser();
        try {
            JSONObject jsonObject = new JSONObject(jsonqr);
            String uuid = jsonObject.getString("uuid");
            String num_ref = jsonObject.getString("num_serie");
            SensorObject sensorObject = new SensorObject("none", num_ref, uuid, "NewSensor", false, 0);

            // recoger la id del usuario de shared preferences
            SharedPreferences sharedPreferences = context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
            int id = sharedPreferences.getInt("id", 0);

            enviarPeticionesUser.registrarSensor(id, sensorObject);
        } catch (JSONException e) {
            Log.e(TAG, "Error parsing JSON from QR code", e);
        }
    }
}
