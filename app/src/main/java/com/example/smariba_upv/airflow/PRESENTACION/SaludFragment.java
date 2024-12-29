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

import com.example.smariba_upv.airflow.API.EnviarPeticionesUser;
import com.example.smariba_upv.airflow.API.MODELS.MedicionMedia;
import com.example.smariba_upv.airflow.POJO.ExposicionItem;
import com.example.smariba_upv.airflow.PRESENTACION.Helpers.CalendarAdapter;
import com.example.smariba_upv.airflow.PRESENTACION.Helpers.ExposicionAdapter;
import com.example.smariba_upv.airflow.R;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SaludFragment extends Fragment implements CalendarAdapter.OnItemListener {

    private TextView monthYearText;
    private RecyclerView calendarRecyclerView, exposicionRecyclerView;
    private LocalDate selectedDate;
    private Button previousMonth;
    private Button nextMonth;
    private HashMap<String, Integer> selectedDaysMap = new HashMap<>();
    private EnviarPeticionesUser enviarPeticionesUser = new EnviarPeticionesUser();

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


        // Llamada para obtener las mediciones
        enviarPeticionesUser.getMediaMedicionesUsuario(2, new Callback<List<MedicionMedia>>() {
            @Override
            public void onResponse(Call<List<MedicionMedia>> call, Response<List<MedicionMedia>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<MedicionMedia> mediciones = response.body();

                    // Calcular medias para cada periodo
                    exposicionItems.add(new ExposicionItem(
                            "Exposición diaria",
                            calcularMedia(mediciones, Periodo.DIA),
                            obtenerClasificacion(calcularMedia(mediciones, Periodo.DIA))
                    ));
                    exposicionItems.add(new ExposicionItem(
                            "Exposición semanal",
                            calcularMedia(mediciones, Periodo.SEMANA),
                            obtenerClasificacion(calcularMedia(mediciones, Periodo.SEMANA))
                    ));
                    exposicionItems.add(new ExposicionItem(
                            "Exposición mensual",
                            calcularMedia(mediciones, Periodo.MES),
                            obtenerClasificacion(calcularMedia(mediciones, Periodo.MES))
                    ));
                    exposicionItems.add(new ExposicionItem(
                            "Exposición anual",
                            calcularMedia(mediciones, Periodo.ANIO),
                            obtenerClasificacion(calcularMedia(mediciones, Periodo.ANIO))
                    ));
                    exposicionItems.add(new ExposicionItem(
                            "Exposición total",
                            calcularMedia(mediciones, Periodo.TOTAL),
                            obtenerClasificacion(calcularMedia(mediciones, Periodo.TOTAL))
                    ));

                    // Configurar el RecyclerView
                    ExposicionAdapter adapter = new ExposicionAdapter(exposicionItems);
                    LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
                    exposicionRecyclerView.setLayoutManager(layoutManager);
                    exposicionRecyclerView.setAdapter(adapter);
                } else {
                    Log.e("SaludFragment", "Error al obtener las mediciones: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<MedicionMedia>> call, Throwable t) {
                Log.e("SaludFragment", "Fallo al obtener las mediciones", t);
            }
        });
    }



    private void setMonthView() {
        enviarPeticionesUser.getMediaMedicionesUsuario(2, new Callback<List<MedicionMedia>>() {
            @Override
            public void onResponse(Call<List<MedicionMedia>> call, Response<List<MedicionMedia>> response) {
                if (response.isSuccessful()) {
                    List<MedicionMedia> medicionesMedia = response.body();

                    monthYearText.setText(monthYearFromDate(selectedDate));
                    ArrayList<String> daysInMonth = daysInMonthArray(selectedDate);

                    CalendarAdapter calendarAdapter = new CalendarAdapter(daysInMonth, SaludFragment.this, selectedDaysMap, medicionesMedia);
                    calendarAdapter.updateDisplayedMonthYear(selectedDate.getMonthValue(), selectedDate.getYear());
                    RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(), 7);
                    calendarRecyclerView.setLayoutManager(layoutManager);
                    calendarRecyclerView.setAdapter(calendarAdapter);
                }
            }

            @Override
            public void onFailure(Call<List<MedicionMedia>> call, Throwable t) {
                Log.e("SaludFragment", "Error al obtener mediciones", t);
            }
        });
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

    private String calcularMedia(List<MedicionMedia> mediciones, Periodo periodo) {
        LocalDate now = LocalDate.now();
        double suma = 0;
        int contador = 0;

        for (MedicionMedia medicion : mediciones) {
            LocalDate fecha = LocalDate.parse(medicion.getFecha());

            switch (periodo) {
                case DIA:
                    if (fecha.isEqual(now)) {
                        suma += medicion.getValorPromedio();
                        contador++;
                    }
                    break;
                case SEMANA:
                    if (!fecha.isBefore(now.minusWeeks(1))) {
                        suma += medicion.getValorPromedio();
                        contador++;
                    }
                    break;
                case MES:
                    if (!fecha.isBefore(now.minusMonths(1))) {
                        suma += medicion.getValorPromedio();
                        contador++;
                    }
                    break;
                case ANIO:
                    if (!fecha.isBefore(now.minusYears(1))) {
                        suma += medicion.getValorPromedio();
                        contador++;
                    }
                    break;
                case TOTAL:
                    suma += medicion.getValorPromedio();
                    contador++;
                    break;
            }
        }

        return contador > 0 ? String.format("%.2f", suma / contador) : "N/A";
    }
    private String obtenerClasificacion(String valor) {
        if (valor.equals("N/A")) return "Sin datos";

        // Reemplazar coma por punto para evitar errores de formato
        valor = valor.replace(",", ".");

        double valorPromedio = Double.parseDouble(valor);

        if (valorPromedio < 50) {
            return "excelente";
        } else if (valorPromedio < 100) {
            return "buena";
        } else if (valorPromedio < 150) {
            return "media";
        } else if (valorPromedio < 200) {
            return "mala";
        }
        else {
            return "Peligroso";
        }
    }

    private enum Periodo {
        DIA,
        SEMANA,
        MES,
        ANIO,
        TOTAL
    }


}