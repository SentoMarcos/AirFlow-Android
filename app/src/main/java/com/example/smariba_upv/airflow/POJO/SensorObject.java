package com.example.smariba_upv.airflow.POJO;

import android.content.Context;
import android.content.Intent;

import com.example.smariba_upv.airflow.Services.ArduinoGetterService;

public class SensorObject {
    private String uuid;
    private String name;
    private String typegas;
    private int measure;
    private String date;
    private int battery;

    public SensorObject(String uuid, String name, String typegas, int measure, String date, int battery) {
        this.uuid = uuid;
        this.name = name;
        this.typegas = typegas;
        this.measure = measure;
        this.date = date;
        this.battery = battery;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTypegas() {
        return typegas;
    }

    public void setTypegas(String typegas) {
        this.typegas = typegas;
    }

    public int getMeasure() {
        return measure;
    }

    public void setMeasure(int measure) {
        this.measure = measure;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getBattery() {
        return battery;
    }

    public void setBattery(int battery) {
        this.battery = battery;
    }

    public void conectSensor(Context context) {
        Intent serviceIntent = new Intent(context, ArduinoGetterService.class);
        serviceIntent.putExtra("sensor_uuid", uuid);
        context.startService(serviceIntent);
    }
}
