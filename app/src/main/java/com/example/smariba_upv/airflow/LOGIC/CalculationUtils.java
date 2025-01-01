package com.example.smariba_upv.airflow.LOGIC;

import com.example.smariba_upv.airflow.API.MODELS.MedicionMedia;
import com.example.smariba_upv.airflow.POJO.ExposicionItem;
import com.example.smariba_upv.airflow.POJO.ItemNotisSalud;
import com.example.smariba_upv.airflow.POJO.Medicion;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

public class CalculationUtils {

    // Periodos definidos para cálculo
    public enum Periodo {
        DIA,
        SEMANA,
        MES,
        ANIO,
        TOTAL
    }

    /**
     * Genera una lista de elementos de exposición a partir de las mediciones promedio.
     *
     * @param mediciones Lista de mediciones promedio.
     * @return Lista de elementos de exposición.
     */
    public static List<ExposicionItem> generateExposicionItems(List<MedicionMedia> mediciones) {
        List<ExposicionItem> exposicionItems = new ArrayList<>();

        exposicionItems.add(createExposicionItem("Exposición diaria", mediciones, Periodo.DIA));
        exposicionItems.add(createExposicionItem("Exposición semanal", mediciones, Periodo.SEMANA));
        exposicionItems.add(createExposicionItem("Exposición mensual", mediciones, Periodo.MES));
        exposicionItems.add(createExposicionItem("Exposición anual", mediciones, Periodo.ANIO));
        exposicionItems.add(createExposicionItem("Exposición total", mediciones, Periodo.TOTAL));

        return exposicionItems;
    }

    private static ExposicionItem createExposicionItem(String title, List<MedicionMedia> mediciones, Periodo periodo) {
        String promedio = calcularMedia(mediciones, periodo);
        return new ExposicionItem(title, promedio, obtenerClasificacion(promedio));
    }

    /**
     * Calcula el promedio de valores según el periodo.
     *
     * @param mediciones Lista de mediciones promedio.
     * @param periodo    Periodo para calcular el promedio.
     * @return Promedio en formato de cadena.
     */
    public static String calcularMedia(List<MedicionMedia> mediciones, Periodo periodo) {
        LocalDate now = LocalDate.now();
        double suma = 0;
        int contador = 0;

        for (MedicionMedia medicion : mediciones) {
            LocalDate fecha = LocalDate.parse(medicion.getFecha());

            if (isWithinPeriod(fecha, now, periodo)) {
                suma += medicion.getValorPromedio();
                contador++;
            }
        }

        return contador > 0 ? String.format("%.2f", suma / contador) : "N/A";
    }

    private static boolean isWithinPeriod(LocalDate fecha, LocalDate now, Periodo periodo) {
        switch (periodo) {
            case DIA:
                return fecha.isEqual(now);
            case SEMANA:
                return !fecha.isBefore(now.minusWeeks(1));
            case MES:
                return !fecha.isBefore(now.minusMonths(1));
            case ANIO:
                return !fecha.isBefore(now.minusYears(1));
            case TOTAL:
                return true;
        }
        return false;
    }

    /**
     * Obtiene la clasificación de calidad del aire según el valor promedio.
     *
     * @param valor Valor promedio como cadena.
     * @return Clasificación del aire.
     */
    public static String obtenerClasificacion(String valor) {
        if ("N/A".equals(valor)) return "Sin datos";

        double valorPromedio = Double.parseDouble(valor.replace(",", "."));

        if (valorPromedio < 50) return "excelente";
        if (valorPromedio < 100) return "buena";
        if (valorPromedio < 150) return "media";
        if (valorPromedio < 200) return "mala";
        return "Peligroso";
    }

    /**
     * Crea una lista de notificaciones basadas en las mediciones.
     *
     * @param mediciones Lista de mediciones.
     * @return Lista de notificaciones.
     */
    public static List<ItemNotisSalud> createNotifications(List<Medicion> mediciones) {
        List<ItemNotisSalud> notifications = new ArrayList<>();

        for (Medicion medicion : mediciones) {
            String estado = obtenerClasificacion(String.valueOf(medicion.getValor()));
            String mensaje = generarMensaje(estado, medicion);
            String fechaFormateada = Medicion.formatFecha(medicion.getFecha());
            notifications.add(new ItemNotisSalud(fechaFormateada, mensaje, estado));
        }

        return notifications;
    }

    private static String generarMensaje(String estado, Medicion medicion) {
        return "La calidad del aire es " + estado + ". Valor: " + medicion.getValor();
    }

    /**
     * Filtra notificaciones según un filtro de tiempo.
     *
     * @param items  Lista de notificaciones.
     * @param filtro Filtro seleccionado.
     * @return Lista filtrada de notificaciones.
     */
    public static List<ItemNotisSalud> filterNotifications(List<ItemNotisSalud> items, String filtro) {
        List<ItemNotisSalud> filteredItems = new ArrayList<>();
        LocalDate now = LocalDate.now();

        for (ItemNotisSalud item : items) {
            LocalDate itemDate = LocalDate.parse(item.getTime().substring(0, 10));

            switch (filtro) {
                case "Hoy":
                    if (itemDate.isEqual(now)) filteredItems.add(item);
                    break;
                case "Ayer":
                    if (itemDate.isEqual(now.minusDays(1))) filteredItems.add(item);
                    break;
                case "Semana":
                    if (!itemDate.isBefore(now.minusWeeks(1))) filteredItems.add(item);
                    break;
                case "Mes":
                    if (!itemDate.isBefore(now.minusMonths(1))) filteredItems.add(item);
                    break;
                case "Año":
                    if (!itemDate.isBefore(now.minusYears(1))) filteredItems.add(item);
                    break;
                default:
                    filteredItems.addAll(items);
                    break;
            }
        }

        return filteredItems;
    }

    /**
     * Obtiene la lista de días de un mes para mostrar en el calendario.
     *
     * @param date Fecha base para calcular los días.
     * @return Lista de días como cadenas.
     */
    public static List<String> getDaysInMonth(LocalDate date) {
        List<String> daysInMonthArray = new ArrayList<>();
        YearMonth yearMonth = YearMonth.from(date);

        int daysInMonth = yearMonth.lengthOfMonth();
        LocalDate firstOfMonth = date.withDayOfMonth(1);
        int dayOfWeek = firstOfMonth.getDayOfWeek().getValue() % 7; // Ajuste para empezar en lunes.

        // Días vacíos iniciales
        for (int i = 0; i < dayOfWeek; i++) {
            daysInMonthArray.add(" ");
        }

        // Días del mes
        for (int i = 1; i <= daysInMonth; i++) {
            daysInMonthArray.add(String.valueOf(i));
        }

        return daysInMonthArray;
    }
}
