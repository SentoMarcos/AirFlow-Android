package com.example.smariba_upv.airflow.LOGIC;

import com.example.smariba_upv.airflow.POJO.Medicion;

import java.util.Date;
import java.util.List;

public class MedicionUtils {

    /**
     * Obtiene la última medición de una lista de mediciones.
     *
     * @param mediciones Lista de mediciones.
     * @return La última medición basada en la fecha.
     */
    public static Medicion obtenerUltimaMedicion(List<Medicion> mediciones) {
        if (mediciones == null || mediciones.isEmpty()) {
            return null;
        }
        Medicion ultimaMedicion = mediciones.get(0);
        for (Medicion medicion : mediciones) {
            if (medicion.getFecha().after(ultimaMedicion.getFecha())) {
                ultimaMedicion = medicion;
            }
        }
        return ultimaMedicion;
    }

    /**
     * Calcula la media de los valores de una lista de mediciones.
     *
     * @param mediciones Lista de mediciones.
     * @return Media de los valores. Si la lista está vacía, devuelve 0.
     */
    public static double calcularMediaValores(List<Medicion> mediciones) {
        if (mediciones == null || mediciones.isEmpty()) {
            return 0;
        }
        double suma = 0;
        for (Medicion medicion : mediciones) {
            suma += medicion.getValor();
        }
        return suma / mediciones.size();
    }

    /**
     * Calcula la exposición total del usuario en función de las mediciones.
     *
     * @param mediciones Lista de mediciones.
     * @return Exposición total del usuario.
     */
    public static double calcularExposicionTotal(List<Medicion> mediciones) {
        if (mediciones == null || mediciones.isEmpty()) {
            return 0;
        }
        double exposicion = 0;
        for (Medicion medicion : mediciones) {
            exposicion += medicion.getValor(); // Ajustar la fórmula según lo que signifique "exposición"
        }
        return exposicion;
    }
}
