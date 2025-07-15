/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.travelgo.backend_travelgo.model;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table (name ="usuarios")
public class Usuario {
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private int id;

@ManyToOne
@JoinColumn(name = "credencial_id", nullable = false)
private Credencial credencial;

@Column(name = "nombre_completo", length = 100)
private String nombreCompleto;

@Column(name = "telefono", length = 20)
private String telefono;

@Column(name = "nacionalidad", length = 50)
private String nacionalidad;

@Column(name = "fecha_nacimiento")
private LocalDate fechaNacimiento;

@Enumerated(EnumType.STRING)
@Column(name = "genero", columnDefinition = "ENUM('M', 'F', 'Otro')")
private Genero genero;

 public Usuario() {
    }

public Usuario (String nombreCompleto, String telefono,String nacionalidad,LocalDate fechaNacimiento, Genero genero ){
        this.nombreCompleto = nombreCompleto;
        this.telefono = telefono;
        this.nacionalidad = nacionalidad;
        this.fechaNacimiento = fechaNacimiento;
        this.genero = genero;
}

public int getId() {
        return id;
    }

    public Credencial getCredencial() {
        return credencial;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public String getTelefono() {
        return telefono;
    }

    public String getNacionalidad() {
        return nacionalidad;
    }

    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }

    public Genero getGenero() {
        return genero;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setCredencial(Credencial credencial) {
        this.credencial = credencial;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public void setNacionalidad(String nacionalidad) {
        this.nacionalidad = nacionalidad;
    }

    public void setFechaNacimiento(LocalDate fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public void setGenero(Genero genero) {
        this.genero = genero;
    }

    // Enum para genero
    public enum Genero {
        M, F, Otro
    }
}
