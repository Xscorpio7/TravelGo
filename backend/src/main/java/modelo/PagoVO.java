/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

import java.time.LocalDateTime;
import javax.persistence.NamedQueries;

/**
 *
 * @author Alejo
 */
public class PagoVO {
    
    private int id;
    private int reserva_id;
    private Metodo_pago metodo_pago;
    public enum Metodo_pago{
        Tarjeta,
        Nequi,
        PSE,
        Efectivo   
    }
    private double monto;
    private Estado estado;
    public enum Estado{
        pendiente,
        pagado,
        fallido
    }
    private LocalDateTime fecha_pago;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getReserva_id() {
        return reserva_id;
    }

    public void setReserva_id(int reserva_id) {
        this.reserva_id = reserva_id;
    }

    
    public Metodo_pago getMetodo_pago() {
        return metodo_pago;
    }

    public void setMetodo_pago(Metodo_pago metodo_pago) {
        this.metodo_pago = metodo_pago;
    }

    public double getMonto() {
        return monto;
    }

    public void setMonto(double monto) {
        this.monto = monto;
    }

    public Estado getEstado() {
        return estado;
    }

    public void setEstado(Estado estado) {
        this.estado = estado;
    }

    public LocalDateTime getFecha_pago() {
        return fecha_pago;
    }

    public void setFecha_pago(LocalDateTime fecha_pago) {
        this.fecha_pago = fecha_pago;
    }
    
}