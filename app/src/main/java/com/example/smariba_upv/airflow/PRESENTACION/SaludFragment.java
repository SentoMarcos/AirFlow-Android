// SaludFragment.java
package com.example.smariba_upv.airflow.PRESENTACION;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smariba_upv.airflow.POJO.ExposicionItem;
import com.example.smariba_upv.airflow.PRESENTACION.Helpers.CalendarAdapter;
import com.example.smariba_upv.airflow.PRESENTACION.Helpers.ExposicionAdapter;
import com.example.smariba_upv.airflow.R;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;

public class SaludFragment extends Fragment implements CalendarAdapter.OnItemListener {

    private TextView monthYearText;
    private RecyclerView calendarRecyclerView, exposicionRecyclerView;
    private LocalDate selectedDate;
    private Button previousMonth;
    private Button nextMonth;
    private HashMap<String, Integer> selectedDaysMap = new HashMap<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_salud, container, false);
        InitWidgets(view);
        initExposicionRecyclerView();
        selectedDate = LocalDate.now();

        // Initialize selectedDaysMap with the current date
        String currentMonthYearKey = selectedDate.getMonthValue() + "-" + selectedDate.getYear();
        selectedDaysMap.put(currentMonthYearKey, selectedDate.getDayOfMonth());

        setMonthView();
        previousMonth = view.findViewById(R.id.btnpreviousMonth);
        nextMonth = view.findViewById(R.id.btnnextMonth);
        previousMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                previousMonth(v);
            }
        });
        nextMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextMonth(v);
            }
        });
        return view;
    }

    private void InitWidgets(View view) {
        calendarRecyclerView = view.findViewById(R.id.rv_calendar);
        exposicionRecyclerView = view.findViewById(R.id.rv_expo);
        monthYearText = view.findViewById(R.id.tv_month);
        selectedDate = LocalDate.now();
    }
    private void initExposicionRecyclerView() {
        ArrayList<ExposicionItem> exposicionItems = new ArrayList<>();
        exposicionItems.add(new ExposicionItem("Exposición última semana", "media", "media"));
        exposicionItems.add(new ExposicionItem("Exposición último mes", "excelente", "excelente"));

        ExposicionAdapter adapter = new ExposicionAdapter(exposicionItems);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        exposicionRecyclerView.setLayoutManager(layoutManager);
        exposicionRecyclerView.setAdapter(adapter);
    }


    private void setMonthView() {
        monthYearText.setText(monthYearFromDate(selectedDate));
        ArrayList<String> daysInMonth = daysInMonthArray(selectedDate);

        // Log para depuración
        Log.d("SaludFragment", "Days in Month: " + daysInMonth.toString());

        CalendarAdapter calendarAdapter = new CalendarAdapter(daysInMonth, this, selectedDaysMap);
        calendarAdapter.updateDisplayedMonthYear(selectedDate.getMonthValue(), selectedDate.getYear()); // Actualizar mes y año
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(), 7);
        calendarRecyclerView.setLayoutManager(layoutManager);
        calendarRecyclerView.setAdapter(calendarAdapter);
    }

    private ArrayList<String> daysInMonthArray(LocalDate date) {
        ArrayList<String> daysInMonthArray = new ArrayList<>();
        YearMonth yearMonth = YearMonth.from(date);

        int daysInMonth = yearMonth.lengthOfMonth();
        LocalDate firstOfMonth = date.withDayOfMonth(1);
        int dayOfWeek = firstOfMonth.getDayOfWeek().getValue(); // 1 = Monday, 7 = Sunday

        // Ajustar para que el lunes sea el primer día (cambiar el comportamiento si necesitas un inicio diferente)
        int adjustedDayOfWeek = dayOfWeek % 7; // Cambia esto si quieres que Domingo sea el primer día

        // Agregar días vacíos iniciales
        for (int i = 0; i < adjustedDayOfWeek; i++) {
            daysInMonthArray.add(" ");
        }

        // Agregar los días del mes
        for (int i = 1; i <= daysInMonth; i++) {
            daysInMonthArray.add(String.valueOf(i));
        }

        return daysInMonthArray;
    }

    private String monthYearFromDate(LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM yyyy");
        return date.format(formatter);
    }

    public void previousMonth(View view) {
        selectedDate = selectedDate.minusMonths(1);
        setMonthView();
    }

    public void nextMonth(View view) {
        selectedDate = selectedDate.plusMonths(1);
        setMonthView();
    }

    @Override
    public void onItemClick(int position, String dayText) {
        if (!dayText.trim().isEmpty()) {
            String message = "Selected Date " + dayText + " " + monthYearFromDate(selectedDate);
            Log.d("SaludFragment", message);
        }
    }
}