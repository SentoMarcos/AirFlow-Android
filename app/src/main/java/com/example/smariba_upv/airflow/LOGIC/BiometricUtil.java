/**
 * @autor: SentoMarcos
 * @file BiometricUtil.java
 * @brief Clase que gestiona la autenticación biométrica
 * */
package com.example.smariba_upv.airflow.LOGIC;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import java.util.concurrent.Executor;
/**
 * @class BiometricUtil
 * @brief Clase que gestiona la autenticación biométrica
 * */
public class BiometricUtil {

    /**
     * @interface BiometricAuthListener
     * @brief Interfaz para gestionar los eventos de la autenticación biométrica
     * */
    public interface BiometricAuthListener {
        void onAuthenticationSucceeded(BiometricPrompt.AuthenticationResult result);
        void onAuthenticationFailed();
        void onAuthenticationError(int errorCode, CharSequence errString);
    }
    /**
     * @func authenticate
     * @brief Método para autenticar al usuario mediante biometría
     * @param context Contexto de la actividad
     * @param listener Listener para gestionar los eventos de la autenticación
     * */
    //context:Context,listener:BiometricAuthListener ==> authenticate():void
    public static void authenticate(Context context, BiometricAuthListener listener) {
        Executor executor = ContextCompat.getMainExecutor(context);
        BiometricPrompt biometricPrompt = new BiometricPrompt((AppCompatActivity) context, executor, new BiometricPrompt.AuthenticationCallback() {
            //errorCode:E,errString:CharSequence ==> onAuthenticationError():void
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                Toast.makeText(context, "Error de autenticación: " + errString, Toast.LENGTH_SHORT).show();
                listener.onAuthenticationError(errorCode, errString);
            }
            //result:AuthenticationResult ==> onAuthenticationSucceeded():void
            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                Toast.makeText(context, "¡Autenticación exitosa!", Toast.LENGTH_SHORT).show();
                listener.onAuthenticationSucceeded(result);
            }
            //==> onAuthenticationFailed():void
            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Toast.makeText(context, "Fallo en la autenticación", Toast.LENGTH_SHORT).show();
                listener.onAuthenticationFailed();
            }
        });

        /**
         * @func authenticate
         * @brief Configurar la información de la autenticación
         **/

        BiometricManager biometricManager = BiometricManager.from(context);
        switch (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_WEAK | BiometricManager.Authenticators.DEVICE_CREDENTIAL)) {
            case BiometricManager.BIOMETRIC_SUCCESS:
                BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                        .setTitle("Inicio de sesión biométrico")
                        .setSubtitle("Inicie sesión con huella dactilar o reconocimiento facial")
                        .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_WEAK | BiometricManager.Authenticators.DEVICE_CREDENTIAL)
                        .build();
                biometricPrompt.authenticate(promptInfo);
                break;
            case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
                Toast.makeText(context, "Este dispositivo no tiene hardware biométrico", Toast.LENGTH_SHORT).show();
                break;
            case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
                Toast.makeText(context, "Hardware biométrico no disponible", Toast.LENGTH_SHORT).show();
                break;
            case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                Toast.makeText(context, "No hay datos biométricos registrados", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}