package com.travelgo.backend_travelgo.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "alojamientos")
public class Alojamiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
   
    @Column(name = "nombre", length = 100)
    private String nombre;
    
    @Column(name = "direccion", length = 100)
    private String direccion;
    
    @Column(name = "ciudad", length = 100)
    private String ciudad;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", columnDefinition = "ENUM('Hotel', 'Hostal', 'Airbnb', 'Otros')")
    private Tipo tipo;
    
    @Column(name = "capacidad")
    private int capacidad;
     
    @Column(name = "precio_noche", precision = 10, scale = 2, nullable = false)
    private BigDecimal precio;
     
    @ManyToOne
    @JoinColumn(name = "viaje_id", nullable = false)
    private Viaje viaje;
    
    // Constructor vacío
    public Alojamiento() {
       
    }

    // Constructor completo
    public Alojamiento(String nombre, String direccion, String ciudad, Tipo tipo, int capacidad, BigDecimal precio, Viaje viaje) {
        this.nombre = nombre;
        this.direccion = direccion;
        this.ciudad = ciudad;
        this.tipo = tipo;
        this.capacidad = capacidad;
        this.precio = precio;
        this.viaje = viaje;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDireccion() {
        return direccion;
    }

    public String getCiudad() {
        return ciudad;
    }

    public Tipo getTipo() {
        return tipo;
    }

    public int getCapacidad() {
        return capacidad;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public Viaje getViaje() {
        return viaje;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    public void setTipo(Tipo tipo) {
        this.tipo = tipo;
    }

    public void setCapacidad(int capacidad) {
        this.capacidad = capacidad;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }

    public void setViaje(Viaje viaje) {
        this.viaje = viaje;
    }

    // Enum tipo de alojamiento
    public enum Tipo {
        Hotel, Hostal, Airbnb, Otros
    }
}

