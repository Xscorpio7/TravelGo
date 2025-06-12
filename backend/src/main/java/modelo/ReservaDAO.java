<<<<<<< HEAD
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
=======
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
    public boolean actualizarReserva(ReservaVO reservaVO){
        
        String sql ="UPDATE reserva SET fecha_reserva = ?, estado = ?";
                try{
                    Connection conn = BdConexion.getConexion();
                    PreparedStatement sentencia = conn.prepareStatement (sql);
                    
                                     
                    sentencia.setTimestamp(4, java.sql.Timestamp.valueOf(reservaVO.getFecha_reserva()));
                    sentencia.setString(2, reservaVO.getEstado().name());
                    int filasAfectadas = sentencia.executeUpdate();
                    operacion = (filasAfectadas > 0);


                }catch(Exception e){
                    System.out.println("Error al insertar usuario" + e.getMessage());
                    
                }
                return operacion;
    }
    public boolean eliminarReserva(ReservaVO reservaVO){
        
        String sql ="DELETE FROM reserva WHERE fecha_reserva = ? AND estado = ?";
                try{
                    Connection conn = BdConexion.getConexion();
                    PreparedStatement sentencia = conn.prepareStatement (sql);
                    
                                     
                    sentencia.setTimestamp(4, java.sql.Timestamp.valueOf(reservaVO.getFecha_reserva()));
                    sentencia.setString(2, reservaVO.getEstado().name());
                    int filasAfectadas = sentencia.executeUpdate();
                    operacion = (filasAfectadas > 0);

                }catch(Exception e){
                    System.out.println("Error al insertar usuario" + e.getMessage());
                    
                }
                return operacion;
    }
}
>>>>>>> 0c1dc06 (Subida de archivos faltante de backend)
