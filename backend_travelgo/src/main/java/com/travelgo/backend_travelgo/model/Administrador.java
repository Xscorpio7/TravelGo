/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.travelgo.backend_travelgo.model;
import jakarta.persistence.*;
import java.util.List;

@Entity
@Table (name ="administradores")
public class Administrador {
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private int id;

@ManyToOne
@JoinColumn(name = "credencial_id", nullable = false)
private Credencial credencial;

@Column(name = "nombre", length = 100)
private String nombre;


@Column(name = "cargo", length = 100)
private String cargo;


public Administrador () {
    
}
public Administrador (String nombre, String cargo){
        this.nombre = nombre;
        this.cargo = cargo;
}

public Administrador(Credencial credencial, String nombre, String cargo) {
        this.credencial = credencial;
        this.nombre = nombre;
        this.cargo = cargo;
    }

public int getId(){
    return id;
}
 public Credencial getCredencial() {
        return credencial;
    }

    public String getNombre() {
        return nombre;
    }

    public String getCargo() {
        return cargo;
    }
 // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setCredencial(Credencial credencial) {
        this.credencial = credencial;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }
}