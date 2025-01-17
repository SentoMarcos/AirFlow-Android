package com.example.smariba_upv.airflow.PRESENTACION;

import static java.lang.String.valueOf;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.smariba_upv.airflow.API.EnviarPeticionesUser;
import com.example.smariba_upv.airflow.POJO.Medicion;
import com.example.smariba_upv.airflow.LOGIC.MedicionUtils;
import com.example.smariba_upv.airflow.R;
import com.example.smariba_upv.airflow.Services.ArduinoGetterService;
import com.example.smariba_upv.airflow.Services.DistanceTrackingService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment implements SensorEventListener {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private TextView tvDistanceCovered;
    private TextView tvDailyExposure;
    private TextView tvDistancePasos;
    private VideoView videoView;
    private ImageView iconoExposicion;

    private SensorManager sensorManager;
    private Sensor stepCounterSensor;
    private boolean isSensorRegistered = false;
    private int totalSteps = 0;
    private int stepsAtReset = 0;

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

        // Inicializar los TextViews y otros elementos de la UI
        tvDistanceCovered = view.findViewById(R.id.tvDistanceCovered);
        tvDailyExposure = view.findViewById(R.id.tvDailyExposure);
        tvDistancePasos = view.findViewById(R.id.tvDistancePasos);
        videoView = view.findViewById(R.id.videoView2);
        iconoExposicion = view.findViewById(R.id.Iconestado);

        // Inicializar SensorManager
        sensorManager = (SensorManager) requireContext().getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            stepCounterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        } else {
            Log.e("HomeFragment", "SensorManager no disponible.");
        }

        // Iniciar los servicios
        Intent intent = new Intent(requireContext(), DistanceTrackingService.class);
        requireContext().startService(intent);
        Intent intent2 = new Intent(requireContext(), ArduinoGetterService.class);
        requireContext().startService(intent2);

        // Registrar el BroadcastReceiver para la distancia
        IntentFilter filter = new IntentFilter("ACTUALIZAR_DISTANCIA");
        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(distanceReceiver, filter);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        // Registrar el sensor de pasos
        if (stepCounterSensor != null && !isSensorRegistered) {
            sensorManager.registerListener(this, stepCounterSensor, SensorManager.SENSOR_DELAY_UI);
            isSensorRegistered = true;
            Log.d("HomeFragment", "Sensor de pasos registrado.");
        } else if (stepCounterSensor == null) {
            Log.e("HomeFragment", "Sensor de pasos no disponible.");
        }

        // Obtener mediciones
        EnviarPeticionesUser peticiones = new EnviarPeticionesUser(requireContext());
        peticiones.getMediciones(new Callback<List<Medicion>>() {
            @Override
            public void onResponse(Call<List<Medicion>> call, Response<List<Medicion>> response) {
                if (isAdded() && response.isSuccessful() && response.body() != null) {
                    List<Medicion> mediciones = response.body();

                    double media = MedicionUtils.calcularMediaValores(mediciones);

                    String nivelExposicion;
                    int videoResId;

                    if (media < 50) {
                        iconoExposicion.setImageResource(R.drawable.salud_excelente);
                        nivelExposicion = "Excelente";
                        videoResId = R.raw.excelente;
                    } else if (media < 100) {
                        iconoExposicion.setImageResource(R.drawable.salud_buena);
                        nivelExposicion = "Bien";
                        videoResId = R.raw.bien;
                    } else if (media < 150) {
                        iconoExposicion.setImageResource(R.drawable.salud_media);
                        nivelExposicion = "Medio";
                        videoResId = R.raw.medio;
                    } else if (media < 200) {
                        iconoExposicion.setImageResource(R.drawable.salud_mala);
                        nivelExposicion = "Malo";
                        videoResId = R.raw.malo;
                    } else {
                        iconoExposicion.setImageResource(R.drawable.salud_peligrosa);
                        nivelExposicion = "Peligroso";
                        videoResId = R.raw.peligroso;
                    }

                    videoView.setVideoPath("android.resource://" + requireContext().getPackageName() + "/" + videoResId);
                    videoView.start();
                    videoView.setOnCompletionListener(mp -> videoView.start());

                    tvDailyExposure.setText(nivelExposicion);
                } else {
                    Log.e("HomeFragment", "Fragment no asociado o no se recibieron datos de mediciones.");
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
        // Desregistrar el sensor de pasos
        if (isSensorRegistered) {
            sensorManager.unregisterListener(this);
            isSensorRegistered = false;
            Log.d("HomeFragment", "Sensor de pasos desregistrado.");
        }

        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(distanceReceiver);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
            if (stepsAtReset == 0) {
                stepsAtReset = (int) event.values[0];
            }

            totalSteps = (int) event.values[0] - stepsAtReset;

            actualizarPasos(totalSteps);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // No es necesario manejar esto
    }

    private final BroadcastReceiver distanceReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null && intent.hasExtra("totalDistance")) {
                double totalDistance = intent.getDoubleExtra("totalDistance", 0);
                actualizarDistancia(totalDistance);
            }
        }
    };

    private void actualizarDistancia(double distance) {
        if (tvDistanceCovered != null) {
            tvDistanceCovered.setText(String.format("%.2f", distance));
        }
    }

    private void actualizarPasos(int steps) {
        if (tvDistancePasos != null) {
            tvDistancePasos.setText(String.valueOf(steps));
        }
    }
}