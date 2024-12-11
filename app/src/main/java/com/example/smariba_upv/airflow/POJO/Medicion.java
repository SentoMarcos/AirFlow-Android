/********************************************
 * @file Medicion.java
 * @brief Clase que representa una medición de gas en un lugar determinado.
 * @version 1.0
 * @date 2024
 *******************************************/

package com.example.smariba_upv.airflow.POJO;

import java.util.Date;
import java.util.Objects;

public class Medicion {
    private int id, idSensor;
    private String tipoGas;
    private double latitud, longitud, valor;
    private Date fecha;

    public Medicion(int id, int idSensor, String tipoGas, double latitud, double longitud, double valor) {
        this.id = id;
        this.idSensor = idSensor;
        this.tipoGas = tipoGas;
        this.latitud = latitud;
        this.longitud = longitud;
        this.valor = valor;
        this.fecha = new Date();
    }

    public Medicion(int idSensor, String tipoGas, double latitud, double longitud, double valor) {
        this.idSensor = idSensor;
        this.tipoGas = tipoGas;
        this.latitud = latitud;
        this.longitud = longitud;
        this.valor = valor;
        this.fecha = new Date();
    }

    // Getters y Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdSensor() {
        return idSensor;
    }

    public void setIdSensor(int idSensor) {
        this.idSensor = idSensor;
    }

    public String getTipoGas() {
        return tipoGas;
    }

    public void setTipoGas(String tipoGas) {
        this.tipoGas = tipoGas;
    }

    public double getLatitud() {
        return latitud;
    }

    public void setLatitud(double latitud) {
        this.latitud = latitud;
    }

    public double getLongitud() {
        return longitud;
    }

    public void setLongitud(double longitud) {
        this.longitud = longitud;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }


}

