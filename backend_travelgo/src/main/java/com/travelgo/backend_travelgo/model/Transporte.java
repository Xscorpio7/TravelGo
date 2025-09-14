package com.travelgo.backend_travelgo.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.time.LocalDate;
@Entity
@Table(name = "transporte")
public class Transporte {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", columnDefinition = "ENUM('Avion', 'Bus', 'Tren', 'Barco')")
    private Tipo tipo;

    @Column(name = "proveedor", length = 100)
    private String proveedor;
    
    @Column(name = "numero_transporte", length = 50)
    private String numero_transporte;


    @Column(name = "salida")
    private LocalDateTime salida;

    @Column(name = "llegada")
    private LocalDateTime llegada;

    @Column(name = "origen", length = 100)
    private String origen;

    @Column(name = "destino", length = 100)
    private String destino;

    @ManyToOne
    @JoinColumn(name = "viaje_id", nullable = false)
    private Viaje viaje;  
    // Constructor vacío
    public Transporte() {}

    // Constructor completo
    public Transporte(Tipo tipo, String proveedor, LocalDateTime salida, LocalDateTime llegada, String origen, String destino) {
        this.tipo = tipo;
        this.proveedor = proveedor;
        this.numero_transporte = numero_transporte;
        this.salida = salida;
        this.llegada = llegada;
        this.origen = origen;
        this.destino = destino;
    }

    // Getters
    public int getId() {
        return id;
    }

    public Tipo getTipo() {
        return tipo;
    }

    public String getProveedor() {
        return proveedor;
    }
    
    public String getNumero_transporte() {
        return numero_transporte;
    }


    public LocalDateTime getSalida() {
        return salida;
    }

    public LocalDateTime getLlegada() {
        return llegada;
    }

    public String getOrigen() {
        return origen;
    }

    public String getDestino() {
        return destino;
    }

    public Viaje getViaje() {
        return viaje;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setTipo(Tipo tipo) {
        this.tipo = tipo;
    }

    public void setProveedor(String proveedor) {
        this.proveedor = proveedor;
    }
    
    public void setNumero_transporte(String numero_transporte) {
        this.numero_transporte = numero_transporte;
    }

    public void setSalida(LocalDateTime salida) {
        this.salida = salida;
    }

    public void setLlegada(LocalDateTime llegada) {
        this.llegada = llegada;
    }

    public void setOrigen(String origen) {
        this.origen = origen;
    }

    public void setDestino(String destino) {
        this.destino = destino;
    }

    public void setViaje(Viaje viaje) {
        this.viaje = viaje;
    }

    // Enum tipo de transporte
    public enum Tipo {
        Avion, Bus, Tren, Barco
    }
}
