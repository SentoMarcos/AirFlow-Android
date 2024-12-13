package com.example.smariba_upv.airflow.POJO;

import com.google.gson.annotations.SerializedName;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;

public class Medicion {
    @SerializedName("id")
    private int id;
    @SerializedName("id_sensor")
    private int idSensor;
    @SerializedName("tipo_gas")
    private String tipoGas;
    @SerializedName("latitud")
    private double latitud;
    @SerializedName("longitud")
    private double longitud;
    @SerializedName("valor")
    private double valor;
    @SerializedName("fecha")
    private Date fecha;

    // Constructores
    public Medicion(int id, int idSensor, String tipoGas, double latitud, double longitud, double valor, Date fecha) {
        this.id = id;
        this.idSensor = idSensor;
        this.tipoGas = tipoGas;
        this.latitud = latitud;
        this.longitud = longitud;
        this.valor = valor;
        this.fecha = fecha;
    }
    public Medicion(double latitud, double longitud, double valor) {
        this.latitud = latitud;
        this.longitud = longitud;
        this.valor = valor;
    }
    public Medicion(int idSensor, String tipoGas, double latitud, double longitud, double valor, Date fecha) {
        this(0, idSensor, tipoGas, latitud, longitud, valor, fecha);
    }

    public Medicion(int idSensor, String tipoGas, double latitud, double longitud, double valor) {
        this(idSensor, tipoGas, latitud, longitud, valor, new Date());
    }

    // Getters y Setters con validaciones
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
        if (latitud < -90 || latitud > 90) {
            throw new IllegalArgumentException("Latitud debe estar entre -90 y 90");
        }
        this.latitud = latitud;
    }

    public double getLongitud() {
        return longitud;
    }

    public void setLongitud(double longitud) {
        if (longitud < -180 || longitud > 180) {
            throw new IllegalArgumentException("Longitud debe estar entre -180 y 180");
        }
        this.longitud = longitud;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        if (valor < 0) {
            throw new IllegalArgumentException("El valor no puede ser negativo");
        }
        this.valor = valor;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    /**
     * Formatea la fecha en formato ISO 8601.
     * @param fecha Fecha a formatear.
     * @return Cadena de texto con la fecha formateada.
     */
    public static String formatFecha(Date fecha) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
        sdf.setTimeZone(TimeZone.getTimeZone("UTC")); // Ajustar zona horaria si es necesario
        return sdf.format(fecha);
    }

    @Override
    public String toString() {
        return "Medicion{" +
                "id=" + id +
                ", idSensor=" + idSensor +
                ", tipoGas='" + tipoGas + '\'' +
                ", latitud=" + latitud +
                ", longitud=" + longitud +
                ", valor=" + valor +
                ", fecha=" + formatFecha(fecha) + // Usar formato formateado en toString
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Medicion medicion = (Medicion) o;
        return id == medicion.id &&
                idSensor == medicion.idSensor &&
                Double.compare(medicion.latitud, latitud) == 0 &&
                Double.compare(medicion.longitud, longitud) == 0 &&
                Double.compare(medicion.valor, valor) == 0 &&
                Objects.equals(tipoGas, medicion.tipoGas) &&
                Objects.equals(fecha, medicion.fecha);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, idSensor, tipoGas, latitud, longitud, valor, fecha);
    }
}
