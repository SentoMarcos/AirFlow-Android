/**
 * @file SensorAdapter.java
 * @brief Adadpter para los elementos del recyclerView de sensores
 * @author Sento Marcos Ibarra
 * */
package com.example.smariba_upv.airflow.PRESENTACION.Helpers;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smariba_upv.airflow.API.EnviarPeticionesUser;
import com.example.smariba_upv.airflow.POJO.Medicion;
import com.example.smariba_upv.airflow.POJO.SensorObject;
import com.example.smariba_upv.airflow.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SensorAdapter extends RecyclerView.Adapter<SensorAdapter.SensorViewHolder> {
    /**
     * @param sensorList Lista con los objetos sensor del usuario
     * @param medicionMap HashMap con los las mediciones tomadas por el usuario
     * */
    private final List<SensorObject> sensorList;
    private final HashMap<Integer, Medicion> medicionMap;

    /**
     * @function SensorAdapter
     * @brief Constructo de la clase SensorAdapter
     * @details SensorObject[]
     * */
    public SensorAdapter(List<SensorObject> initialSensorList, List<Medicion> initialMedicionList) {
        this.sensorList = new ArrayList<>();
        this.medicionMap = new HashMap<>();
        updateData(initialSensorList, initialMedicionList);
    }

    public void updateData(List<SensorObject> newSensorList, List<Medicion> newMedicionList ) {
        // Actualizar lista de sensores
        sensorList.clear();
        if (newSensorList != null) {
            sensorList.addAll(newSensorList);
        }

        // Actualizar mapa de mediciones
        medicionMap.clear();
        if (newMedicionList != null) {
            for (Medicion medicion : newMedicionList) {
                medicionMap.put(medicion.getIdSensor(), medicion);
            }
        }

        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SensorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.sensor_item_layout, parent, false);
        return new SensorViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull SensorViewHolder holder, int position) {
        SensorObject sensor = sensorList.get(position);

        // Configurar datos del sensor
        holder.tvSensorName.setText(sensor.getNombre());
        holder.tvEstado.setText("Estado: " + (sensor.isConexion() ? "Conectado" : "Desconectado"));
        holder.tvBattery.setText("Batería: " + (sensor.isConexion() ? sensor.getBateria() + "%" : "N/A"));
        holder.batteryIndicator.setProgress(sensor.isConexion() ? sensor.getBateria() : 0);

        // Determinar nivel de señal
        int signalIconResId;
        Double distancia = sensor.getDistancia();
        if (!sensor.isConexion() || distancia == null) {
            signalIconResId = R.drawable.baseline_signal_wifi_0_bar_24; // Sin señal
        } else if (sensor.getDistancia() > 100) {
            signalIconResId = R.drawable.baseline_network_wifi_1_bar_24; // Señal baja
        } else if (sensor.getDistancia() > 50) {
            signalIconResId = R.drawable.baseline_network_wifi_2_bar_24; // Señal media
        } else if (sensor.getDistancia() > 20) {
            signalIconResId = R.drawable.baseline_network_wifi_3_bar_24; // Señal alta
        } else {
            signalIconResId = R.drawable.baseline_signal_wifi_4_bar_24; // Señal excelente
        }

        holder.ivSignalStrength.setImageResource(signalIconResId);

        // Manejar desconexión
        Medicion medicionAsociada = medicionMap.get(sensor.getId());
        if (medicionAsociada != null && sensor.isConexion()) {
            holder.tvGasType.setText("Tipo de Gas: " + medicionAsociada.getTipoGas());
            holder.tvMeasurement.setText("Medición: " + medicionAsociada.getValor());
            holder.tvDate.setText("Fecha: " + medicionAsociada.getFecha());
        } else {
            holder.tvGasType.setText("Tipo de Gas: N/A");
            holder.tvMeasurement.setText("Medición: N/A");
            holder.tvDate.setText("Fecha: N/A");
        }

        holder.btneditar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Crear popup para editar sensor
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                LayoutInflater inflater = LayoutInflater.from(v.getContext());
                View dialogView = inflater.inflate(R.layout.dialog_edit_sensor, null);
                builder.setView(dialogView);

                EditText etSensorName = dialogView.findViewById(R.id.et_sensor_name);

                etSensorName.setText(sensor.getNombre());

                builder.setTitle("Editar Sensor")
                        .setPositiveButton("Guardar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //get id sensor
                                int idSensor = sensor.getId();

                                EnviarPeticionesUser enviarPeticionesUser = new EnviarPeticionesUser();
                                enviarPeticionesUser.editNombreSensor(idSensor, etSensorName.getText().toString());
                                sensor.setNombre(etSensorName.getText().toString());
                                notifyDataSetChanged();
                            }
                        })
                        .setNegativeButton("Cancelar", null);

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return sensorList.size();
    }

    static class SensorViewHolder extends RecyclerView.ViewHolder {
        TextView tvSensorName, tvEstado, tvBattery, tvGasType, tvMeasurement, tvDate, tvRef;
        ImageView ivSignalStrength;
        ProgressBar batteryIndicator;
        ImageButton btneditar;

        public SensorViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSensorName = itemView.findViewById(R.id.tv_sensor_name);
            tvEstado = itemView.findViewById(R.id.tv_estado);
            tvBattery = itemView.findViewById(R.id.tv_battery);
            tvRef = itemView.findViewById(R.id.tv_num_referencia);
            batteryIndicator = itemView.findViewById(R.id.battery_indicator);
            tvGasType = itemView.findViewById(R.id.tv_gas_type);
            tvMeasurement = itemView.findViewById(R.id.tv_measurement);
            tvDate = itemView.findViewById(R.id.tv_date);
            ivSignalStrength = itemView.findViewById(R.id.iv_signal_strength);
            btneditar = itemView.findViewById(R.id.btn_edit_sensor);
        }
    }
}
