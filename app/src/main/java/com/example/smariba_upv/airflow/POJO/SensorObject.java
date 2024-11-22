package com.example.smariba_upv.airflow.POJO;

import android.content.Context;
import android.content.Intent;

import com.example.smariba_upv.airflow.Services.ArduinoGetterService;

public class SensorObject {
    private int id;
    private String estado;
    private String num_referencia;
    private String uuid;
    private String nombre;
    private boolean conexion;
    private int bateria;

    private int measure;
    private String date;
    private String typegas;

    public SensorObject(int id, String estado, String num_referencia, String uuid, String nombre, boolean conexion, int bateria) {
        this.id = id;
        this.estado = estado;
        this.num_referencia = num_referencia;
        this.uuid = uuid;
        this.nombre = nombre;
        this.conexion = conexion;
        this.bateria = bateria;
    }

    public SensorObject(String estado, String num_referencia, String uuid, String nombre, boolean conexion, int bateria) {
        this.estado = estado;
        this.num_referencia = num_referencia;
        this.uuid = uuid;
        this.nombre = nombre;
        this.conexion = conexion;
        this.bateria = bateria;
    }

    public SensorObject(String uuid, String nombre, String typegas, int measure, String date, int battery) {
        this.uuid = uuid;
        this.nombre = nombre;
        this.typegas = typegas;
        this.measure = measure;
        this.date = date;
        this.bateria = battery;
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

    public String getTypegas() {
        return typegas;
    }

    public void setTypegas(String typegas) {
        this.typegas = typegas;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getNum_referencia() {
        return num_referencia;
    }

    public void setNum_referencia(String num_referencia) {
        this.num_referencia = num_referencia;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public boolean isConexion() {
        return conexion;
    }

    public void setConexion(boolean conexion) {
        this.conexion = conexion;
    }

    public int getBateria() {
        return bateria;
    }

    public void setBateria(int bateria) {
        this.bateria = bateria;
    }

    public void conectSensor(Context context) {
        Intent serviceIntent = new Intent(context, ArduinoGetterService.class);
        serviceIntent.putExtra("sensor_uuid", uuid);
        context.startService(serviceIntent);
    }
}