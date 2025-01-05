// SensorFragment.java
package com.example.smariba_upv.airflow.PRESENTACION;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smariba_upv.airflow.API.EnviarPeticionesUser;
import com.example.smariba_upv.airflow.POJO.Medicion;
import com.example.smariba_upv.airflow.POJO.SensorObject;
import com.example.smariba_upv.airflow.PRESENTACION.Helpers.SensorAdapter;
import com.example.smariba_upv.airflow.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import android.os.Handler;
public class SensorFragment extends Fragment {
    private Button btnAddSensor;
    private RecyclerView recyclerView;
    private SensorAdapter sensorAdapter;
    private List<SensorObject> sensorList;
    private List<Medicion> medicionList;
    TextView tvNoSensors;
    private final Handler handler = new Handler();
    private boolean isUpdatePending = false;
    private static final long UPDATE_DELAY = 500; // 500 ms de retraso entre actualizaciones


    public SensorFragment() {
        // Required empty public constructor
    }

    @Override
    public void onResume() {
        super.onResume();
        añadirSensoresVista();
        LocalBroadcastManager.getInstance(requireContext())
                .registerReceiver(nuevaMedicionReceiver, new IntentFilter("NUEVA_MEDICION"));
        LocalBroadcastManager.getInstance(requireContext())
                .registerReceiver(nuevaMedicionReceiver, new IntentFilter("ACTUALIZAR_SENSOR"));
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(nuevaMedicionReceiver);
    }


    private final BroadcastReceiver nuevaMedicionReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String sensorJson = intent.getStringExtra("sensor");
            String medicionJson = intent.getStringExtra("medicion");


            Log.d("nuevaMedicionReceiver", "Datos recibidos: sensor=" + sensorJson + ", medicion=" + medicionJson);

            if (sensorJson != null && medicionJson != null ) {
                Gson gson = new Gson();
                SensorObject sensor = gson.fromJson(sensorJson, SensorObject.class);
                Medicion medicion = gson.fromJson(medicionJson, Medicion.class);

                //Log.d("nuevaMedicionReceiver", "Sensor deserializado: " + sensor.getId());
                //Log.d("nuevaMedicionReceiver", "Medicion deserializada: idSensor=" + medicion.getIdSensor() + ", valor=" + medicion.getValor());

                if (sensor.getId() != -1 && medicion.getIdSensor() == sensor.getId()) {
                    actualizarVista(sensor, medicion);
                } else {
                    Log.e("nuevaMedicionReceiver", "IDs no coinciden o no son válidos.");
                }
            } else {
                Log.e("nuevaMedicionReceiver", "Datos recibidos son nulos.");
            }
        }

    };

    private void actualizarVista(SensorObject sensor, Medicion medicion ) {
        // Actualizar o agregar el sensor
        boolean sensorExistente = false;
        for (int i = 0; i < sensorList.size(); i++) {
            if (sensorList.get(i).getId() == sensor.getId()) {
                sensorList.set(i, sensor); // Actualizar sensor existente
                sensorExistente = true;
                break;
            }
        }
        if (!sensorExistente) {
            sensorList.add(sensor); // Agregar nuevo sensor
        }

        // Actualizar o agregar la medición
        boolean medicionExistente = false;
        for (int i = 0; i < medicionList.size(); i++) {
            if (medicionList.get(i).getIdSensor() == medicion.getIdSensor()) {
                if (!datosIguales(medicion, medicionList.get(i))) {
                    medicionList.set(i, medicion); // Actualizar medición existente
                }
                medicionExistente = true;
                break;
            }
        }
        if (!medicionExistente) {
            medicionList.add(medicion); // Agregar nueva medición
        }

        Log.d("SensorFragment", "Datos actualizados: sensores=" + sensorList.size() + ", mediciones=" + medicionList.size());

        // Controlar la frecuencia de actualización del adaptador
        if (!isUpdatePending) {
            isUpdatePending = true;
            handler.postDelayed(() -> {
                sensorAdapter.updateData(sensorList, medicionList);
                isUpdatePending = false;
            }, UPDATE_DELAY);
        }
    }


    private boolean datosIguales(Medicion nuevaMedicion, Medicion antiguaMedicion) {
        return nuevaMedicion.getIdSensor() == antiguaMedicion.getIdSensor()
                && nuevaMedicion.getValor() == antiguaMedicion.getValor()
                && nuevaMedicion.getLatitud() == antiguaMedicion.getLatitud()
                && nuevaMedicion.getLongitud() == antiguaMedicion.getLongitud();
    }



    private void añadirSensoresVista() {
        // Limpiar la lista de sensores antes de agregar nuevos datos
        sensorList.clear();

        EnviarPeticionesUser enviarPeticionesUser = new EnviarPeticionesUser(getContext());
        enviarPeticionesUser.obtenerMisSensores(getContext());

        // Obtener datos de sensores desde SharedPreferences
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        String jsonSensores = sharedPreferences.getString("sensores", null);

        if (jsonSensores != null) {
            try {
                Gson gson = new Gson();
                Type type = new TypeToken<List<SensorObject>>() {}.getType();
                List<SensorObject> sensores = gson.fromJson(jsonSensores, type);

                if (sensores != null) {
                    sensorList.addAll(sensores); // Agregar los sensores a la lista
                }
            } catch (Exception e) {
                Log.e("SensorFragment", "Error parsing JSON: " + e.getMessage());
            }
        }

        // Actualizar visibilidad de la vista
        updateView(tvNoSensors);

        // Notificar cambios al adaptador
        sensorAdapter.updateData(sensorList, medicionList);
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