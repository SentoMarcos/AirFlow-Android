// SensorFragment.java
package com.example.smariba_upv.airflow.PRESENTACION;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smariba_upv.airflow.API.EnviarPeticionesUser;
import com.example.smariba_upv.airflow.POJO.Medicion;
import com.example.smariba_upv.airflow.POJO.SensorObject;
import com.example.smariba_upv.airflow.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
public class SensorFragment extends Fragment {
    private Button btnAddSensor;
    private RecyclerView recyclerView;
    private SensorAdapter sensorAdapter;
    private List<SensorObject> sensorList;
    private List<Medicion> medicionList;
    TextView tvNoSensors;

    public SensorFragment() {
        // Required empty public constructor
    }

    @Override
    public void onResume() {
        super.onResume();
        // Clear the list and fetch new data
        sensorList.clear();
        añadirSensoresVista();
        sensorAdapter.notifyDataSetChanged(); // Notify adapter
    }

    private void añadirSensoresVista() {
        EnviarPeticionesUser enviarPeticionesUser = new EnviarPeticionesUser(getContext());
        enviarPeticionesUser.obtenerMisSensores(getContext());

        // Get sensor data from SharedPreferences
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        String jsonSensores = sharedPreferences.getString("sensores", null);

        if (jsonSensores != null) {
            try {
                Gson gson = new Gson();
                Type type = new TypeToken<List<SensorObject>>() {}.getType();
                List<SensorObject> sensores = gson.fromJson(jsonSensores, type);
                sensorList.addAll(sensores);
            } catch (Exception e) {
                Log.e("SensorFragment", "Error parsing JSON: " + e.getMessage());
            }
        }

        // Update view visibility
        updateView(tvNoSensors);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sensor, container, false);

        // Initialize views
        recyclerView = view.findViewById(R.id.rv_sensors);
        btnAddSensor = view.findViewById(R.id.add_sensor);
        tvNoSensors = view.findViewById(R.id.tv_no_sensors);

        // Set up RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialize lists and adapter
        sensorList = new ArrayList<>();
        medicionList = new ArrayList<>();
        medicionList.add(new Medicion(2, "CO2", 0, 0, 10));
        sensorAdapter = new SensorAdapter(sensorList, medicionList);
        recyclerView.setAdapter(sensorAdapter);

        // Add sensor button
        btnAddSensor.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), QRreader.class);
            startActivity(intent);
        });

        // Update view
        updateView(tvNoSensors);

        return view;
    }

    private void updateView(TextView tvNoSensors) {
        if (sensorList.isEmpty()) {
            tvNoSensors.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            tvNoSensors.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }
}
