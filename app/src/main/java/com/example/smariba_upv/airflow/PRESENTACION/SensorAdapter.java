package com.example.smariba_upv.airflow.PRESENTACION;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smariba_upv.airflow.API.MODELS.SensorResponse;
import com.example.smariba_upv.airflow.POJO.Medicion;
import com.example.smariba_upv.airflow.POJO.SensorObject;
import com.example.smariba_upv.airflow.R;

import java.util.ArrayList;
import java.util.List;

public class SensorAdapter extends RecyclerView.Adapter<SensorAdapter.SensorViewHolder> {
    /**
     * Adapter for the sensor listç
     * @param sensorList List of sensors
     * @param medicionList List of measurements
     */
    private final List<SensorObject> sensorList;
    private final List<Medicion> medicionList;

    /**
     * Constructor
     * @param sensorList List of sensors
     * @param medicionList List of measurements
     */
    public SensorAdapter(List<SensorObject> sensorList, List<Medicion> medicionList) {
        this.sensorList = sensorList != null ? sensorList : new ArrayList<>();
        this.medicionList = medicionList != null ? medicionList : new ArrayList<>();
    }

    /**
     * @param parent   The ViewGroup into which the new View will be added after it is bound to
     *                 an adapter position.
     * @param viewType The view type of the new View.
     * @return
     */
    @NonNull
    @Override
    public SensorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.sensor_item_layout, parent, false);
        return new SensorViewHolder(view);
    }
    /**
     * @param holder   The ViewHolder which should be updated to represent the contents of the item at
     *                 the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull SensorViewHolder holder, int position) {
        if (position < sensorList.size()) {
            SensorObject sensor = sensorList.get(position);

            // Configure sensor data
            holder.tvSensorName.setText(sensor.getNombre());
            holder.tvEstado.setText("Estado: " + sensor.getEstado());
            holder.tvNumReferencia.setText("Num. Referencia: " + sensor.getNum_ref());
            holder.tvConexion.setText("Conexión: " + (sensor.isConexion() ? "Conectado" : "Desconectado"));
            holder.tvBattery.setText("Battery: " + sensor.getBateria() + "%");
            holder.batteryIndicator.setProgress(
                    Math.max(0, Math.min(sensor.getBateria(), 100))
            );

            // Configure measurement data
            Medicion medicion = position < medicionList.size() ? medicionList.get(position) : null;
            if (medicion != null) {
                holder.tvGasType.setText("Gas Type: " + medicion.getTipoGas());
                holder.tvMeasurement.setText("Measurement: " + medicion.getValor());
                holder.tvDate.setText("Date: " + medicion.getFecha());
            } else {
                holder.tvGasType.setText("Gas Type: N/A");
                holder.tvMeasurement.setText("Measurement: N/A");
                holder.tvDate.setText("Date: N/A");
            }
        }
    }
/**
     * @return The total number of items in the data set held by the adapter.
     */
    @Override
    public int getItemCount() {
        return sensorList.size();
    }

    static class SensorViewHolder extends RecyclerView.ViewHolder {

        /**
         * ViewHolder for the sensor list
         * Contains the views for each sensor item
         * @param itemView The view for each sensor item
         * @param tvSensorName Sensor name
         * @param tvEstado Sensor state
         * @param tvNumReferencia Sensor reference number
         * @param tvConexion Sensor connection status
         * @param tvGasType Gas type
         * @param tvMeasurement Measurement value
         * @param tvDate Measurement date
         * @param tvBattery Battery percentage
         * @param batteryIndicator Battery indicator
         */
        TextView tvSensorName, tvEstado, tvNumReferencia, tvConexion, tvGasType, tvMeasurement, tvDate, tvBattery;
        ProgressBar batteryIndicator;

        public SensorViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSensorName = itemView.findViewById(R.id.tv_sensor_name);
            tvEstado = itemView.findViewById(R.id.tv_estado);
            tvNumReferencia = itemView.findViewById(R.id.tv_num_referencia);
            tvConexion = itemView.findViewById(R.id.tv_conexion);
            tvGasType = itemView.findViewById(R.id.tv_gas_type);
            tvMeasurement = itemView.findViewById(R.id.tv_measurement);
            tvDate = itemView.findViewById(R.id.tv_date);
            tvBattery = itemView.findViewById(R.id.tv_battery);
            batteryIndicator = itemView.findViewById(R.id.battery_indicator);
        }
    }
}
