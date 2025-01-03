/**
 * @file SensorAdapter.java
 * @brief Adadpter para los elementos del recyclerView de sensores
 * @author Sento Marcos Ibarra
 * */
package com.example.smariba_upv.airflow.PRESENTACION;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

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
        holder.tvdistancia.setText("Distancia: " + sensor.getDistancia());

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
    }

    @Override
    public int getItemCount() {
        return sensorList.size();
    }

    static class SensorViewHolder extends RecyclerView.ViewHolder {
        TextView tvSensorName, tvEstado, tvBattery, tvGasType, tvMeasurement, tvDate, tvRef, tvdistancia;
        ProgressBar batteryIndicator;

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
            tvdistancia = itemView.findViewById(R.id.tv_distancia);
        }
    }
}
