package com.example.smariba_upv.airflow;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class PerfilActivity extends AppCompatActivity {

    public TextView txtNombre;
    public TextView txtApellidos;
    public TextView txtCorreo;
    public TextView txtTelefono;
    public Button btnEditarPerfil;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_perfil);

        txtNombre = findViewById(R.id.textVNombre);
        txtApellidos = findViewById(R.id.textVApellidos);
        txtCorreo = findViewById(R.id.textVCorreo);
        txtTelefono = findViewById(R.id.textVTelefono);
        btnEditarPerfil = findViewById(R.id.btnEditarUser);

        cargarDatosUsuario();
        btnEditarPerfil.setOnClickListener(v -> {
            // Abrir la actividad de editar perfil
            Intent intent = new Intent(PerfilActivity.this, EditarPerfilActivity.class);
            startActivity(intent);
        });
    }

    // Recoger datos del usuario de la cache guardada al hacer el login
    private void cargarDatosUsuario() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        String nombre = sharedPreferences.getString("nombre", "Nombre no disponible");
        String apellidos = sharedPreferences.getString("apellidos", "Apellidos no disponibles");
        String correo = sharedPreferences.getString("email", "Correo no disponible");
        String telefono = sharedPreferences.getString("Telefono", "Teléfono no disponible");

        txtNombre.setText(nombre);
        txtApellidos.setText(apellidos);
        txtCorreo.setText(correo);
        txtTelefono.setText(telefono);
    }

}