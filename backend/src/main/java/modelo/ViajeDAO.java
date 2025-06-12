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
public class ViajeDAO {
    private boolean operacion=false;
    public boolean agregarViaje(ViajeVO viajeVO){
        
        String sql ="INSERT INTO viajes(titulo, descripcion, destino, fecha_inicio, fecha_fin, precio, cupos_disponibles) values(?, ?, ?, ?, ?, ?, ?)";
                try{
                    Connection conn = BdConexion.getConexion();
                    PreparedStatement sentencia = conn.prepareStatement (sql);
                    
                    sentencia.setString(1, viajeVO.getTitulo());
                    sentencia.setString(2, viajeVO.getDescripcion());
                    sentencia.setString(3, viajeVO.getDestino());                  
                    sentencia.setTimestamp(4, java.sql.Timestamp.valueOf(viajeVO.getFecha_inicio()));
                    sentencia.setTimestamp(5, java.sql.Timestamp.valueOf(viajeVO.getFecha_fin()));
                    sentencia.setDouble(6,viajeVO.getPrecio());
                    sentencia.setInt(7, viajeVO.getCupos_disponibles());
                    sentencia.executeUpdate();
                    operacion =true;

                }catch(Exception e){
                    System.out.println("Error al insertar usuario" + e.getMessage());
                    
                }
                return operacion;
    }
    public boolean actualizarViaje(ViajeVO viajeVO){
        
        String sql ="UPDATE pagos SET titulo = ?, descripcion = ?, destino = ?, fecha_inicio = ?, fecha_fin = ?, precio = ?, cupos_disponibles = ?";
                try{
                    Connection conn = BdConexion.getConexion();
                    PreparedStatement sentencia = conn.prepareStatement (sql);
                    
                    sentencia.setString(1, viajeVO.getTitulo());
                    sentencia.setString(2, viajeVO.getDescripcion());
                    sentencia.setString(3, viajeVO.getDestino());                  
                    sentencia.setTimestamp(4, java.sql.Timestamp.valueOf(viajeVO.getFecha_inicio()));
                    sentencia.setTimestamp(5, java.sql.Timestamp.valueOf(viajeVO.getFecha_fin()));
                    sentencia.setDouble(6,viajeVO.getPrecio());
                    sentencia.setInt(7, viajeVO.getCupos_disponibles());
                    int filasAfectadas = sentencia.executeUpdate();
                    operacion = (filasAfectadas > 0);

                }catch(Exception e){
                    System.out.println("Error al insertar usuario" + e.getMessage());
                    
                }
                return operacion;
    }
    public boolean elimanrViaje(ViajeVO viajeVO){
        
        String sql ="DELETE FROM pagos WHERE titulo = ?, descripcion = ?, destino = ?, fecha_inicio = ?, fecha_fin = ?, precio = ?, cupos_disponibles = ?";
                try{
                    Connection conn = BdConexion.getConexion();
                    PreparedStatement sentencia = conn.prepareStatement (sql);
                    
                    sentencia.setString(1, viajeVO.getTitulo());
                    sentencia.setString(2, viajeVO.getDescripcion());
                    sentencia.setString(3, viajeVO.getDestino());                  
                    sentencia.setTimestamp(4, java.sql.Timestamp.valueOf(viajeVO.getFecha_inicio()));
                    sentencia.setTimestamp(5, java.sql.Timestamp.valueOf(viajeVO.getFecha_fin()));
                    sentencia.setDouble(6,viajeVO.getPrecio());
                    sentencia.setInt(7, viajeVO.getCupos_disponibles());
                    int filasAfectadas = sentencia.executeUpdate();
                    operacion = (filasAfectadas > 0);

                }catch(Exception e){
                    System.out.println("Error al insertar usuario" + e.getMessage());
                    
                }
                return operacion;
    }
}
