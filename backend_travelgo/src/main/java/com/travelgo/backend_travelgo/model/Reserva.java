package com.travelgo.backend_travelgo.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "reservas")
public class Reserva {
    
    public enum Estado {
        pendiente, confirmada, cancelada
    }
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    // ✅ IMPORTANTE: El nombre del campo Java debe ser "usuarioId" (camelCase)
    @Column(nullable = false, name = "usuario_id")
    private Integer usuarioId;
    
    @Column(nullable = false, name = "viaje_id")
    private Integer viajeId;
    
    @Column(name = "alojamiento_id", nullable = true)
    private Integer alojamientoId;
    
    @Column(name = "transporte_id", nullable = true)
    private Integer transporteId;
    
    @Column(nullable = false, updatable = false, name = "fecha_reserva")
    private LocalDateTime fechaReserva;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "estado")
    private Estado estado;
    
    @PrePersist
    protected void onCreate() {
        fechaReserva = LocalDateTime.now();
        if (estado == null) {
            estado = Estado.pendiente;
        }
    }
    
    // Constructores
    public Reserva() {
    }
    
    public Reserva(Integer usuarioId, Integer viajeId) {
        this.usuarioId = usuarioId;
        this.viajeId = viajeId;
        this.estado = Estado.pendiente;
    }
    
    // Getters y Setters - USAR CAMELCASE
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    
    public Integer getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Integer usuarioId) { this.usuarioId = usuarioId; }
    
    public Integer getViajeId() { return viajeId; }
    public void setViajeId(Integer viajeId) { this.viajeId = viajeId; }
    
    public Integer getAlojamientoId() { return alojamientoId; }
    public void setAlojamientoId(Integer alojamientoId) { this.alojamientoId = alojamientoId; }
    
    public Integer getTransporteId() { return transporteId; }
    public void setTransporteId(Integer transporteId) { this.transporteId = transporteId; }
    
    public LocalDateTime getFechaReserva() { return fechaReserva; }
    public void setFechaReserva(LocalDateTime fechaReserva) { this.fechaReserva = fechaReserva; }
    
    public Estado getEstado() { return estado; }
    public void setEstado(Estado estado) { this.estado = estado; }
    
    // ⚠️ MÉTODOS DEPRECADOS PARA RETROCOMPATIBILIDAD
    // Mantener temporalmente para no romper código existente
    @Deprecated
    public Integer getUsuario_id() { return usuarioId; }
    @Deprecated
    public void setUsuario_id(Integer usuarioId) { this.usuarioId = usuarioId; }
    
    @Deprecated
    public Integer getViaje_id() { return viajeId; }
    @Deprecated
    public void setViaje_id(Integer viajeId) { this.viajeId = viajeId; }
    
    @Deprecated
    public Integer getAlojamiento_id() { return alojamientoId; }
    @Deprecated
    public void setAlojamiento_id(Integer alojamientoId) { this.alojamientoId = alojamientoId; }
    
    @Deprecated
    public Integer getTransporte_id() { return transporteId; }
    @Deprecated
    public void setTransporte_id(Integer transporteId) { this.transporteId = transporteId; }
    
    @Deprecated
    public LocalDateTime getFecha_reserva() { return fechaReserva; }
    @Deprecated
    public void setFecha_reserva(LocalDateTime fechaReserva) { this.fechaReserva = fechaReserva; }
}