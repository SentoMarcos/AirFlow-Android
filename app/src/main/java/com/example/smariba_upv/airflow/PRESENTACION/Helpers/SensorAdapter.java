package com.example.smariba_upv.airflow.PRESENTACION.Helpers;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smariba_upv.airflow.API.EnviarPeticionesUser;
import com.example.smariba_upv.airflow.POJO.Medicion;
import com.example.smariba_upv.airflow.POJO.SensorObject;
import com.example.smariba_upv.airflow.PRESENTACION.SensorDetailsBottomSheet;
import com.example.smariba_upv.airflow.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SensorAdapter extends RecyclerView.Adapter<SensorAdapter.SensorViewHolder> {
    private final List<SensorObject> sensorList;
    private final HashMap<Integer, Medicion> medicionMap;

    public SensorAdapter(List<SensorObject> initialSensorList, List<Medicion> initialMedicionList) {
        this.sensorList = new ArrayList<>();
        this.medicionMap = new HashMap<>();
        updateData(initialSensorList, initialMedicionList);
    }

    public void updateData(List<SensorObject> newSensorList, List<Medicion> newMedicionList) {
        sensorList.clear();
        if (newSensorList != null) {
            sensorList.addAll(newSensorList);
        }

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

        holder.tvSensorName.setText(sensor.getNombre());
        holder.tvEstado.setText("Estado: " + (sensor.isConexion() ? "Conectado" : "Desconectado"));
        holder.tvBattery.setText("Batería: " + (sensor.isConexion() ? sensor.getBateria() + "%" : "N/A"));
        holder.batteryIndicator.setProgress(sensor.isConexion() ? sensor.getBateria() : 0);

        int signalIconResId;
        Double distancia = sensor.getDistancia();
        if (!sensor.isConexion() || distancia == null) {
            signalIconResId = R.drawable.baseline_signal_wifi_0_bar_24;
        } else if (sensor.getDistancia() > 100) {
            signalIconResId = R.drawable.baseline_network_wifi_1_bar_24;
        } else if (sensor.getDistancia() > 50) {
            signalIconResId = R.drawable.baseline_network_wifi_2_bar_24;
        } else if (sensor.getDistancia() > 20) {
            signalIconResId = R.drawable.baseline_network_wifi_3_bar_24;
        } else {
            signalIconResId = R.drawable.baseline_signal_wifi_4_bar_24;
        }

        holder.ivSignalStrength.setImageResource(signalIconResId);

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

        holder.itemView.setOnClickListener(v -> {
            EnviarPeticionesUser enviarPeticionesUser = new EnviarPeticionesUser();
            enviarPeticionesUser.getMedicionesPorSensor(sensor.getId(), new Callback<List<Medicion>>() {
                @Override
                public void onResponse(Call<List<Medicion>> call, Response<List<Medicion>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        List<Medicion> mediciones = response.body();
                        showSensorDetails(v.getContext(), sensor, mediciones);
                    } else {
                        Log.e("SensorAdapter", "No se pudieron cargar las mediciones. " + response.message());
                    }
                }

                @Override
                public void onFailure(Call<List<Medicion>> call, Throwable t) {
                    Log.e("SensorAdapter", "Error al obtener mediciones del sensor", t);
                }
            });
        });

        holder.btneditar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

    private void showSensorDetails(Context context, SensorObject sensor, List<Medicion> mediciones) {
        SensorDetailsBottomSheet bottomSheet = new SensorDetailsBottomSheet(sensor, mediciones);
        bottomSheet.show(((FragmentActivity) context).getSupportFragmentManager(), "SensorDetailsBottomSheet");
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