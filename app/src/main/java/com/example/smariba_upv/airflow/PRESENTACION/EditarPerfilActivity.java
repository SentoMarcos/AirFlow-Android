package com.example.smariba_upv.airflow.PRESENTACION;
/**
 * @file EditarPerfilActivity.java
 * @brief Clase que permite editar el perfil del usuario
 * @details Clase que permite editar el perfil del usuario y guardar los cambios realizados
 */

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.smariba_upv.airflow.LOGIC.PeticionesUserUtil;
import com.example.smariba_upv.airflow.R;
/**
 * @class EditarPerfilActivity
 * @brief Clase que permite editar el perfil del usuario
 * @details Clase que permite editar el perfil del usuario y guardar los cambios realizados
 */
public class EditarPerfilActivity extends AppCompatActivity {
    /**
     * @param NombreEditText Campo de texto para el nombre
     * @param ApellidosEditText Campo de texto para los apellidos
     * @param EmailEditText Campo de texto para el correo electrónico
     * @param TelefonoEditText Campo de texto para el teléfono
     * @param GuardarCambiosButton Botón para guardar los cambios
     * @param VolverButton Botón para volver
     */
    EditText NombreEditText;
    EditText ApellidosEditText;
    EditText EmailEditText;
    EditText TelefonoEditText;
    Button GuardarCambiosButton;
    Button VolverButton;

    /**
     * @function onCreate
     * @param savedInstanceState
     */
    //Bundle:saveInstanceState => void
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_editar_perfil);

        NombreEditText = findViewById(R.id.TxtIUser);
        ApellidosEditText = findViewById(R.id.TxtIApellidos);
        EmailEditText = findViewById(R.id.TxtICorreo);
        TelefonoEditText = findViewById(R.id.TxtITelefono);

        GuardarCambiosButton = findViewById(R.id.btn_Guardar);
        VolverButton = findViewById(R.id.btn_Volver);

        cargarDatosUsuario();

        GuardarCambiosButton.setOnClickListener(v -> {
            guardarCambios();
        });

        VolverButton.setOnClickListener(v -> {
            finish();
        });
    }
    /**
     * @function guardarCambios
     * @brief Método que permite guardar los cambios realizados en el perfil del usuario
     * @details Método que permite guardar los cambios realizados en el perfil del usuario y enviar los datos a la base de datos
     */
    // En el método guardarCambios() de EditarPerfilActivity
    public void guardarCambios() {
        // recoger los datos de los campos de texto
        String nombre = NombreEditText.getText().toString();
        String apellidos = ApellidosEditText.getText().toString();
        String email = EmailEditText.getText().toString();
        String telefono = TelefonoEditText.getText().toString();

        // guardar los datos en la cache y recuperar el id del usuario
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        int id = sharedPreferences.getInt("id", 0);
        String contrasenya = sharedPreferences.getString("contrasenya", "contrasenya no disponible");
        editor.putString("nombre", nombre);
        editor.putString("apellidos", apellidos);
        editor.putString("email", email);
        editor.putString("telefono", telefono);
        editor.apply();
        Toast.makeText(this, "Datos guardados con éxito", Toast.LENGTH_SHORT).show();

        // Log the values to debug
        Log.d("EditarPerfilActivity", "ID: " + id);
        Log.d("EditarPerfilActivity", "Nombre: " + nombre);
        Log.d("EditarPerfilActivity", "Apellidos: " + apellidos);
        Log.d("EditarPerfilActivity", "Email: " + email);
        Log.d("EditarPerfilActivity", "Telefono: " + telefono);

        // enviar datos a la base de datos
        PeticionesUserUtil peticionesUserUtil = new PeticionesUserUtil();
        peticionesUserUtil.editUsuario(id, nombre, apellidos, email, telefono, contrasenya, this);

        // cerrar la actividad y devolver el resultado
        setResult(RESULT_OK);
        finish();
    }
    /**
     * @function cargarDatosUsuario
     * @brief Método que carga los datos del usuario
     * @details Método que carga los datos del usuario desde SharedPreferences
     */
    private void cargarDatosUsuario() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        String nombre = sharedPreferences.getString("nombre", "Nombre no disponible");
        String apellidos = sharedPreferences.getString("apellidos", "Apellidos no disponibles");
        String correo = sharedPreferences.getString("email", "Correo no disponible");
        String telefono = sharedPreferences.getString("Telefono", "Teléfono no disponible");

        NombreEditText.setText(nombre);
        ApellidosEditText.setText(apellidos);
        EmailEditText.setText(correo);
        TelefonoEditText.setText(telefono);
    }

}