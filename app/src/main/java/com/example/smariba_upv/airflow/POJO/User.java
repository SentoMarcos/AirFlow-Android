package com.example.smariba_upv.airflow.POJO;

public class User {
    private String name;
    private String email;
    private String contrasenya;
    private String phone;
    private String address;

    public User(String name, String email, String contrasenya, String phone, String address) {
        this.name = name;
        this.email = email;
        this.contrasenya = contrasenya;
        this.phone = phone;
        this.address = address;
    }

    //Constructor para el login
    public User(String email, String contrasenya) {
        this.email = email;
        this.contrasenya = contrasenya;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return contrasenya;
    }

    public void setPassword(String contrasenya) {
        this.contrasenya = contrasenya;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

}
