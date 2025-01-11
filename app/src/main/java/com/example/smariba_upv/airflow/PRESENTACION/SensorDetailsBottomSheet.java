package com.example.smariba_upv.airflow.PRESENTACION;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.smariba_upv.airflow.POJO.Medicion;
import com.example.smariba_upv.airflow.POJO.SensorObject;
import com.example.smariba_upv.airflow.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SensorDetailsBottomSheet extends BottomSheetDialogFragment {

    private SensorObject sensor;
    private List<Medicion> mediciones;

    public SensorDetailsBottomSheet(SensorObject sensor, List<Medicion> mediciones) {
        this.sensor = sensor;
        this.mediciones = mediciones;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_sensor_details, container, false);

        // Referencias a los elementos del layout
        TextView tvName = view.findViewById(R.id.tv_sensor_detail_name);
        TextView tvStatus = view.findViewById(R.id.tv_sensor_detail_status);
        TextView tvLastMeasurement = view.findViewById(R.id.tv_last_measurement);
        TextView tvLastMeasurementDate = view.findViewById(R.id.tv_last_measurement_date);
        BarChart barChart = view.findViewById(R.id.bar_chart_sensor);

        // Configurar los datos iniciales
        tvName.setText("Sensor: " + sensor.getNombre());
        tvStatus.setText(sensor.isConexion() ? "Estado: Conectado" : "Estado: Desconectado");

        // Filtrar mediciones para mostrar solo las de hoy y actualizar la gráfica
        List<Medicion> medicionesDeHoy = filterTodayMeasurements(mediciones);
        updateBarChart(barChart, medicionesDeHoy, tvLastMeasurement, tvLastMeasurementDate);

        return view;
    }


    private List<Medicion> filterTodayMeasurements(List<Medicion> mediciones) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String today = dateFormat.format(new Date());

        List<Medicion> medicionesDeHoy = new ArrayList<>();
        for (Medicion medicion : mediciones) {
            String medicionDate = dateFormat.format(medicion.getFecha());
            if (medicionDate.equals(today)) {
                medicionesDeHoy.add(medicion);
            }
        }
        return medicionesDeHoy;
    }
    private void updateBarChart(BarChart barChart, List<Medicion> mediciones, TextView tvLastMeasurement, TextView tvLastMeasurementDate) {
        if (mediciones == null || mediciones.isEmpty()) {
            tvLastMeasurement.setText("Última Medición: N/A");
            tvLastMeasurementDate.setText("Fecha: N/A");
            return;
        }

        // Ordenar las mediciones por fecha (ascendente)
        mediciones.sort((m1, m2) -> m1.getFecha().compareTo(m2.getFecha()));

        // Obtener la última medición
        Medicion ultimaMedicion = mediciones.get(mediciones.size() - 1);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String fechaUltimaMedicion = dateFormat.format(ultimaMedicion.getFecha());

        // Actualizar los TextView con la última medición
        tvLastMeasurement.setText("Última Medición: " + ultimaMedicion.getValor());
        tvLastMeasurementDate.setText("Fecha: " + fechaUltimaMedicion);

        // Crear las entradas para la gráfica
        List<BarEntry> entries = new ArrayList<>();
        SimpleDateFormat hourFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        List<String> labels = new ArrayList<>();

        int entryIndex = 0;
        for (Medicion medicion : mediciones) {
            // Extraer la hora para las etiquetas
            String hora = hourFormat.format(medicion.getFecha());
            labels.add(hora);

            // Crear la entrada para la gráfica
            entries.add(new BarEntry(entryIndex, (float) medicion.getValor()));
            entryIndex++;
        }

        BarDataSet dataSet = new BarDataSet(entries, "Mediciones de Hoy");
        dataSet.setColor(Color.BLUE);
        dataSet.setValueTextColor(Color.BLACK);

        BarData barData = new BarData(dataSet);
        barData.setBarWidth(0.9f); // Ancho de las barras
        barChart.setData(barData);

        // Configuración del eje X para mostrar las etiquetas ordenadas
        barChart.getXAxis().setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                int intValue = (int) value;
                if (intValue >= 0 && intValue < labels.size()) {
                    return labels.get(intValue);
                }
                return "";
            }
        });

        barChart.getXAxis().setGranularity(1f);
        barChart.getXAxis().setGranularityEnabled(true);
        barChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        barChart.getXAxis().setDrawGridLines(false);

        barChart.getAxisLeft().setGranularity(1f);
        barChart.getAxisRight().setEnabled(false);

        barChart.getLegend().setEnabled(true);
        barChart.getDescription().setEnabled(false);

        // Refrescar la gráfica
        barChart.notifyDataSetChanged();
        barChart.invalidate();
    }



}
