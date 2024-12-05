package com.example.smariba_upv.airflow.LOGIC;
import android.content.Context;
import android.util.Log;

import com.example.smariba_upv.airflow.API.EnviarPeticionesUser;

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
}
