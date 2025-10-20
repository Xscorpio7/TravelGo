package com.travelgo.backend_travelgo.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "pagina_de_reservas")
public class Reserva {
    
    public enum Estado {
        pendiente, confirmada, cancelada
    }
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(nullable = false, name = "usuario_id")
    private Integer usuario_id;
    
    @Column(nullable = false, name = "viaje_id")
    private Integer viaje_id;
    
    @Column(name = "alojamiento_id", nullable = true)
    private Integer alojamiento_id;
    
    @Column(name = "transporte_id", nullable = true)
    private Integer transporte_id;
    
    @Column(nullable = false, updatable = false, name = "fecha_reserva")
    private LocalDateTime fecha_reserva;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "estado")
    private Estado estado;
    
    @PrePersist
    protected void onCreate() {
        fecha_reserva = LocalDateTime.now();
        if (estado == null) {
            estado = Estado.pendiente;
        }
    }
    
    // Constructores
    public Reserva() {
    }
    
    public Reserva(Integer usuario_id, Integer viaje_id) {
        this.usuario_id = usuario_id;
        this.viaje_id = viaje_id;
        this.estado = Estado.pendiente;
    }
    
    // Getters y Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    
    public Integer getUsuario_id() { return usuario_id; }
    public void setUsuario_id(Integer usuario_id) { this.usuario_id = usuario_id; }
    
    public Integer getViaje_id() { return viaje_id; }
    public void setViaje_id(Integer viaje_id) { this.viaje_id = viaje_id; }
    
    public Integer getAlojamiento_id() { return alojamiento_id; }
    public void setAlojamiento_id(Integer alojamiento_id) { this.alojamiento_id = alojamiento_id; }
    
    public Integer getTransporte_id() { return transporte_id; }
    public void setTransporte_id(Integer transporte_id) { this.transporte_id = transporte_id; }
    
    public LocalDateTime getFecha_reserva() { return fecha_reserva; }
    public void setFecha_reserva(LocalDateTime fecha_reserva) { this.fecha_reserva = fecha_reserva; }
    
    public Estado getEstado() { return estado; }
    public void setEstado(Estado estado) { this.estado = estado; }
}