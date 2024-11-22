package com.example.smariba_upv.airflow.POJO;

public class SensorRequest {
    private int id_usuario;
    private int id_sensor;
    private String estado;
    private String num_referencia;
    private String uuid;
    private String nombre;
    private String conexion;
    private int bateria;

    public SensorRequest(int id_usuario, int id_sensor, String estado, String num_referencia, String uuid, String nombre, String conexion, int bateria) {
        this.id_usuario = id_usuario;
        this.id_sensor = id_sensor;
        this.estado = estado;
        this.num_referencia = num_referencia;
        this.uuid = uuid;
        this.nombre = nombre;
        this.conexion = conexion;
        this.bateria = bateria;
    }

    public SensorRequest(int id_sensor, String estado, String conexion, int bateria) {
        this.id_sensor = id_sensor;
        this.estado = estado;
        this.conexion = conexion;
        this.bateria = bateria;
    }

    // Getters y setters
}