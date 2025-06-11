/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

import java.time.LocalDateTime;

/**
 *
 * @author Alejo
 */
public class ReservaVO {
    private int id;
    private int usuario_id;
    private int viaje_id;
    private int alojamiento_id;
    private int transporte_id;
    private LocalDateTime  fecha_reserva;
    private Estado estado;
    public enum Estado{
        pendiente,
        confirmada,
        cancelada
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUsuario_id() {
        return usuario_id;
    }

    public void setUsuario_id(int usuario_id) {
        this.usuario_id = usuario_id;
    }

    public int getViaje_id() {
        return viaje_id;
    }

    public void setViaje_id(int viaje_id) {
        this.viaje_id = viaje_id;
    }

    public int getAlojamiento_id() {
        return alojamiento_id;
    }

    public void setAlojamiento_id(int alojamiento_id) {
        this.alojamiento_id = alojamiento_id;
    }

    public int getTransporte_id() {
        return transporte_id;
    }

    public void setTransporte_id(int transporte_id) {
        this.transporte_id = transporte_id;
    }

    public LocalDateTime getFecha_reserva() {
        return fecha_reserva;
    }

    public void setFecha_reserva(LocalDateTime fecha_reserva) {
        this.fecha_reserva = fecha_reserva;
    }

    public Estado getEstado() {
        return estado;
    }

    public void setEstado(Estado estado) {
        this.estado = estado;
    }

}
