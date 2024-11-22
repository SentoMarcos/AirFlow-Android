package com.example.smariba_upv.airflow.API.MODELS;

import android.hardware.Sensor;

public class UsuarioSensor {
    private int id_usuario;
    private Sensor sensor;

    // Getters y Setters
    public int getId_usuario() {
        return id_usuario;
    }

    public void setId_usuario(int id_usuario) {
        this.id_usuario = id_usuario;
    }

    public Sensor getSensor() {
        return sensor;
    }

    public void setSensor(Sensor sensor) {
        this.sensor = sensor;
    }
}
