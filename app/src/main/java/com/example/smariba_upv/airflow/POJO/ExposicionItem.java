package com.example.smariba_upv.airflow.POJO;

public class ExposicionItem {
    private String titulo;
    private String descripcion;
    private String nivel;

    public ExposicionItem(String titulo, String descripcion, String nivel) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.nivel = nivel;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getNivel() {
        return nivel;
    }
}
