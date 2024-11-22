package com.example.smariba_upv.airflow.API.MODELS;

public class SensorResponse {
    private UsuarioSensor usuarioSensor;
    private int sensorId;

    public UsuarioSensor getUsuarioSensor() {
        return usuarioSensor;
    }

    public void setUsuarioSensor(UsuarioSensor usuarioSensor) {
        this.usuarioSensor = usuarioSensor;
    }

    public int getSensorId() {
        return sensorId;
    }

    public void setSensorId(int sensorId) {
        this.sensorId = sensorId;
    }
}

