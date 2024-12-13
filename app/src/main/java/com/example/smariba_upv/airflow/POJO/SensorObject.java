package com.example.smariba_upv.airflow.POJO;

import android.content.Context;
import android.content.Intent;
import com.google.gson.annotations.SerializedName;

import java.util.Objects;

public class SensorObject {
    @SerializedName("id_sensor")
    int id;
    @SerializedName("estado")
    private String estado;

    @SerializedName("num_referencia")
    private String num_ref;

    @SerializedName("uuid")
    private String UUID;

    @SerializedName("nombre")
    private String nombre;

    @SerializedName("conexion")
    private boolean conexion;

    @SerializedName("bateria")
    private int Bateria;
    private double distancia;

    public SensorObject(int id, String estado, String num_ref, String UUID, String nombre, boolean conexion, int bateria) {
        this.id = id;
        this.estado = estado;
        this.num_ref = num_ref;
        this.UUID = UUID;
        this.nombre = nombre;
        this.conexion = conexion;
        Bateria = bateria;
    }

    public SensorObject(int id, String estado, String num_ref, String UUID, String nombre, boolean conexion, int bateria,double distancia) {
        this.id = id;
        this.estado = estado;
        this.num_ref = num_ref;
        this.UUID = UUID;
        this.nombre = nombre;
        this.conexion = conexion;
        Bateria = bateria;
        this.distancia = distancia;
    }


    public SensorObject( String estado, String num_ref, String UUID, String nombre, boolean conexion, int bateria) {
        this.estado = estado;
        this.num_ref = num_ref;
        this.UUID = UUID;
        this.nombre = nombre;
        this.conexion = conexion;
        Bateria = bateria;
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

    public String getNum_ref() {
        return num_ref;
    }

    public void setNum_ref(String num_ref) {
        this.num_ref = num_ref;
    }

    public String getUUID() {
        return UUID;
    }

    public void setUUID(String UUID) {
        this.UUID = UUID;
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
        return Bateria;
    }

    public void setBateria(int bateria) {
        Bateria = bateria;
    }

    public double getDistancia() {
        return distancia;
    }

    public void setDistancia(double distancia) {
        this.distancia = distancia;
    }

    @Override
    public String toString() {
        return "SensorObject{" +
                "id=" + id +
                "estado='" + estado + '\'' +
                ", num_ref='" + num_ref + '\'' +
                ", UUID='" + UUID + '\'' +
                ", nombre='" + nombre + '\'' +
                ", conexion=" + conexion +
                ", bateria=" + Bateria +
                '}';
    }

}