package com.example.smariba_upv.airflow.API.MODELS;
/********************************************
 * @file SensorRequest.java
 * @brief Clase que representa un objeto SensorRequest.
 * @version 1.0
 * @date 2024
 *******************************************/
public class SensorRequest {
    /**
     * @brief Atributos de la clase SensorRequest.
     * @param id_usuario Identificador del usuario.
     * @param id_sensor Identificador del sensor.
     * @param estado Estado del sensor.
     * @param num_referencia Número de referencia del sensor.
     * @param uuid UUID del sensor.
     * @param nombre Nombre del sensor.
     * @param conexion Estado de conexión del sensor.
     * @param bateria Nivel de batería del sensor
     * @return SensorRequest
     */
    private int id_usuario;
    private int id_sensor;
    private String estado;
    private String num_referencia;
    private String uuid;
    private String nombre;
    private boolean conexion;
    private int bateria;

    /**
     * @fn SensorRequest()
     * @brief Constructor de la clase SensorRequest.
     * @param id_usuario
     * @param id_sensor
     * @param estado
     * @param num_referencia
     * @param uuid
     * @param nombre
     * @param conexion
     * @param bateria
     */
    //N: id_usuario, id_sensor, estado, num_referencia, uuid, nombre, conexion, bateria => SensorRequest()
    public SensorRequest(int id_usuario, int id_sensor, String estado, String num_referencia, String uuid, String nombre, boolean conexion, int bateria) {
        this.id_usuario = id_usuario;
        this.id_sensor = id_sensor;
        this.estado = estado;
        this.num_referencia = num_referencia;
        this.uuid = uuid.trim();
        this.nombre = nombre;
        this.conexion = conexion;
        this.bateria = bateria;
    }
    /**
     * @fn SensorRequest()
     * @brief Constructor de la clase SensorRequest.
     * @param idSensor
     * @param estado
     * @param conexion
     * @param bateria
     */
    public SensorRequest(int idSensor, String estado, boolean conexion, int bateria) {
        this.id_sensor = idSensor;
        this.estado = estado;
        this.conexion = conexion;
        this.bateria = bateria;
    }
    /**
     * @fn SensorRequest()
     * @brief Constructor de la clase SensorRequest.
     * @param id_usuario
     * @param estado
     * @param num_referencia
     * @param uuid
     * @param nombre
     * @param conexion
     * @param bateria
     */
    public SensorRequest(int id_usuario, String estado, String num_referencia, String uuid, String nombre, boolean conexion, int bateria) {
        this.id_usuario = id_usuario;
        this.estado = estado;
        this.num_referencia = num_referencia;
        this.uuid = uuid.trim();
        this.nombre = nombre;
        this.conexion = conexion;
        this.bateria = bateria;
    }

    /**
     * @fn getIdUsuario()
     * @return
     */
    public int getIdUsuario() {
        return id_usuario;
    }
    /**
     * @fn setIdUsuario()
     * @param id_usuario
     */
    public void setIdUsuario(int id_usuario) {
        this.id_usuario = id_usuario;
    }
    /**
     * @fn getIdSensor()
     * @return
     */
    public int getIdSensor() {
        return id_sensor;
    }
    /**
     * @fn setIdSensor()
     * @param id_sensor
     */
    public void setIdSensor(int id_sensor) {
        this.id_sensor = id_sensor;
    }
    /**
     * @fn getEstado()
     * @return
     */
    public String getEstado() {
        return estado;
    }
    /**
     * @fn setEstado()
     * @param estado
     */
    public void setEstado(String estado) {
        this.estado = estado;
    }
    /**
     * @fn getNumReferencia()
     * @return
     */
    public String getNumReferencia() {
        return num_referencia;
    }
    /**
     * @fn setNumReferencia()
     * @param num_referencia
     */
    public void setNumReferencia(String num_referencia) {
        this.num_referencia = num_referencia;
    }
    /**
     * @fn getUuid()
     * @return
     */
    public String getUuid() {
        return uuid;
    }
    /**
     * @fn setUuid()
     * @param uuid
     */
    public void setUuid(String uuid) {
        this.uuid = uuid.trim();
    }
    /**
     * @fn getNombre()
     * @return
     */
    public String getNombre() {
        return nombre;
    }
    /**
     * @fn setNombre()
     * @param nombre
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    /**
     * @fn isConexion()
     * @return
     */
    public boolean isConexion() {
        return conexion;
    }
    /**
     * @fn setConexion()
     * @param conexion
     */
    public void setConexion(boolean conexion) {
        this.conexion = conexion;
    }
    /**
     * @fn getBateria()
     * @return
     */
    public int getBateria() {
        return bateria;
    }
    /**
     * @fn setBateria()
     * @param bateria
     */
    public void setBateria(int bateria) {
        this.bateria = bateria;
    }
}