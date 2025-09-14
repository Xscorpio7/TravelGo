package com.travelgo.backend_travelgo.model;

import java.time.LocalDate;
import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "pagos")
public class Pago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "reserva_id", nullable = false)
    private Reserva reserva;

    @Enumerated(EnumType.STRING)
    @Column(name = "metodo_pago", columnDefinition = "ENUM('Tarjeta', 'Nequi', 'PSE', 'Efectivo')")
    private MetodoPago metodoPago;

    @Column(name = "monto", precision = 10, scale = 2)
    private BigDecimal monto;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", columnDefinition = "ENUM('pendiente', 'pagado', 'fallido')")
    private Estado estado;

    @Column(name = "fecha_pago")
    private LocalDate fechaPago;

    // Constructor vacío (requerido por JPA)
    public Pago() {}

    // Constructor completo
    public Pago(MetodoPago metodoPago, BigDecimal monto, Estado estado, LocalDate fechaPago) {
        this.metodoPago = metodoPago;
        this.monto = monto;
        this.estado = estado;
        this.fechaPago = fechaPago;
    }

    // Getters
    public int getId() {
        return id;
    }

    public Reserva getReserva() {
        return reserva;
    }

    public MetodoPago getMetodoPago() {
        return metodoPago;
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public Estado getEstado() {
        return estado;
    }

    public LocalDate getFechaPago() {
        return fechaPago;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setReserva(Reserva reserva) {
        this.reserva = reserva;
    }

    public void setMetodoPago(MetodoPago metodoPago) {
        this.metodoPago = metodoPago;
    }

    public void setMonto(BigDecimal monto) {
        this.monto = monto;
    }

    public void setEstado(Estado estado) {
        this.estado = estado;
    }

    public void setFechaPago(LocalDate fechaPago) {
        this.fechaPago = fechaPago;
    }

    // Enumeraciones
    public enum MetodoPago {
        Tarjeta,
        Nequi,
        PSE,
        Efectivo
    }

    public enum Estado {
        pendiente,
        pagado,
        fallido
    }
}
