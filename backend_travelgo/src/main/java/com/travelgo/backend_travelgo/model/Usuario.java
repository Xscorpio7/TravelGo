/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.travelgo.backend_travelgo.model;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table (name ="usuarios")
public class Usuario {
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private int id;

@ManyToOne
@JoinColumn(name = "credencial_id", nullable = false)
private Credencial credencial;

@Column(name = "primerNombre", length = 100)
private String primerNombre;

@Column(name = "primerApellido", length = 100)
private String primerApellido;

@Column(name = "telefono", length = 20)
private String telefono;

@Enumerated(EnumType.STRING)
@Column(name = "nacionalidad", columnDefinition = "ENUM('Colombia','Mexico','Argentina','Ecuador','Peru','Bolivia','Chile','Paraguay','Uruguay','Panama','Costa_rica','Nicaragua','Honduras','Guatemala')")
private Nacionalidad nacionalidad;


@Column(name = "fechaNacimiento")
private LocalDate fechaNacimiento;

@Enumerated(EnumType.STRING)
@Column(name = "genero", columnDefinition = "ENUM('MALE', 'FEMALE', 'UNSPECIFIED')")
private Genero genero;

 public Usuario() {
    }

public Usuario (String primerNombre,String primerApellido, String telefono,Nacionalidad nacionalidad,LocalDate fechaNacimiento, Genero genero ){
        this.primerNombre = primerNombre;
        this.primerApellido = primerApellido;
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

    public String getPrimerNombre() {
        return primerNombre;
    }
    public String getPrimerApellido() {
        return primerApellido;
    }

    public String getTelefono() {
        return telefono;
    }

    public Nacionalidad getNacionalidad() {
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

    public void setPrimerNombre(String primerNombre) {
        this.primerNombre = primerNombre;
    }

    public void setPrimerApellido(String primerApellido) {
        this.primerApellido = primerApellido;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public void setNacionalidad(Nacionalidad nacionalidad) {
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
        MALE, FEMALE, UNSPECIFIED
    }
    public enum Nacionalidad {
        Colombia,Mexico,Argentina,Ecuador,Peru,Bolivia,Chile,Paraguay,Uruguay,Panama,Costa_rica,Nicaragua,Honduras,Guatemala
    }
}
