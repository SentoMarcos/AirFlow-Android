package com.example.smariba_upv.airflow.PRESENTACION;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.smariba_upv.airflow.R;

public class SensorFragment extends Fragment {
    private Button btnAddSensor;

    public SensorFragment() {
        // Required empty public constructor
    }

    public static SensorFragment newInstance(String param1, String param2) {
        SensorFragment fragment = new SensorFragment();
        Bundle args = new Bundle();
        args.putString("param1", param1);
        args.putString("param2", param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            String mParam1 = getArguments().getString("param1");
            String mParam2 = getArguments().getString("param2");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sensor, container, false);
        btnAddSensor = view.findViewById(R.id.add_sensor);
        btnAddSensor.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), QRreader.class);
            startActivity(intent);
        });
        return view;
    }
}