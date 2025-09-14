/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.travelgo.backend_travelgo.model;
import jakarta.persistence.*;
import java.util.List;


import jakarta.persistence.*;

@Entity

@Table(name = "credenciales")
public class Credencial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false, unique = true, length = 100)
    private String correo;

    @Column(nullable = false, length = 255)
    private String contrasena;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_usuario", columnDefinition = "ENUM('usuario', 'admin')", nullable = false)
    private TipoUsuario tipoUsuario;

    @Column(name = "esta_activo")
    private boolean estaActivo = true;

    // Constructor vacío
    public Credencial() {}

    // Constructor completo
    public Credencial(String correo, String contrasena, TipoUsuario tipoUsuario, boolean estaActivo) {
        this.correo = correo;
        this.contrasena = contrasena;
        this.tipoUsuario = tipoUsuario;
        this.estaActivo = estaActivo;
    }

    // Getters y Setters
    public int getId() {
        return id;
    }

    public String getCorreo() {
        return correo;
    }

    public String getContrasena() {
        return contrasena;
    }

    public TipoUsuario getTipoUsuario() {
        return tipoUsuario;
    }

    public boolean getEstaActivo() {
        return estaActivo;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public void setTipoUsuario(TipoUsuario tipoUsuario) {
        this.tipoUsuario = tipoUsuario;
    }

    public void setEstaActivo(boolean estaActivo) {
        this.estaActivo = estaActivo;
    }

    // Enum interno
    public enum TipoUsuario {
        usuario,
        admin
    }
}