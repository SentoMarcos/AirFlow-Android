package com.example.smariba_upv.airflow.API.MODELS;

public class MedicionMedia {
    private String fecha_dia; // Cambia según tu backend
    private double valor_promedio;

    public MedicionMedia(String fecha_dia, double valor_promedio) {
        this.fecha_dia = fecha_dia;
        this.valor_promedio = valor_promedio;
    }

    public String getFecha() {
        return fecha_dia;
    }

    public double getValorPromedio() {
        return valor_promedio;
    }

}
