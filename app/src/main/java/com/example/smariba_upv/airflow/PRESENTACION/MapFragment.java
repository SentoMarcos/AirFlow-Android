package com.example.smariba_upv.airflow.PRESENTACION;

import android.Manifest;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.example.smariba_upv.airflow.API.EnviarPeticionesUser;
import com.example.smariba_upv.airflow.POJO.Medicion;
import com.example.smariba_upv.airflow.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.maps.android.heatmaps.Gradient;
import com.google.maps.android.heatmaps.HeatmapTileProvider;
import com.google.maps.android.heatmaps.WeightedLatLng;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap googleMap;
    private HeatmapTileProvider heatmapTileProvider;
    private CheckBox cbHeatMap;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_map, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Inicializa el mapa
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        // Inicializa el checkbox
        cbHeatMap = view.findViewById(R.id.cb_heat_map);
        cbHeatMap.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                clearMap(); // Limpiar el mapa antes de mostrar el mapa de calor
                fetchAndDisplayData(); // Mostrar el mapa de calor
            } else {
                clearMap(); // Limpiar el mapa
                applyDefaultMapStyle(); // Aplicar el estilo predeterminado
            }
        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap map) {
        this.googleMap = map;
        enableUserLocation();
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(40.416775, -3.703790), 6));
        applyDefaultMapStyle(); // Aplicar estilo normal por defecto
    }

    private void applyDefaultMapStyle() {
        try {
            boolean success = googleMap.setMapStyle(null); // Restaurar estilo predeterminado de Google Maps

            if (!success) {
                Log.e("MapFragment", "No se pudo aplicar el estilo predeterminado.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e("MapFragment", "Error al aplicar el estilo predeterminado.", e);
        }
    }

    private void fetchAndDisplayData() {
        EnviarPeticionesUser enviarPeticionesUser = new EnviarPeticionesUser();
        enviarPeticionesUser.getMediciones(new Callback<List<Medicion>>() {
            @Override
            public void onResponse(Call<List<Medicion>> call, Response<List<Medicion>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    displayDataOnMap(response.body());
                } else {
                    Toast.makeText(getContext(), "No se pudieron cargar los datos de calidad del aire.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Medicion>> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(getContext(), "Error al obtener datos.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayDataOnMap(List<Medicion> mediciones) {
        List<WeightedLatLng> weightedData = new ArrayList<>();

        for (Medicion medicion : mediciones) {
            double latitud = medicion.getLatitud();
            double longitud = medicion.getLongitud();
            double intensidad = normalizeValue(medicion.getValor());
            weightedData.add(new WeightedLatLng(new LatLng(latitud, longitud), intensidad));
        }

        // Crear proveedor de mapa de calor
        heatmapTileProvider = new HeatmapTileProvider.Builder()
                .weightedData(weightedData)
                .gradient(createGradient())
                .radius(50) // Ajustar radio para suavizar transiciones
                .opacity(0.6) // Opacidad ajustada
                .build();

        googleMap.addTileOverlay(new TileOverlayOptions().tileProvider(heatmapTileProvider));
    }

    private Gradient createGradient() {
        int[] colors = {
                Color.rgb(0, 255, 0),    // Verde (baja concentración)
                Color.rgb(255, 255, 0),  // Amarillo (moderada concentración)
                Color.rgb(255, 0, 0)     // Rojo (alta concentración)
        };

        float[] startPoints = {0.2f, 0.5f, 1.0f};
        return new Gradient(colors, startPoints);
    }

    private double normalizeValue(double valor) {
        double limiteInferior = 50; // Valor bajo (verde)
        double limiteSuperior = 100; // Valor moderado (amarillo)

        if (valor <= limiteInferior) {
            return 0.2; // Verde
        } else if (valor <= limiteSuperior) {
            return 0.5; // Amarillo
        } else {
            return 1.0; // Rojo
        }
    }

    private void clearMap() {
        if (googleMap != null) {
            googleMap.clear();
        }
    }

    private void enableUserLocation() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }

        googleMap.setMyLocationEnabled(true);
    }
}