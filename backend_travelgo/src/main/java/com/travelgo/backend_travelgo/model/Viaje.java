/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.travelgo.backend_travelgo.model;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.List;
import java.math.BigDecimal;

@Entity
@Table (name ="viajes")
public class Viaje {
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private int id;

@Column(name = "titulo", length = 100)
private String titulo;

@Column(name = "descripcion", length = 100)
private String descripcion;

@Column(name = "destino", length = 20)
private String destino;

@Column(name = "telefono", length = 20)
private String telefono;

@Column(name = "fecha_inicio", nullable = false)
private LocalDate fechaInicio;

@Column(name = "fecha_fin", nullable = false)
private LocalDate fechaFin;

@Column(name = "precio", precision = 10, scale = 2, nullable = false)
private BigDecimal precio;

@Column(name = "cupos_disponibles")
private int cuposDisponibles;


 public Viaje() {
    }

public Viaje(String titulo, String descripcion, String destino, String telefono,
                 LocalDate fechaInicio, LocalDate fechaFin, BigDecimal precio, int cuposDisponibles) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.destino = destino;
        this.telefono = telefono;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.precio = precio;
        this.cuposDisponibles = cuposDisponibles;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getDestino() {
        return destino;
    }

    public String getTelefono() {
        return telefono;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public LocalDate getFechaFin() {
        return fechaFin;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public int getCuposDisponibles() {
        return cuposDisponibles;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public void setDestino(String destino) {
        this.destino = destino;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public void setFechaInicio(LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public void setFechaFin(LocalDate fechaFin) {
        this.fechaFin = fechaFin;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }

    public void setCuposDisponibles(int cuposDisponibles) {
        this.cuposDisponibles = cuposDisponibles;
    }
}