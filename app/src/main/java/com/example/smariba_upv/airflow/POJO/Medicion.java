/********************************************
 * @file Medicion.java
 * @brief Clase que representa una medición de gas en un lugar determinado.
 * @version 1.0
 * @date 2024
 *******************************************/

package com.example.smariba_upv.airflow.POJO;

import java.util.Date;


public class Medicion {
    int id, idSenor;
    String tipoGas;
    double latitud, longitud,valor;
    Date fecha;

    public Medicion(int id, int idSenor, String tipoGas, double latitud, double longitud, double valor, Date fecha) {
        this.id = id;
        this.idSenor = idSenor;
        this.tipoGas = tipoGas;
        this.latitud = latitud;
        this.longitud = longitud;
        this.valor = valor;
        this.fecha = fecha;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdSenor() {
        return idSenor;
    }

    public void setIdSenor(int idSenor) {
        this.idSenor = idSenor;
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
