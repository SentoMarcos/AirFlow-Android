// SensorResponse.java
package com.example.smariba_upv.airflow.API.MODELS;
/**
 * @file SensorResponse.java
 * @brief Clase que representa un objeto SensorResponse.
 * @version 1.0
 */

import com.google.gson.annotations.SerializedName;
/**
 * @class SensorResponse
 * @brief Clase que representa un objeto SensorResponse.
 */
public class SensorResponse {
    /**
     * @brief Atributos de la clase SensorResponse.
     * @param idUsuario Identificador del usuario.
     * @param idSensor Identificador del sensor.
     * @param sensor Objeto Sensor.
     * @return SensorResponse
     */
    @SerializedName("id_usuario")
    private int idUsuario;

    @SerializedName("id_sensor")
    private int idSensor;

    @SerializedName("Sensor")
    private Sensor sensor;

    // Getters and setters
    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public int getIdSensor() {
        return idSensor;
    }

    public void setIdSensor(int idSensor) {
        this.idSensor = idSensor;
    }

    public Sensor getSensor() {
        return sensor;
    }

    public void setSensor(Sensor sensor) {
        this.sensor = sensor;
    }
    /**
     * @class Sensor
     * @brief Clase que representa un objeto Sensor.
     */
    public static class Sensor {
        /**
         * @brief Atributos de la clase Sensor.
         * @param estado Estado del sensor.
         * @param numReferencia Número de referencia del sensor.
         * @param uuid UUID del sensor.
         * @param nombre Nombre del sensor.
         * @param conexion Estado de conexión del sensor.
         * @param bateria Nivel de batería del sensor
         * @return Sensor
         */
        @SerializedName("estado")
        private String estado;

        @SerializedName("num_referencia")
        private String numReferencia;

        @SerializedName("uuid")
        private String uuid;

        @SerializedName("nombre")
        private String nombre;

        @SerializedName("conexion")
        private boolean conexion;

        @SerializedName("bateria")
        private int bateria;

        // Getters and setters
        public String getEstado() {
            return estado;
        }

        public void setEstado(String estado) {
            this.estado = estado;
        }

        public String getNumReferencia() {
            return numReferencia;
        }

        public void setNumReferencia(String numReferencia) {
            this.numReferencia = numReferencia;
        }

        public String getUuid() {
            return uuid;
        }

        public void setUuid(String uuid) {
            this.uuid = uuid;
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
            return bateria;
        }

        public void setBateria(int bateria) {
            this.bateria = bateria;
        }
    }
}