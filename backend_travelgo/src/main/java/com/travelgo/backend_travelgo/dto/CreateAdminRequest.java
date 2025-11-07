package com.travelgo.backend_travelgo.dto;

public class CreateAdminRequest {
    private String nombre;
    private String cargo;
    private String correo;
    private String contrasena;
    
    // Constructor vacío
    public CreateAdminRequest() {}
    
    // Constructor completo
    public CreateAdminRequest(String nombre, String cargo, String correo, String contrasena) {
        this.nombre = nombre;
        this.cargo = cargo;
        this.correo = correo;
        this.contrasena = contrasena;
    }
    
    // Getters y Setters
    public String getNombre() {
        return nombre;
    }
    
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    
    public String getCargo() {
        return cargo;
    }
    
    public void setCargo(String cargo) {
        this.cargo = cargo;
    }
    
    public String getCorreo() {
        return correo;
    }
    
    public void setCorreo(String correo) {
        this.correo = correo;
    }
    
    public String getContrasena() {
        return contrasena;
    }
    
    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }
}