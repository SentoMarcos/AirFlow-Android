package com.example.smariba_upv.airflow.API.MODELS;

public class SensorResponse {
    private int id;
    private String estado;
    private String num_referencia;
    private String uuid;
    private String nombre;
    private boolean conexion;
    private int bateria;

    public SensorResponse(int id, String estado, String num_referencia, String uuid, String nombre, boolean conexion, int bateria) {
        this.id = id;
        this.estado = estado;
        this.num_referencia = num_referencia;
        this.uuid = uuid;
        this.nombre = nombre;
        this.conexion = conexion;
        this.bateria = bateria;
    }

    public int getId() {
        return id;
    }

    public String getEstado() {
        return estado;
    }

    public String getNum_referencia() {
        return num_referencia;
    }

    public String getUuid() {
        return uuid;
    }

    public String getNombre() {
        return nombre;
    }

    public boolean isConexion() {
        return conexion;
    }

    public int getBateria() {
        return bateria;
    }
}
