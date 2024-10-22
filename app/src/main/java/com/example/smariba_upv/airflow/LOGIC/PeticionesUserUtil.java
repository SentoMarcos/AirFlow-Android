package com.example.smariba_upv.airflow.LOGIC;
import android.content.Context;
import android.util.Log;

import com.example.smariba_upv.airflow.API.EnviarPeticionesUser;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class PeticionesUserUtil {

    /**
     * @function login
     * @param email
     * @param password
     * @brief Funcion que comprueba si el email y la contraseña cumplen el formato correcto. antres de enciarlos al servidor.
     * @return void
     **/

    public static void login(String email, String password, Context context) {

        EnviarPeticionesUser enviarPeticionesUser = new EnviarPeticionesUser(context);
        enviarPeticionesUser.login(email, password);
    }
}
