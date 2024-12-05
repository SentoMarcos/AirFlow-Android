package com.example.smariba_upv.airflow.POJO;

public class User {
    private String nombre;      // Coincide con "nombre" en el JSON
    private String apellidos;   // Coincide con "apellidos" en el JSON
    private String email;       // Coincide con "email" en el JSON
    private String telefono;    // Coincide con "telefono" en el JSON
    private String contrasenya; // Coincide con "contrasenya" en el JSON
    private int id;             // Coincide con "id" en el JSON

    /**
     * @brief Constructor de la clase User
     * @param nombre      Nombre del usuario
     * @param apellidos   Apellidos del usuario
     * @param email       Correo electrónico del usuario
     * @param telefono    Teléfono del usuario
     * @param contrasenya Contraseña del usuario
     * Texto:nombre, Texto:apellidos, Texto:email, Texto:telefono, Texto:contrasenya => User()
     */
    public User(String nombre, String apellidos, String email, String telefono, String contrasenya) {
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.email = email;
        this.telefono = telefono;
        this.contrasenya = contrasenya;
    }

    /**
     * @brief Constructor de la clase User
     * @param email       Correo electrónico del usuario
     * @param contrasenya Contraseña del usuario
     * Texto:email, Texto:contrasenya => User()
     */
    public User(String email, String contrasenya) {
        this.email = email;
        this.contrasenya = contrasenya;
    }

    /**
     * @brief Constructor de la clase User
     * @param id        Identificador del usuario
     * @param nombre    Nombre del usuario
     * @param apellidos Apellidos del usuario
     * @param email     Correo electrónico del usuario
     * @param telefono  Teléfono del usuario
     * Entero:id, Texto:nombre, Texto:apellidos, Texto:email, Texto:telefono => User()
     */
    public User(int id, String nombre, String apellidos, String email, String telefono) {
        this.id = id;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.email = email;
        this.telefono = telefono;
    }

    /**
     * getNombre() => Texto:nombre
     * @return
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Texto:nombre => setNombre()
     * @param nombre
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    /**
     * getApellidos() => Texto:apellidos
     * @return
     */
    public String getApellidos() {
        return apellidos;
    }
    /**
     * Texto:apellidos => setApellidos()
     * @param apellidos
     */
    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }
    /**
     * getEmail() => Texto:email
     * @return
     */
    public String getEmail() {
        return email;
    }
    /**
     * Texto:email => setEmail()
     * @param email
     */
    public void setEmail(String email) {
        this.email = email;
    }
    /**
     * getTelefono() => Texto:telefono
     * @return
     */
    public String getTelefono() {
        return telefono;
    }
    /**
     * Texto:telefono => setTelefono()
     * @param telefono
     */
    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }
    /**
     * getContrasenya() => Texto:contrasenya
     * @return
     */
    public String getContrasenya() {
        return contrasenya;
    }
    /**
     * Texto:contrasenya => setContrasenya()
     * @param contrasenya
     */
    public void setContrasenya(String contrasenya) {
        this.contrasenya = contrasenya;
    }
    /**
     * getId() => N:id
     * @return
     */
    public int getId() {
        return id;
    }
    /**
     * N:id => setId()
     * @param id
     */
    public void setId(int id) {
        this.id = id;
    }
}
