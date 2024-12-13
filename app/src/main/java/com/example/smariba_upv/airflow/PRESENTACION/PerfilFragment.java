package com.example.smariba_upv.airflow.PRESENTACION;
/**
 * @file PerfilFragment.java
 * @brief Clase que permite visualizar el perfil del usuario
 * @details Clase que permite visualizar el perfil del usuario y editar la información del usuario
 */
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

/**
 * @class PerfilFragment
 * @brief Clase que permite visualizar el perfil del usuario
 * @details Clase que permite visualizar el perfil del usuario y editar la información del usuario
 */
public class PerfilFragment extends Fragment {
    /**
     * @param Bienvenido Bienvenida
     * @param perfilNombreCompleto Nombre completo del usuario
     * @param numeroTelefono Número telefónico del usuario
     * @param correoElectronico Correo electrónico del usuario
     * @param editarPerfilButton Botón para editar el perfil
     */
    private TextView Bienvenido;
    private TextView perfilNombreCompleto;
    private TextView numeroTelefono;
    private TextView correoElectronico;
    private Button editarPerfilButton;

    /**
     * @function PerfilFragment
     * @brief Constructor de la clase PerfilFragment
     */
    public PerfilFragment() {
        // Required empty public constructor
    }

    /**
     * @function newInstance
     * @param param1
     * @param param2
     * @return
     */
    public static PerfilFragment newInstance(String param1, String param2) {
        PerfilFragment fragment = new PerfilFragment();
        Bundle args = new Bundle();
        args.putString("param1", param1);
        args.putString("param2", param2);
        fragment.setArguments(args);
        return fragment;
    }
    /**
     * @function onCreate
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            String mParam1 = getArguments().getString("param1");
            String mParam2 = getArguments().getString("param2");
        }
    }
    /**
     * @function onCreateView
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
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
    /**
     * @function onActivityResult
     * @param requestCode
     * @param resultCode
     * @param data
     */
    // N:requestCode,N:resultCode,Intent:data => onActivityResult():void
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == AppCompatActivity.RESULT_OK) {
            // Actualizar los datos del usuario
            loadUserData();
        }
    }

    /**
     * @function loadUserData
     * @brief Método que carga los datos del usuario
     * @details Método que carga los datos del usuario desde SharedPreferences
     */
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