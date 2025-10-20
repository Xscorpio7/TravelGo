/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.travelgo.backend_travelgo.dto;

public class LoginResponse {
    private String token;
    private Integer usuarioId;
    private String correo;
    private String primerNombre;
    private String primerApellido;
    private String tipoUsuario;
    
    public LoginResponse(String token, Integer usuarioId, String correo, 
                        String primerNombre, String primerApellido, String tipoUsuario) {
        this.token = token;
        this.usuarioId = usuarioId;
        this.correo = correo;
        this.primerNombre = primerNombre;
        this.primerApellido = primerApellido;
        this.tipoUsuario = tipoUsuario;
    }
    
    // Getters y Setters
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    
    public Integer getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Integer usuarioId) { this.usuarioId = usuarioId; }
    
    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }
    
    public String getPrimerNombre() { return primerNombre; }
    public void setPrimerNombre(String primerNombre) { this.primerNombre = primerNombre; }
    
    public String getPrimerApellido() { return primerApellido; }
    public void setPrimerApellido(String primerApellido) { this.primerApellido = primerApellido; }
    
    public String getTipoUsuario() { return tipoUsuario; }
    public void setTipoUsuario(String tipoUsuario) { this.tipoUsuario = tipoUsuario; }
}