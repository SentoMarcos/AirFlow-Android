package com.example.smariba_upv.airflow.PRESENTACION;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricPrompt;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.smariba_upv.airflow.API.EnviarPeticionesUser;
import com.example.smariba_upv.airflow.LOGIC.BiometricUtil;
import com.example.smariba_upv.airflow.LOGIC.PeticionesUserUtil;
import android.Manifest;
import com.example.smariba_upv.airflow.R;

import java.util.ArrayList;
import java.util.List;

public class LogInActivity extends AppCompatActivity implements BiometricUtil.BiometricAuthListener {

    private static final String TAG = "LogInActivity";
    EditText EmailEditText;
    EditText passwordEditText;
    Button loginButton;
    ImageButton biometricButton;
    TextView errorText, forgotPasswordText;
    CheckBox Condiciones, rememberMeCheckBox;
    private static final int REQUEST_BLUETOOTH_PERMISSIONS = 1;

    //Bundle:savedInstanceState => onCreate():void
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_log_in);

        // Verificar y solicitar permisos de ubicación, notificación y acceso a dispositivos cercanos
        checkAndRequestPermissions();

        // Inicializar elementos
        EmailEditText = findViewById(R.id.correoInput);
        passwordEditText = findViewById(R.id.PasswordInput);
        loginButton = findViewById(R.id.logIn_btn);
        biometricButton = findViewById(R.id.biometric_btn);
        errorText = findViewById(R.id.errorText);
        forgotPasswordText = findViewById(R.id.forgotPasswordText);
        Condiciones = findViewById(R.id.ChekCondi);
        rememberMeCheckBox = findViewById(R.id.rememberMeCheckBox);

        // Configuración de eventos
        Condiciones.setOnClickListener(v -> showConditionsPopup());
        forgotPasswordText.setOnClickListener(v -> showForgotPasswordPopup());
    }

    private void checkAndRequestPermissions() {
        String[] permissions = {
                Manifest.permission.ACCESS_FINE_LOCATION,   // Permiso de ubicación precisa
                Manifest.permission.ACCESS_COARSE_LOCATION, // Permiso de ubicación general
                Manifest.permission.BLUETOOTH_CONNECT,      // Permiso de acceso a dispositivos Bluetooth cercanos
                Manifest.permission.POST_NOTIFICATIONS      // Permiso para mostrar notificaciones (Android 13+)
        };

        // Lista para permisos que faltan
        List<String> permissionsToRequest = new ArrayList<>();

        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(permission);
            }
        }

        // Si hay permisos que solicitar, hacer la solicitud
        if (!permissionsToRequest.isEmpty()) {
            ActivityCompat.requestPermissions(
                    this,
                    permissionsToRequest.toArray(new String[0]),
                    REQUEST_BLUETOOTH_PERMISSIONS
            );
        }
    }

    // requestCode:int, permissions:String[], grantResults:int[] => onRequestPermissionsResult():void
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_BLUETOOTH_PERMISSIONS) {
            for (int i = 0; i < permissions.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "Permiso concedido: " + permissions[i]);
                } else {
                    Log.d(TAG, "Permiso denegado: " + permissions[i]);
                }
            }
        }
    }

    private void showConditionsPopup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Condiciones de Uso");

// Obtener el texto con formato HTML desde los recursos
        String termsHtml = getString(R.string.terms_and_conditions);

// Convertir HTML a texto para Android
        CharSequence formattedText;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            formattedText = Html.fromHtml(termsHtml, Html.FROM_HTML_MODE_LEGACY);
        } else {
            formattedText = Html.fromHtml(termsHtml);
        }

// Configurar el mensaje del popup
        builder.setMessage(formattedText);

// Añadir botón de aceptación
        builder.setPositiveButton("Aceptar", (dialog, which) -> {
            dialog.dismiss();
        });

// Mostrar el popup
        builder.create().show();




    }

    private void showForgotPasswordPopup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.CustomDialogStyle);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.popup_forgot_password, null);
        builder.setView(dialogView);

        EditText emailInput = dialogView.findViewById(R.id.forgotPasswordEmailInput);
        Button sendButton = dialogView.findViewById(R.id.forgotPasswordSendButton);

        AlertDialog dialog = builder.create();
        sendButton.setOnClickListener(v -> {
            String email = emailInput.getText().toString();
            if (email.isEmpty()) {
                emailInput.setError("Por favor, ingresa tu correo electrónico.");
            } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                emailInput.setError("Correo electrónico no válido.");
            } else {
                // Lógica para enviar correo de recuperación
                dialog.dismiss();
                errorText.setText("Correo de recuperación enviado.");
            }
        });

        dialog.show();
    }
    //View view => logIn() => void
    public void logIn(View view) {
        String emailInput = EmailEditText.getText().toString();
        String passwordInput = passwordEditText.getText().toString();

        if (emailInput.isEmpty() || passwordInput.isEmpty()) {
            errorText.setText("Por favor, completa todos los campos.");
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(emailInput).matches()) {
            errorText.setText("Correo electrónico no válido.");
            return;
        }

        if (!Condiciones.isChecked()) {
            errorText.setText("Por favor, acepta las condiciones.");
            return;
        }

        boolean loginSuccessful = PeticionesUserUtil.login(emailInput, passwordInput, this);

        if (!loginSuccessful) {
            errorText.setText("Usuario o contraseña incorrectos.");
        } else {
            errorText.setText("");
            EnviarPeticionesUser enviarPeticionesUser = new EnviarPeticionesUser(this);
            enviarPeticionesUser.obtenerMisSensores(this);
        }
    }
    //View view => biometricLogIn() => void
    public void biometricLogIn(View view) {
        BiometricUtil.authenticate(this, this);
    }
    //BiometricPrompt.AuthenticationResult result => onAuthenticationSucceeded() => void
    @Override
    public void onAuthenticationSucceeded(BiometricPrompt.AuthenticationResult result) {
        Intent intent = new Intent(LogInActivity.this, LandActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onAuthenticationFailed() {
        errorText.setText("Fallo en la autenticación");
        Log.d(TAG, "Fallo en la autenticación");
    }
    //int errorCode, CharSequence errString => onAuthenticationError() => void
    @Override
    public void onAuthenticationError(int errorCode, CharSequence errString) {
        errorText.setText("Error de autenticación");
        Log.d(TAG, "Error de autenticación: " + errString);
    }
}
