package com.example.smariba_upv.airflow.POJO;

public class User {
    private String nombre;      // Coincide con "nombre" en el JSON
    private String apellidos;   // Coincide con "apellidos" en el JSON
    private String email;       // Coincide con "email" en el JSON
    private String telefono;    // Coincide con "telefono" en el JSON
    private String contrasenya; // Coincide con "contrasenya" en el JSON
    private int id;             // Coincide con "id" en el JSON

    // Constructor
    public User(String nombre, String apellidos, String email, String telefono, String contrasenya) {
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.email = email;
        this.telefono = telefono;
        this.contrasenya = contrasenya;
    }

    //constructor login
    public User(String email, String contrasenya) {
        this.email = email;
        this.contrasenya = contrasenya;
    }

    //constructor editUsuario
    public User(int id, String nombre, String apellidos, String email, String telefono) {
        this.id = id;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.email = email;
        this.telefono = telefono;
    }

    // Getters y setters
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getContrasenya() {
        return contrasenya;
    }

    public void setContrasenya(String contrasenya) {
        this.contrasenya = contrasenya;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
