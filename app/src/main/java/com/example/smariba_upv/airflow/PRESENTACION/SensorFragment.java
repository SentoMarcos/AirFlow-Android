// SensorFragment.java
package com.example.smariba_upv.airflow.PRESENTACION;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.smariba_upv.airflow.API.EnviarPeticionesUser;
import com.example.smariba_upv.airflow.POJO.SensorObject;
import com.example.smariba_upv.airflow.R;

public class SensorFragment extends Fragment {

    private TextView tvSensorName, tvTypeGas, tvMeasure, tvDate, tvBattery;
    private ProgressBar batteryIndicator;
    private SensorObject sensor;
    private CardView cardView;
    private Button btnRegisterSensor;

    private BroadcastReceiver sensorUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Obtener los datos del intent
            String uuid = intent.getStringExtra("sensor_uuid");
            String name = intent.getStringExtra("sensor_name");
            String typegas = intent.getStringExtra("sensor_typegas");
            int measure = intent.getIntExtra("sensor_measure", 0);
            String date = intent.getStringExtra("sensor_date");
            int battery = intent.getIntExtra("sensor_battery", 0);

            // Actualizar el objeto sensor
            sensor.setUuid(uuid);
            sensor.setNombre(name);
            sensor.setTypegas(typegas);
            sensor.setMeasure(measure);
            sensor.setDate(date);
            sensor.setBateria(battery);

            // Actualizar la interfaz de usuario
            updateUI();
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sensor, container, false);

        // Obtener referencias a los elementos del diseño
        tvSensorName = view.findViewById(R.id.tv_sensor_name);
        tvTypeGas = view.findViewById(R.id.tv_type_gas);
        tvMeasure = view.findViewById(R.id.tv_measure);
        tvDate = view.findViewById(R.id.tv_date);
        tvBattery = view.findViewById(R.id.tv_battery);
        batteryIndicator = view.findViewById(R.id.battery_indicator);
        cardView = view.findViewById(R.id.card_view);
        btnRegisterSensor = view.findViewById(R.id.add_sensor);

        // Inicializar el objeto sensor
        sensor = new SensorObject("", "Air Quality Sensor", "CO2", 75, "2024-11-20", 85);

        // Configurar los datos iniciales en el diseño
        updateUI();

        // Configurar el botón de registro de sensor
        btnRegisterSensor.setOnClickListener(this::registerSensor);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Registrar el BroadcastReceiver
        IntentFilter filter = new IntentFilter("com.example.smariba_upv.airflow.SENSOR_UPDATE");
        getActivity().registerReceiver(sensorUpdateReceiver, filter, Context.RECEIVER_NOT_EXPORTED);    }

    @Override
    public void onPause() {
        super.onPause();
        // Desregistrar el BroadcastReceiver
        getActivity().unregisterReceiver(sensorUpdateReceiver);
    }

    private void updateUI() {
        changeBackgroundColor(sensor.getMeasure());
        tvSensorName.setText(sensor.getNombre());
        tvTypeGas.setText("Gas Type: " + sensor.getTypegas());
        tvMeasure.setText("Measurement: " + sensor.getMeasure() + " ppm");
        tvDate.setText("Date: " + sensor.getDate());
        tvBattery.setText("Battery: " + sensor.getBateria() + "%");
        batteryIndicator.setProgress(sensor.getBateria());
    }

    private void changeBackgroundColor(int value) {
        Log.d("SensorFragment", "Changing background color, value: " + value);
        if (value > 200) {
            //pone un menaje de error en el la tarjeta
            tvSensorName.setText("Error en la medición");
        } else if (value > 100) {
            cardView.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.RojoPeligroso));
        } else if (value > 75) {
            cardView.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.NaranjaMalo));
        } else if (value > 50) {
            cardView.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.AmarilloMedio));
        } else if (value > 25) {
            cardView.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.VerdeBueno));
        } else {
            cardView.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.RosaExcelente));
        }
    }

    public void registerSensor(View view) {
        // Recoger la id del usuario de sharedPreferences
        int id_usuario = getContext().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE).getInt("id", 0);

        // Crear el objeto SensorObject con los datos actuales del sensor
        SensorObject sensorObject = new SensorObject(
                "Activo",
                "EPSG-GTI-PROY-3D",
                "EPSG-GTI-PROY-3D",
                "SensorName1",
                true,
                100
        );

        // Llamar al método registrarSensor de EnviarPeticionesUser
        EnviarPeticionesUser enviarPeticionesUser = new EnviarPeticionesUser(getContext());
        enviarPeticionesUser.registrarSensor(id_usuario, sensorObject);
    }

}