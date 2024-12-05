package com.example.smariba_upv.airflow.PRESENTACION;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import com.example.smariba_upv.airflow.R;

public class PerfilFragment extends Fragment {

    private TextView Bienvenido;
    private TextView perfilNombreCompleto;
    private TextView numeroTelefono;
    private TextView correoElectronico;
    private Button editarPerfilButton;

    public PerfilFragment() {
        // Required empty public constructor
    }

    public static PerfilFragment newInstance(String param1, String param2) {
        PerfilFragment fragment = new PerfilFragment();
        Bundle args = new Bundle();
        args.putString("param1", param1);
        args.putString("param2", param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            String mParam1 = getArguments().getString("param1");
            String mParam2 = getArguments().getString("param2");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_perfil, container, false);

        // Initialize TextViews
        Bienvenido = view.findViewById(R.id.Bienvenida);
        perfilNombreCompleto = view.findViewById(R.id.perfilNombreCompleto);
        numeroTelefono = view.findViewById(R.id.perfilNumeroTelefonico);
        correoElectronico = view.findViewById(R.id.perfilCorreo);
        editarPerfilButton = view.findViewById(R.id.perfilBtEditarPerfil);

        // Editar perfil button
        editarPerfilButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), EditarPerfilActivity.class);
            startActivityForResult(intent, 1);
        });

        // Load data from SharedPreferences
        loadUserData();

        return view;
    }

    // N:requestCode,N:resultCode,Intent:data => onActivityResult():void
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == AppCompatActivity.RESULT_OK) {
            // Actualizar los datos del usuario
            loadUserData();
        }
    }

    private void loadUserData() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);

        String nombre = sharedPreferences.getString("nombre", "Nombre no disponible");
        String apellidos = sharedPreferences.getString("apellidos", "Apellidos no disponibles");
        String email = sharedPreferences.getString("email", "Correo electrónico no disponible");
        String telefono = sharedPreferences.getString("telefono", "Número telefónico no disponible");

        Bienvenido.setText("Bienvenido, " + nombre);
        perfilNombreCompleto.setText(nombre + " " + apellidos);
        numeroTelefono.setText(telefono);
        correoElectronico.setText(email);
    }
}