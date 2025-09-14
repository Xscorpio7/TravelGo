package com.travelgo.backend_travelgo.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "reservas")
public class Reserva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "viaje_id", nullable = false)
    private Viaje viaje;

    @ManyToOne
    @JoinColumn(name = "alojamiento_id", nullable = false)
    private Alojamiento alojamiento;

    @ManyToOne
    @JoinColumn(name = "transporte_id", nullable = false)
    private Transporte transporte;

    @Column(name = "fecha_reserva", nullable = false)
    private LocalDateTime fechaReserva;


    @Enumerated(EnumType.STRING)
    @Column(name = "estado", columnDefinition = "ENUM('pendiente', 'pagado', 'fallido')", nullable = false)
    private Estado estado;

    // Constructor vacío
    public Reserva() {}

    // Constructor completo
    public Reserva(Usuario usuario, Viaje viaje, Alojamiento alojamiento, Transporte transporte,
                   LocalDateTime fechaReserva, Estado estado) {
        this.usuario = usuario;
        this.viaje = viaje;
        this.alojamiento = alojamiento;
        this.transporte = transporte;
        this.fechaReserva = fechaReserva;
        this.estado = estado;
    }

    // Getters y Setters

    public int getId() {
        return id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public Viaje getViaje() {
        return viaje;
    }

    public Alojamiento getAlojamiento() {
        return alojamiento;
    }

    public Transporte getTransporte() {
        return transporte;
    }

    public LocalDateTime getFechaReserva() {
        return fechaReserva;
    }

    public Estado getEstado() {
        return estado;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public void setViaje(Viaje viaje) {
        this.viaje = viaje;
    }

    public void setAlojamiento(Alojamiento alojamiento) {
        this.alojamiento = alojamiento;
    }

    public void setTransporte(Transporte transporte) {
        this.transporte = transporte;
    }

    public void setFechaReserva(LocalDateTime fechaReserva) {
        this.fechaReserva = fechaReserva;
    }

    public void setEstado(Estado estado) {
        this.estado = estado;
    }

    public enum Estado {
        pendiente, pagado, fallido
    }
}
