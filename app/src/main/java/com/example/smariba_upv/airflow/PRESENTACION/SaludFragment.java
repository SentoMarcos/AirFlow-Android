package com.example.smariba_upv.airflow.PRESENTACION;

import android.animation.ValueAnimator;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smariba_upv.airflow.API.EnviarPeticionesUser;
import com.example.smariba_upv.airflow.API.MODELS.MedicionMedia;
import com.example.smariba_upv.airflow.POJO.ExposicionItem;
import com.example.smariba_upv.airflow.POJO.ItemNotisSalud;
import com.example.smariba_upv.airflow.POJO.Medicion;
import com.example.smariba_upv.airflow.PRESENTACION.Helpers.CalendarAdapter;
import com.example.smariba_upv.airflow.PRESENTACION.Helpers.ExposicionAdapter;
import com.example.smariba_upv.airflow.PRESENTACION.Helpers.NotisAdapter;
import com.example.smariba_upv.airflow.R;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SaludFragment extends Fragment {

    private TextView monthYearText;
    private RecyclerView calendarRecyclerView, exposicionRecyclerView, notisRecyclerView;
    private LocalDate selectedDate;
    private Button previousMonth, nextMonth;
    private HashMap<String, Integer> selectedDaysMap = new HashMap<>();
    private EnviarPeticionesUser enviarPeticionesUser = new EnviarPeticionesUser();
    private Spinner spinnerFilter;
    private NotisAdapter adapter;
    private List<ItemNotisSalud> allNotisItems = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_salud, container, false);
        initWidgets(view);
        initRecyclerViews();
        setupSpinnerFilter();

        selectedDate = LocalDate.now();
        selectedDaysMap.put(generateMonthYearKey(selectedDate), selectedDate.getDayOfMonth());

        setMonthView();
        setupMonthNavigation(view);

        return view;
    }

    private void initWidgets(View view) {
        calendarRecyclerView = view.findViewById(R.id.rv_calendar);
        exposicionRecyclerView = view.findViewById(R.id.rv_expo);
        notisRecyclerView = view.findViewById(R.id.rv_notisSalud);
        spinnerFilter = view.findViewById(R.id.spinnerFilter);
        monthYearText = view.findViewById(R.id.tv_month);
    }

    private void initRecyclerViews() {
        initNotisRecyclerView();
        initExposicionRecyclerView();
    }

    private void setupSpinnerFilter() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                getContext(),
                R.array.filter_options,
                R.layout.spinner_item);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinnerFilter.setAdapter(adapter);

        spinnerFilter.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                animateRecyclerViewMargin(notisRecyclerView, 0, 500);
            }
            return false;
        });

        spinnerFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedFilter = parent.getItemAtPosition(position).toString();
                applyFilter(selectedFilter);
                animateRecyclerViewMargin(notisRecyclerView, 500, 0);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void animateRecyclerViewMargin(RecyclerView recyclerView, int fromMargin, int toMargin) {
        ValueAnimator animator = ValueAnimator.ofInt(fromMargin, toMargin);
        animator.setDuration(300);
        animator.addUpdateListener(animation -> {
            int value = (int) animation.getAnimatedValue();
            ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) recyclerView.getLayoutParams();
            if (layoutParams.topMargin != value) {
                layoutParams.topMargin = value;
                recyclerView.setLayoutParams(layoutParams);
            }
        });
        animator.start();
    }

    private void initNotisRecyclerView() {
        enviarPeticionesUser.getAllMedicionesUsuario(2, new Callback<List<Medicion>>() {
            @Override
            public void onResponse(Call<List<Medicion>> call, Response<List<Medicion>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    allNotisItems = response.body().stream()
                            .map(medicion -> new ItemNotisSalud(
                                    Medicion.formatFecha(medicion.getFecha()),
                                    generateMessage(medicion),
                                    classify(medicion.getValor())))
                            .collect(Collectors.toList());

                    adapter = new NotisAdapter(getContext(), allNotisItems, notisRecyclerView);
                    notisRecyclerView.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<List<Medicion>> call, Throwable t) {
                Log.e("SaludFragment", "Failed to fetch data", t);
            }
        });
    }

    private void applyFilter(String filter) {
        LocalDate now = LocalDate.now();
        List<ItemNotisSalud> filteredItems = allNotisItems.stream()
                .filter(item -> isValidForFilter(item, filter, now))
                .collect(Collectors.toList());
        adapter.updateData(filteredItems);
    }

    private boolean isValidForFilter(ItemNotisSalud item, String filter, LocalDate referenceDate) {
        LocalDate itemDate = LocalDate.parse(item.getTime().substring(0, 10));
        switch (filter) {
            case "Hoy": return itemDate.isEqual(referenceDate);
            case "Ayer": return itemDate.isEqual(referenceDate.minusDays(1));
            case "Semana": return !itemDate.isBefore(referenceDate.minusWeeks(1));
            case "Mes": return !itemDate.isBefore(referenceDate.minusMonths(1));
            case "Año": return !itemDate.isBefore(referenceDate.minusYears(1));
            default: return true;
        }
    }

    private void initExposicionRecyclerView() {
        enviarPeticionesUser.getMediaMedicionesUsuario(2, new Callback<List<MedicionMedia>>() {
            @Override
            public void onResponse(Call<List<MedicionMedia>> call, Response<List<MedicionMedia>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<ExposicionItem> exposicionItems = generateExposicionItems(response.body());
                    ExposicionAdapter exposicionAdapter = new ExposicionAdapter(exposicionItems);
                    exposicionRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
                    exposicionRecyclerView.setAdapter(exposicionAdapter);
                }
            }

            @Override
            public void onFailure(Call<List<MedicionMedia>> call, Throwable t) {
                Log.e("SaludFragment", "Failed to fetch exposicion data", t);
            }
        });
    }

    private List<ExposicionItem> generateExposicionItems(List<MedicionMedia> mediciones) {
        return List.of(
                createExposicionItem("Diaria", mediciones, Period.DAILY),
                createExposicionItem("Semanal", mediciones, Period.WEEKLY),
                createExposicionItem("Mensual", mediciones, Period.MONTHLY),
                createExposicionItem("Anual", mediciones, Period.YEARLY),
                createExposicionItem("Total", mediciones, Period.TOTAL)
        );
    }

    private ExposicionItem createExposicionItem(String title, List<MedicionMedia> mediciones, Period period) {
        double avg = calculateAverage(mediciones, period);
        return new ExposicionItem(title, String.valueOf(avg), classify(avg));
    }

    private double calculateAverage(List<MedicionMedia> mediciones, Period period) {
        LocalDate now = LocalDate.now();
        return mediciones.stream()
                .filter(m -> isValidForPeriod(m, period, now))
                .mapToDouble(MedicionMedia::getValorPromedio)
                .average()
                .orElse(0.0);
    }

    private boolean isValidForPeriod(MedicionMedia medicion, Period period, LocalDate referenceDate) {
        LocalDate date = LocalDate.parse(medicion.getFecha());
        switch (period) {
            case DAILY: return date.isEqual(referenceDate);
            case WEEKLY: return !date.isBefore(referenceDate.minusWeeks(1));
            case MONTHLY: return !date.isBefore(referenceDate.minusMonths(1));
            case YEARLY: return !date.isBefore(referenceDate.minusYears(1));
            case TOTAL: return true;
            default: return false;
        }
    }

    private void setupMonthNavigation(View view) {
        previousMonth = view.findViewById(R.id.btnpreviousMonth);
        nextMonth = view.findViewById(R.id.btnnextMonth);

        previousMonth.setOnClickListener(v -> {
            selectedDate = selectedDate.minusMonths(1);
            setMonthView();
        });

        nextMonth.setOnClickListener(v -> {
            selectedDate = selectedDate.plusMonths(1);
            setMonthView();
        });
    }

    private void setMonthView() {
        monthYearText.setText(monthYearFromDate(selectedDate));
        ArrayList<String> daysInMonth = daysInMonthArray(selectedDate);

        CalendarAdapter calendarAdapter = new CalendarAdapter(daysInMonth, new CalendarAdapter.OnItemListener() {
            @Override
            public void onItemClick(int position, String dayText) {
                // Implement the click behavior here
            }
        }, selectedDaysMap, new ArrayList<>());
        calendarRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 7));
        calendarRecyclerView.setAdapter(calendarAdapter);
    }

    private ArrayList<String> daysInMonthArray(LocalDate date) {
        ArrayList<String> daysInMonthArray = new ArrayList<>();
        YearMonth yearMonth = YearMonth.from(date);
        int daysInMonth = yearMonth.lengthOfMonth();
        LocalDate firstOfMonth = date.withDayOfMonth(1);
        int dayOfWeek = firstOfMonth.getDayOfWeek().getValue();

        for (int i = 0; i < dayOfWeek % 7; i++) {
            daysInMonthArray.add(" ");
        }

        for (int i = 1; i <= daysInMonth; i++) {
            daysInMonthArray.add(String.valueOf(i));
        }

        return daysInMonthArray;
    }

    private String monthYearFromDate(LocalDate date) {
        return date.format(DateTimeFormatter.ofPattern("MMMM yyyy"));
    }

    private String generateMonthYearKey(LocalDate date) {
        return date.getMonthValue() + "-" + date.getYear();
    }

    private String generateMessage(Medicion medicion) {
        return "La calidad del aire es " + classify(medicion.getValor()) + ". Valor: " + medicion.getValor();
    }

    private String classify(double valor) {
        if (valor < 50) return "excelente";
        if (valor < 100) return "buena";
        if (valor < 150) return "media";
        if (valor < 200) return "mala";
        return "peligroso";
    }

    private String classify(String valor) {
        try {
            double val = Double.parseDouble(valor.replace(",", "."));
            return classify(val);
        } catch (NumberFormatException e) {
            return "Sin datos";
        }
    }

    private enum Period {
        DAILY, WEEKLY, MONTHLY, YEARLY, TOTAL
    }
}