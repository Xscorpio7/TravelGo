package com.travelgo.backend_travelgo.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Modelo Transporte - Compatible con tabla existente en BD
 * Gestión manual de transportes (sin integración Amadeus Transfer API)
 */
@Entity
@Table(name = "transporte")
public class Transporte {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", columnDefinition = "ENUM('Avion', 'Bus', 'Tren', 'Barco', 'Auto_Rental', 'Taxi', 'Transfer')")
    private Tipo tipo;

    @Column(name = "proveedor", length = 100)
    private String proveedor;
    
    @Column(name = "numero_transporte", length = 50)
    private String numeroTransporte;

    @Column(name = "salida")
    private LocalDateTime salida;

    @Column(name = "llegada")
    private LocalDateTime llegada;

    @Column(name = "origen", length = 100)
    private String origen;

    @Column(name = "destino", length = 100)
    private String destino;

    @Column(name = "amadeus_id", length = 50)
    private String amadeusId;

    @Column(name = "booking_reference", length = 50)
    private String bookingReference;

    @Column(name = "precio", precision = 10, scale = 2)
    private BigDecimal precio;

    @Column(name = "currency", length = 3)
    private String currency = "USD";

    @Column(name = "capacidad")
    private Integer capacidad;

    @Column(name = "categoria", length = 50)
    private String categoria;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", columnDefinition = "ENUM('disponible', 'reservado', 'cancelado')")
    private Estado estado = Estado.disponible;

    @Column(name = "detalles_json", columnDefinition = "LONGTEXT")
    private String detallesJson;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "viaje_id", nullable = true)
    private Viaje viaje;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (estado == null) {
            estado = Estado.disponible;
        }
        if (currency == null) {
            currency = "USD";
        }
    }

    // Constructores
    public Transporte() {}

    public Transporte(Tipo tipo, String origen, String destino, BigDecimal precio) {
        this.tipo = tipo;
        this.origen = origen;
        this.destino = destino;
        this.precio = precio;
        this.estado = Estado.disponible;
    }

    // Getters y Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Tipo getTipo() { return tipo; }
    public void setTipo(Tipo tipo) { this.tipo = tipo; }

    public String getProveedor() { return proveedor; }
    public void setProveedor(String proveedor) { this.proveedor = proveedor; }
    
    public String getNumeroTransporte() { return numeroTransporte; }
    public void setNumeroTransporte(String numeroTransporte) { this.numeroTransporte = numeroTransporte; }

    public LocalDateTime getSalida() { return salida; }
    public void setSalida(LocalDateTime salida) { this.salida = salida; }

    public LocalDateTime getLlegada() { return llegada; }
    public void setLlegada(LocalDateTime llegada) { this.llegada = llegada; }

    public String getOrigen() { return origen; }
    public void setOrigen(String origen) { this.origen = origen; }

    public String getDestino() { return destino; }
    public void setDestino(String destino) { this.destino = destino; }

    public String getAmadeusId() { return amadeusId; }
    public void setAmadeusId(String amadeusId) { this.amadeusId = amadeusId; }

    public String getBookingReference() { return bookingReference; }
    public void setBookingReference(String bookingReference) { this.bookingReference = bookingReference; }

    public BigDecimal getPrecio() { return precio; }
    public void setPrecio(BigDecimal precio) { this.precio = precio; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public Integer getCapacidad() { return capacidad; }
    public void setCapacidad(Integer capacidad) { this.capacidad = capacidad; }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    public Estado getEstado() { return estado; }
    public void setEstado(Estado estado) { this.estado = estado; }

    public String getDetallesJson() { return detallesJson; }
    public void setDetallesJson(String detallesJson) { this.detallesJson = detallesJson; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public Viaje getViaje() { return viaje; }
    public void setViaje(Viaje viaje) { this.viaje = viaje; }

    // Métodos deprecados para compatibilidad
    @Deprecated
    public String getNumero_transporte() { return numeroTransporte; }
    @Deprecated
    public void setNumero_transporte(String numeroTransporte) { this.numeroTransporte = numeroTransporte; }

    // Enums
    public enum Tipo {
        Avion, Bus, Tren, Barco, Auto_Rental, Taxi, Transfer
    }

    public enum Estado {
        disponible, reservado, cancelado
    }

    @Override
    public String toString() {
        return "Transporte{" +
                "id=" + id +
                ", tipo=" + tipo +
                ", origen='" + origen + '\'' +
                ", destino='" + destino + '\'' +
                ", precio=" + precio +
                ", estado=" + estado +
                '}';
    }
}