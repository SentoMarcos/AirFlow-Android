/**
 * @autor Sento
 * @file LogInActivity.java
 * @brief Clase que gestiona el inicio de sesión de la aplicación
 * */
package com.example.smariba_upv.airflow;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricPrompt;
/**
 * @class LogInActivity
 * @brief Clase que gestiona el inicio de sesión de la aplicación
 * */
public class LogInActivity extends AppCompatActivity implements BiometricUtil.BiometricAuthListener {

    /**
     * @brief Declaración de variables
     * @param EmailEditText Campo de texto para introducir el correo
     * @param passwordEditText Campo de texto para introducir la contraseña
     * @param loginButton Botón para iniciar sesión
     * @param biometricButton Botón para iniciar la autenticación biométrica
     * @param email Correo de prueba
     * @param password Contraseña de prueba
     * @param errorText Texto de error
     * */

    EditText EmailEditText;
    EditText passwordEditText;
    Button loginButton;
    ImageButton biometricButton;
    TextView errorText;

    String email = "admin@g.com";
    String password  = "admin";

    /**
     * @func onCreate
     * @brief Método que se ejecuta al crear la actividad
     * @param savedInstanceState Instancia de la actividad
     * */
    //Bundle:savedInstanceState ==> onCreate() : void
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_log_in);

        EmailEditText = findViewById(R.id.correoInput);
        passwordEditText = findViewById(R.id.PasswordInput);
        loginButton = findViewById(R.id.logIn_btn);
        biometricButton = findViewById(R.id.biometric_btn);
        errorText = findViewById(R.id.errorText);
    }

    /**
     * @func logIn
     * @brief Método que se ejecuta al pulsar el botón de iniciar sesión
     * @param view Vista de la actividad
     * @details Comprueba si el correo y la contraseña introducidos son correctos
     * */
    //View:view ==> logIn() : void
    public void logIn(View view){
        String emailInput = EmailEditText.getText().toString();
        String passwordInput = passwordEditText.getText().toString();

        if(emailInput.equals(email) && passwordInput.equals(password)){
            Intent intent = new Intent(LogInActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }else{
            if(emailInput.isEmpty() || passwordInput.isEmpty()){
                errorText.setText("Rellena todos los campos");
                return;
            }else if (!emailInput.equals(email)){
                errorText.setText("Correo incorrecto");
                return;
            }else if (!passwordInput.equals(password)){
                errorText.setText("Contraseña incorrecta");
                return;
            }else {
                errorText.setText("Error desconocido");
            }
        }
    }

    /**
     * @func biometricLogIn
     * @brief Método que se ejecuta al pulsar el botón de autenticación biométrica
     * @param view Vista de la actividad
     * @details Inicia la autenticación biométrica
     * */
    //View:view ==> biometricLogIn() : void
    public void biometricLogIn(View view){
        BiometricUtil.authenticate(this, this);
    }

    /**
     * @func onAuthenticationSucceeded
     * @brief Método que se ejecuta al autenticar con éxito
     * */
    // BiometricPrompt.AuthenticationResult:result ==> onAuthenticationSucceeded() : void
    @Override
    public void onAuthenticationSucceeded(BiometricPrompt.AuthenticationResult result) {
        Intent intent = new Intent(LogInActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * @func onAuthenticationFailed
     * @brief Método que se ejecuta al fallar la autenticación
     * */
    // ==> onAuthenticationFailed() : void
    @Override
    public void onAuthenticationFailed() {
        errorText.setText("Fallo en la autenticación");
    }

    /**
     * @func onAuthenticationError
     * @brief Método que se ejecuta al producirse un error en la autenticación
     * @param errorCode Código de error
     * @param errString Mensaje de error
     * */
    // Entero:errorCode Caracter:errString ==> onAuthenticationError() : void
    @Override
    public void onAuthenticationError(int errorCode, CharSequence errString) {
        errorText.setText("Error de autenticación");
    }
}