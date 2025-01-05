package com.example.smariba_upv.airflow.PRESENTACION;

import static com.example.smariba_upv.airflow.LOGIC.CalculationUtils.obtenerClasificacion;

import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
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
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SaludFragment extends Fragment implements CalendarAdapter.OnItemListener {

    private TextView monthYearText;
    private RecyclerView calendarRecyclerView, exposicionRecyclerView, notisRecyclerView;
    private LocalDate selectedDate;
    private Button previousMonth, nextMonth;
    private HashMap<String, Integer> selectedDaysMap = new HashMap<>();
    private EnviarPeticionesUser enviarPeticionesUser = new EnviarPeticionesUser();
    private Spinner spinnerFilter;
    private NotisAdapter adapter;
    private List<ItemNotisSalud> allNotisItems = new ArrayList<>();
    private List<ItemNotisSalud> filteredNotisItems = new ArrayList<>();
    private ImageView ivShowChart;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_salud, container, false);
        initWidgets(view);
        initRecyclerViews();
        setupSpinnerFilter();

        selectedDate = LocalDate.now();
        selectedDaysMap.put(generateMonthYearKey(selectedDate), selectedDate.getDayOfMonth());
        ivShowChart.setOnClickListener(v -> {
            String selectedDateString = selectedDate.toString();
            Log.d("SaludFragment", "Ícono presionado. Fecha seleccionada: " + selectedDateString);
            loadAndShowGraphForDate(selectedDateString);
        });




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
        ivShowChart = view.findViewById(R.id.iv_more_info);
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
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                    DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss 'GMT'XXX yyyy", Locale.ENGLISH);
                    Log.d("SaludFragment", "Received data: " + response.body());
                    // Almacenar los datos originales
                    allNotisItems = response.body().stream()
                            .map(medicion -> new ItemNotisSalud(
                                    LocalDate.parse(medicion.getFecha().toString(), inputFormatter).format(formatter),
                                    generateMessage(medicion),
                                    obtenerClasificacion(String.valueOf(medicion.getValor()))
                            ))
                            .collect(Collectors.toList());

                    // Inicializar el adaptador con todos los datos
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
        if (allNotisItems == null || allNotisItems.isEmpty()) return;

        LocalDate now = LocalDate.now();
        List<ItemNotisSalud> newFilteredItems;

        switch (filter) {
            case "Hoy":
                newFilteredItems = filterByDate(now);
                break;
            case "Ayer":
                newFilteredItems = filterByDate(now.minusDays(1));
                break;
            case "Semana":
                newFilteredItems = filterByRange(now.minusWeeks(1), now);
                break;
            case "Mes":
                newFilteredItems = filterByRange(now.minusMonths(1), now);
                break;
            case "Año":
                newFilteredItems = filterByRange(now.minusYears(1), now);
                break;
            case "Todos":
            default:
                newFilteredItems = new ArrayList<>(allNotisItems);
        }

        // Solo actualizamos el adaptador si hay cambios en los datos filtrados
        if (!filteredNotisItems.equals(newFilteredItems)) {
            filteredNotisItems = Collections.unmodifiableList(newFilteredItems);
            adapter.updateData(filteredNotisItems);
            Log.d("Filter", "Filter applied: " + filter + ", Items: " + filteredNotisItems.size());
        } else {
            Log.d("Filter", "No changes detected for filter: " + filter);
        }
    }

    private List<ItemNotisSalud> filterByDate(LocalDate date) {
        return Collections.unmodifiableList(
                allNotisItems.stream()
                        .filter(item -> {
                            LocalDate itemDate = parseItemDate(item.getTime());
                            return itemDate != null && itemDate.isEqual(date);
                        })
                        .collect(Collectors.toList())
        );
    }

    private List<ItemNotisSalud> filterByRange(LocalDate startDate, LocalDate endDate) {
        return Collections.unmodifiableList(
                allNotisItems.stream()
                        .filter(item -> {
                            LocalDate itemDate = parseItemDate(item.getTime());
                            return itemDate != null && !itemDate.isBefore(startDate) && !itemDate.isAfter(endDate);
                        })
                        .collect(Collectors.toList())
        );
    }

    private LocalDate parseItemDate(String time) {
        try {
            return LocalDate.parse(time.substring(0, 10)); // Ajusta según el formato
        } catch (Exception e) {
            Log.e("ParseDate", "Error parsing date: " + time, e);
            return null;
        }
    }


    private void updateAirQualityDisplay(String state, String formattedDate) {
        // Obtener referencias a los elementos de diseño
        TextView airQualityLabel = getView().findViewById(R.id.tv_air_quality_label);
        //si es el dia actual poener Calidad del aire de hoy si no poner Calidad del aire de la fecha
        if (formattedDate.equals(LocalDate.now().toString())) {
            airQualityLabel.setText("Calidad del aire de hoy");
        } else {
            airQualityLabel.setText("Calidad del aire de:\n " + formattedDate);
        }



        TextView airQualityStatus = getView().findViewById(R.id.tv_air_quality_status);
        ImageView airQualityFace = getView().findViewById(R.id.iv_air_quality_face);
        Log.d("SaludFragment", "Received state: " + state);
        // Configurar textos y colores según el estado
        switch (state.toLowerCase()) {
            case "excelente":
                airQualityStatus.setText("Excelente");
                airQualityStatus.setTextColor(getResources().getColor(R.color.RosaExcelente));
                airQualityFace.setImageResource(R.drawable.landing_excelente);
                break;
            case "buena":
                airQualityStatus.setText("Buena");
                airQualityStatus.setTextColor(getResources().getColor(R.color.VerdeBueno));
                airQualityFace.setImageResource(R.drawable.landing_bueno);
                break;
            case "media":
                airQualityStatus.setText("Media");
                airQualityStatus.setTextColor(getResources().getColor(R.color.AmarilloMedio));
                airQualityFace.setImageResource(R.drawable.landing_media);
                break;
            case "mala":
                airQualityStatus.setText("Mala");
                airQualityStatus.setTextColor(getResources().getColor(R.color.NaranjaMalo));
                airQualityFace.setImageResource(R.drawable.landing_malo);
                break;
            case "peligroso":
                airQualityStatus.setText("Peligroso");
                airQualityStatus.setTextColor(getResources().getColor(R.color.RojoPeligroso));
                airQualityFace.setImageResource(R.drawable.landing_peligroso);
                break;
            default:
                airQualityStatus.setText("Sin datos");
                airQualityStatus.setTextColor(getResources().getColor(R.color.GrisClaro));
                airQualityFace.setImageResource(R.drawable.edittext_border);
                break;
        }
    }



    private void initExposicionRecyclerView() {
        enviarPeticionesUser.getMediaMedicionesUsuario(2, new Callback<List<MedicionMedia>>() {
            @Override
            public void onResponse(Call<List<MedicionMedia>> call, Response<List<MedicionMedia>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<MedicionMedia> mediciones = response.body();
                    List<ExposicionItem> exposicionItems = generateExposicionItems(mediciones);
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
        enviarPeticionesUser.getMediaMedicionesUsuario(2, new Callback<List<MedicionMedia>>() {
            @Override
            public void onResponse(Call<List<MedicionMedia>> call, Response<List<MedicionMedia>> response) {
                if (response.isSuccessful()) {
                    List<MedicionMedia> medicionesMedia = response.body();

                    monthYearText.setText(monthYearFromDate(selectedDate));
                    ArrayList<String> daysInMonth = daysInMonthArray(selectedDate);

                    CalendarAdapter calendarAdapter = new CalendarAdapter(daysInMonth, SaludFragment.this, selectedDaysMap, medicionesMedia);
                    RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(), 7);
                    calendarRecyclerView.setLayoutManager(layoutManager);
                    calendarRecyclerView.setAdapter(calendarAdapter);
                    calendarAdapter.updateDisplayedMonthYear(selectedDate.getMonthValue(), selectedDate.getYear());
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
        if (valor < 0) return "Sin datos";
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

    @Override
    public void onItemClick(int position, String dayText) {
        if (!dayText.trim().isEmpty()) {
            int selectedDay = Integer.parseInt(dayText);
            selectedDate = LocalDate.of(selectedDate.getYear(), selectedDate.getMonth(), selectedDay);
            String formattedDate = String.format("%04d-%02d-%02d", selectedDate.getYear(), selectedDate.getMonthValue(), selectedDay);

            // Buscar el dato correspondiente a la fecha seleccionada en el calendario
            CalendarAdapter adapter = (CalendarAdapter) calendarRecyclerView.getAdapter();
            double valorPromedio = adapter.getValorPromedio(position);

            String estado = classify(valorPromedio);
            updateAirQualityDisplay(estado,formattedDate);
        }
    }


    private enum Period {
        DAILY, WEEKLY, MONTHLY, YEARLY, TOTAL
    }

    private void loadAndShowGraphForDate(String date) {
        Log.d("SaludFragment", "Buscando datos para la fecha seleccionada: " + date);

        enviarPeticionesUser.getAllMedicionesUsuario(2, new Callback<List<Medicion>>() {
            @Override
            public void onResponse(Call<List<Medicion>> call, Response<List<Medicion>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Medicion> mediciones = response.body();
                    ArrayList<BarEntry> entries = new ArrayList<>();
                    ArrayList<Integer> colors = new ArrayList<>();

                    // Mapa para agrupar los valores por hora
                    HashMap<Integer, Float> hourlyValues = new HashMap<>();

                    // Formateador para convertir Date a String
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    SimpleDateFormat hourFormat = new SimpleDateFormat("HH", Locale.getDefault());

                    // Filtrar las mediciones por la fecha seleccionada y extraer las horas
                    for (Medicion medicion : mediciones) {
                        String medicionFecha = dateFormat.format(medicion.getFecha()); // Convierte la fecha a String
                        if (medicionFecha.equals(date)) {
                            int hour = Integer.parseInt(hourFormat.format(medicion.getFecha())); // Extrae la hora como entero
                            float valor = (float) medicion.getValor();

                            // Acumular el valor promedio por hora (si es necesario)
                            if (hourlyValues.containsKey(hour)) {
                                hourlyValues.put(hour, (hourlyValues.get(hour) + valor) / 2);
                            } else {
                                hourlyValues.put(hour, valor);
                            }
                        }
                    }

                    // Crear entradas para la gráfica basadas en el mapa de horas
                    for (int hour : hourlyValues.keySet()) {
                        entries.add(new BarEntry(hour, hourlyValues.get(hour)));
                        colors.add(getColorForValue(hourlyValues.get(hour))); // Agregar color basado en el valor
                    }

                    if (!entries.isEmpty()) {
                        showGraphDialog(entries, colors, date);
                    } else {
                        Log.d("SaludFragment", "No hay datos para la fecha seleccionada: " + date);
                        showNoDataDialog(date);
                    }
                } else {
                    Log.e("SaludFragment", "Error al obtener los datos desde la API");
                    showErrorDialog();
                }
            }

            @Override
            public void onFailure(Call<List<Medicion>> call, Throwable t) {
                Log.e("SaludFragment", "Error al cargar datos para la gráfica", t);
                showErrorDialog();
            }
        });
    }

    private void showNoDataDialog(String date) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Sin datos disponibles")
                .setMessage("No hay datos registrados para la fecha seleccionada: " + date)
                .setPositiveButton("Aceptar", (dialog, which) -> dialog.dismiss())
                .show();
    }
    private void showErrorDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Error")
                .setMessage("Hubo un problema al obtener los datos. Por favor, intenta nuevamente.")
                .setPositiveButton("Aceptar", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void showGraphDialog(ArrayList<BarEntry> entries, ArrayList<Integer> colors, String date) {
        // Inflar el layout personalizado
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View dialogView = inflater.inflate(R.layout.dialog_fullscreen_chart, null);

        // Configurar el título
        TextView tvTitle = dialogView.findViewById(R.id.tv_graph_title);
        tvTitle.setText("Calidad del aire - " + date);

        // Configurar la gráfica
        BarChart barChart = dialogView.findViewById(R.id.bar_chart);
        BarDataSet dataSet = new BarDataSet(entries, "Valores horarios");
        dataSet.setColors(colors);
        dataSet.setValueTextColor(getResources().getColor(R.color.Blanco)); // Color de los valores
        dataSet.setValueTextSize(12f); // Tamaño del texto

        BarData barData = new BarData(dataSet);
        barData.setBarWidth(0.9f);
        barChart.setData(barData);

        barChart.getDescription().setEnabled(false);
        barChart.animateY(1500);
        barChart.setFitBars(true);

        // Configuración de ejes
        barChart.getXAxis().setGranularity(1f);
        barChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        barChart.getXAxis().setDrawGridLines(false);

        barChart.getAxisLeft().setDrawGridLines(false);
        barChart.getAxisRight().setEnabled(false);

        barChart.getLegend().setEnabled(false);

        // Configurar el TextView para mostrar la información de la barra seleccionada
        TextView tvSelectedBarInfo = dialogView.findViewById(R.id.tv_selected_bar_info);

        // Configurar el listener para la selección de barras
        barChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                int index = (int) e.getX();
                float value = e.getY();
                tvSelectedBarInfo.setText("Hora: " + index + ":00, Valor: " + value);
                tvSelectedBarInfo.setVisibility(View.VISIBLE);
            }

            @Override
            public void onNothingSelected() {
                tvSelectedBarInfo.setVisibility(View.GONE);
            }
        });

        // Crear el diálogo centrado en pantalla y ocupando casi todo el anchura y altura de la pantalla
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.CustomDialogStyle);
        builder.setView(dialogView);

        // Crear la instancia del diálogo
        AlertDialog dialog = builder.create();
        barChart.setMinimumHeight(300); // Set minimum height in pixels

        // Configurar el botón de cierre
        Button btnClose = dialogView.findViewById(R.id.btn_close_chart);
        btnClose.setOnClickListener(v -> dialog.dismiss());

        // Mostrar el diálogo
        dialog.show();
    }



    private String convertDateToString(Date date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().format(formatter);
    }
    private int getColorForValue(float value) {
        if (value < 50) {
            return getResources().getColor(R.color.RosaExcelente); // Excelente
        } else if (value < 100) {
            return getResources().getColor(R.color.VerdeBueno); // Buena
        } else if (value < 150) {
            return getResources().getColor(R.color.AmarilloMedio); // Media
        } else if (value < 200) {
            return getResources().getColor(R.color.NaranjaMalo); // Mala
        } else {
            return getResources().getColor(R.color.RojoPeligroso); // Peligroso
        }
    }




}