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

import com.example.smariba_upv.airflow.R;

import java.time.LocalDate;
import java.util.ArrayList;

public class CalendarAdapter extends RecyclerView.Adapter<CalendarViewHolder> {
    private final ArrayList<String> daysOfMonth;
    private final OnItemListener onItemListener;


    public CalendarAdapter(ArrayList<String> daysOfMonth, OnItemListener onItemListener) {
        this.daysOfMonth = daysOfMonth;
        this.onItemListener = onItemListener;
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

// Updated code for applying colors dynamically based on measurements and logical rules

    // Variable to track the selected day
    private int selectedDay = LocalDate.now().getDayOfMonth();

    @Override
    public void onBindViewHolder(@NonNull CalendarViewHolder holder, int position) {
        String dayText = daysOfMonth.get(position);
        holder.dayOfMonth.setText(dayText);

        if (!dayText.trim().isEmpty()) {
            int day = Integer.parseInt(dayText);

            // Get the current date to compare with days in the calendar
            LocalDate currentDate = LocalDate.now();
            int currentDay = currentDate.getDayOfMonth();
            int currentMonth = currentDate.getMonthValue();
            int currentYear = currentDate.getYear();

            // Variable to store the background color for the current day
            int backgroundColor;

            // Logic for applying colors based on whether the day is valid and has measurements
            if ((displayedYear < currentYear) ||
                    (displayedYear == currentYear && displayedMonth < currentMonth) ||
                    (displayedYear == currentYear && displayedMonth == currentMonth && day <= currentDay)) {
                // Past days, including today, have measurements
                if (day % 5 == 0) {
                    backgroundColor = R.color.RosaExcelente;
                } else if (day % 3 == 0) {
                    backgroundColor = R.color.VerdeBueno;
                } else if (day % 2 == 0) {
                    backgroundColor = R.color.AmarilloMedio;
                } else {
                    backgroundColor = R.color.NaranjaMalo;
                }
            } else {
                // Future days have no measurements
                backgroundColor = R.color.GrisClaro;
            }

            // Apply the background color with rounded corners
            holder.itemView.findViewById(R.id.bg_color).setBackground(getRoundedDrawable(holder.itemView.getContext(), backgroundColor));

            // Highlight the selected day with a neon border, or the current day by default
            if (day == selectedDay) {
                holder.itemView.findViewById(R.id.bg_color).setBackground(getNeonBorderDrawable(holder.itemView.getContext(), backgroundColor));
            }

            // Handle day selection
            holder.itemView.setOnClickListener(v -> {
                selectedDay = day;
                notifyDataSetChanged(); // Refresh the adapter to update the selection
            });
        } else {
            // Empty or invalid days always have the default background color
            holder.itemView.findViewById(R.id.bg_color).setBackground(getRoundedDrawable(holder.itemView.getContext(), R.color.GrisClaro));
        }
    }

    // Method to create a rounded drawable dynamically
    private Drawable getRoundedDrawable(Context context, int backgroundColor) {
        GradientDrawable rounded = new GradientDrawable();
        rounded.setColor(ContextCompat.getColor(context, backgroundColor)); // Set the background color
        rounded.setCornerRadii(new float[]{12f, 12f, 12f, 12f, 20f, 20f, 20f, 20f}); // Apply rounded corners
        return rounded;
    }

    // Method to create a neon border drawable dynamically
    private Drawable getNeonBorderDrawable(Context context, int backgroundColor) {
        GradientDrawable border = new GradientDrawable();
        border.setColor(ContextCompat.getColor(context, backgroundColor)); // Set the background color
        border.setCornerRadii(new float[]{12f, 12f, 12f, 12f, 20f, 20f, 20f, 20f}); // Corner radius for the border
        border.setStroke(4, ContextCompat.getColor(context, R.color.Blanco)); // White neon-like border
        return border;
    }

    // Variables to track displayed month and year
    private int displayedMonth;
    private int displayedYear;

    // Ensure displayedMonth and displayedYear are updated correctly when the calendar changes
    public void updateDisplayedMonthYear(int month, int year) {
        this.displayedMonth = month;
        this.displayedYear = year;
    }




    @Override
    public int getItemCount() {
        return daysOfMonth.size();
    }

    public interface OnItemListener {
        void onItemClick(int position, String dayText);
    }
}
