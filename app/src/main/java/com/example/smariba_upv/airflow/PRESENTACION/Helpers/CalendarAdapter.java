package com.example.smariba_upv.airflow.PRESENTACION.Helpers;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smariba_upv.airflow.API.MODELS.MedicionMedia;
import com.example.smariba_upv.airflow.R;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CalendarAdapter extends RecyclerView.Adapter<CalendarViewHolder> {
    private final ArrayList<String> daysOfMonth;
    private final OnItemListener onItemListener;
    private final HashMap<String, Integer> selectedDaysMap;
    private final List<MedicionMedia> medicionesMedia;
    // Variables para rastrear el mes y año actual
    private int displayedMonth;
    private int displayedYear;

    public CalendarAdapter(ArrayList<String> daysOfMonth, OnItemListener onItemListener, HashMap<String, Integer> selectedDaysMap, List<MedicionMedia> medicionesMedia) {
        this.daysOfMonth = daysOfMonth;
        this.onItemListener = onItemListener;
        this.selectedDaysMap = selectedDaysMap;
        this.medicionesMedia = medicionesMedia;
    }

    @Override
    public CalendarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.calendar_cell, parent, false);

        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();

        // Obtener altura de la pantalla
        int screenHeight = parent.getContext().getResources().getDisplayMetrics().heightPixels;

        // Asignar altura para cada celda (aproximadamente 1/6 del RecyclerView o pantalla)
        layoutParams.height = (screenHeight - 1000) / 6; // Ajusta 200 según márgenes/botones
        view.setLayoutParams(layoutParams);

        return new CalendarViewHolder(view, onItemListener);
    }

    @Override
    public void onBindViewHolder(@NonNull CalendarViewHolder holder, int position) {
        String dayText = daysOfMonth.get(position);
        holder.dayOfMonth.setText(dayText);

        // Clave única para el mes y año actual
        String currentMonthYearKey = displayedMonth + "-" + displayedYear;

        if (!dayText.trim().isEmpty()) {
            int day = Integer.parseInt(dayText);
            String formattedDate = String.format("%04d-%02d-%02d", displayedYear, displayedMonth, day);


            // Determinar si este día debe estar seleccionado
            boolean isSelected = selectedDaysMap.containsKey(currentMonthYearKey) &&
                    selectedDaysMap.get(currentMonthYearKey) == day;

            // Asignar color de fondo basado en lógica
            MedicionMedia medicion = findMedicionByDate(formattedDate);
            int backgroundColor;

            if (medicion != null) {
                double valorPromedio = medicion.getValorPromedio();
                if (valorPromedio < 50) {
                    backgroundColor = R.color.RosaExcelente;
                } else if (valorPromedio < 100) {
                    backgroundColor = R.color.VerdeBueno;
                } else if (valorPromedio < 150) {
                    backgroundColor = R.color.AmarilloMedio;
                } else {
                    backgroundColor = R.color.NaranjaMalo;
                }
            } else {
                backgroundColor = R.color.GrisClaro; // Sin medición
            }

            // Aplicar el fondo redondeado
            holder.itemView.findViewById(R.id.bg_color).setBackground(
                    isSelected ? getNeonBorderDrawable(holder.itemView.getContext(), backgroundColor)
                            : getRoundedDrawable(holder.itemView.getContext(), backgroundColor)
            );

            // Manejar selección del día
            holder.itemView.setOnClickListener(v -> {
                // Limpiar el mapa de días seleccionados antes de agregar el nuevo día
                selectedDaysMap.clear();
                // Guardar el día seleccionado en el mapa para este mes y año
                selectedDaysMap.put(currentMonthYearKey, day);
                notifyDataSetChanged(); // Refrescar la vista para mostrar la selección
            });
        } else {
            // Celdas vacías
            holder.itemView.findViewById(R.id.bg_color).setBackground(
                    getRoundedDrawable(holder.itemView.getContext(), R.color.GrisClaro)
            );
        }
    }

    private MedicionMedia findMedicionByDate(String date) {
        if (medicionesMedia != null) {
            for (MedicionMedia medicion : medicionesMedia) {
                if (medicion.getFecha().equals(date)) {
                    return medicion;
                }
            }
        }
        return null;
    }

    // Método para actualizar mes y año
    public void updateDisplayedMonthYear(int month, int year) {
        this.displayedMonth = month;
        this.displayedYear = year;

        // Refrescar la vista al cambiar de mes/año
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return daysOfMonth.size();
    }

    // Método para crear un drawable redondeado dinámicamente
    private Drawable getRoundedDrawable(Context context, int backgroundColor) {
        GradientDrawable rounded = new GradientDrawable();
        rounded.setColor(ContextCompat.getColor(context, backgroundColor));
        rounded.setCornerRadii(new float[]{12f, 12f, 12f, 12f, 20f, 20f, 20f, 20f});
        return rounded;
    }

    // Método para crear un drawable con borde de neón dinámico
    private Drawable getNeonBorderDrawable(Context context, int backgroundColor) {
        GradientDrawable border = new GradientDrawable();
        border.setColor(ContextCompat.getColor(context, backgroundColor));
        border.setCornerRadii(new float[]{12f, 12f, 12f, 12f, 20f, 20f, 20f, 20f});
        border.setStroke(4, ContextCompat.getColor(context, R.color.Blanco)); // Borde blanco tipo neón
        return border;
    }

    public interface OnItemListener {
        void onItemClick(int position, String dayText);
    }
}