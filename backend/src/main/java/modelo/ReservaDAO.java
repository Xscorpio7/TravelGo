/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

import java.sql.Connection;
import java.sql.PreparedStatement;

/**
 *
 * @author Alejo
 */
public class ReservaDAO {
    private boolean operacion=false;
    public boolean agregarReserva(ReservaVO reservaVO){
        
        String sql ="INSERT INTO reservas(fecha_reserva, estado) values(?, ?)";
                try{
                    Connection conn = BdConexion.getConexion();
                    PreparedStatement sentencia = conn.prepareStatement (sql);
                    
                                     
                    sentencia.setTimestamp(4, java.sql.Timestamp.valueOf(reservaVO.getFecha_reserva()));
                    sentencia.setString(2, reservaVO.getEstado().name());
                    sentencia.executeUpdate();
                    operacion =true;

                }catch(Exception e){
                    System.out.println("Error al insertar usuario" + e.getMessage());
                    
                }
                return operacion;
    }
}
