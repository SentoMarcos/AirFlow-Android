package com.example.smariba_upv.airflow.PRESENTACION;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.smariba_upv.airflow.API.EnviarPeticionesUser;
import com.example.smariba_upv.airflow.POJO.Medicion;
import com.example.smariba_upv.airflow.LOGIC.MedicionUtils;
import com.example.smariba_upv.airflow.R;
import com.example.smariba_upv.airflow.Services.ArduinoGetterService;
import com.example.smariba_upv.airflow.Services.DistanceTrackingService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */

public class HomeFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private TextView tvLastMeasurement;
    private TextView tvAverage;
    private TextView tvExposure;
    private TextView tvDistanceCovered;
    private TextView tvDailyExposure;
    private TextView tvCurrentTime;

    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Inicializar los TextViews
        tvLastMeasurement = view.findViewById(R.id.tvLastMeasurement);
        tvAverage = view.findViewById(R.id.tvAverage);
        tvExposure = view.findViewById(R.id.tvExposure);
        tvDistanceCovered = view.findViewById(R.id.tvDistanceCovered);
        tvDailyExposure = view.findViewById(R.id.tvDailyExposure);
        tvCurrentTime = view.findViewById(R.id.tvCurrentTime);

        // Iniciar el servicio de distancia
        Intent intent = new Intent(requireContext(), DistanceTrackingService.class);
        requireContext().startService(intent);
        Intent intent2 = new Intent(requireContext(), ArduinoGetterService.class);
        requireContext().startService(intent2);

        // Configurar la actualización de la hora
        startClockUpdate();



        // Registrar el BroadcastReceiver para la distancia
        IntentFilter filter = new IntentFilter("ACTUALIZAR_DISTANCIA");
        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(distanceReceiver, filter);



        return view;
    }

    private final Handler clockHandler = new Handler();
    private final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());

    private final Runnable clockRunnable = new Runnable() {
        @Override
        public void run() {
            if (isAdded() && tvCurrentTime != null) {
                // Obtener la hora actual y formatearla
                String currentTime = timeFormat.format(new Date());
                tvCurrentTime.setText(getString(R.string.current_time, currentTime));
            } else {
                Log.w("HomeFragment", "Fragmento no está asociado al contexto. Deteniendo reloj.");
                return; // Salir del Runnable si el fragmento no está asociado
            }

            // Repetir cada segundo
            clockHandler.postDelayed(this, 1000);
        }
    };

    private void startClockUpdate() {
        clockHandler.post(clockRunnable);
    }

    private void stopClockUpdate() {
        clockHandler.removeCallbacks(clockRunnable);
    }

    @Override
    public void onResume() {
        super.onResume();
        startClockUpdate();
        EnviarPeticionesUser peticiones = new EnviarPeticionesUser(requireContext());
        peticiones.getMediciones(new Callback<List<Medicion>>() {
            @Override
            public void onResponse(Call<List<Medicion>> call, Response<List<Medicion>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Medicion> mediciones = response.body();

                    // Procesar datos reales
                    Medicion ultimaMedicion = MedicionUtils.obtenerUltimaMedicion(mediciones);
                    double media = MedicionUtils.calcularMediaValores(mediciones);
                    double exposicionTotal = MedicionUtils.calcularExposicionTotal(mediciones);

                    String nivelExposicion;
                    if (media > 100) {
                        nivelExposicion = "Peligrosa";
                    } else if (media > 50) {
                        nivelExposicion = "Moderada";
                    } else {
                        nivelExposicion = "Ninguna";
                    }

                    // Actualizar la interfaz de usuario
                    if (ultimaMedicion != null) {
                        tvLastMeasurement.setText(getString(R.string.last_measurement, String.format("%.2f", ultimaMedicion.getValor())));
                    }
                    tvAverage.setText(getString(R.string.average_value, media));
                    tvExposure.setText(getString(R.string.total_exposure, exposicionTotal));
                    tvDailyExposure.setText(getString(R.string.daily_exposure, nivelExposicion));
                    tvCurrentTime.setText(getString(R.string.current_time, new Date().toString()));
                } else {
                    Log.e("HomeFragment", "No se recibieron datos de mediciones.");
                }
            }

            @Override
            public void onFailure(Call<List<Medicion>> call, Throwable t) {
                Log.e("HomeFragment", "Error al obtener mediciones: ", t);
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(distanceReceiver);
        stopClockUpdate();
    }

    private final BroadcastReceiver distanceReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null && intent.hasExtra("totalDistance")) {
                double totalDistance = intent.getDoubleExtra("totalDistance", 0);

                // Log para depuración
                Log.d("HomeFragment", "Broadcast recibido. Distancia total: " + totalDistance);

                actualizarDistancia(totalDistance);
            } else {
                Log.e("HomeFragment", "Broadcast recibido sin datos.");
            }
        }
    };

    private void actualizarDistancia(double distance) {
        if (tvDistanceCovered != null) {
            tvDistanceCovered.setText(String.format("Distancia recorrida: %.2f m", distance));

            // Log para depuración
            Log.d("HomeFragment", "Interfaz actualizada con distancia: " + distance);
        } else {
            Log.e("HomeFragment", "El TextView tvDistanceCovered no está inicializado.");
        }
    }
}