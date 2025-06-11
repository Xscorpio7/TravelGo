/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Alejo
 */
public class AlojamientoDAO {
    private boolean operacion=false;
    public boolean agregarAlojamiento(AlojamientoVO alojamientoVO){
        
        String sql ="INSERT INTO alojamientos(nombre, direccion, ciudad, tipo, capacidad, precio_noche, ) values(?, ?, ?, ?, ?, ?)";
                try{
                    Connection conn = BdConexion.getConexion();
                    PreparedStatement sentencia = conn.prepareStatement (sql);
                    
                    sentencia.setString(1, alojamientoVO.getNombre());
                    sentencia.setString(2, alojamientoVO.getDireccion());
                    sentencia.setString(3, alojamientoVO.getCiudad());
                    sentencia.setString(4, alojamientoVO.getTipo().name());
                    sentencia.setInt(5, alojamientoVO.getCapacidad());
                    sentencia.setDouble(6, alojamientoVO.getPrecio_noche());
                    sentencia.executeUpdate();
                    operacion =true;

                }catch(Exception e){
                    System.out.println("Error al insertar usuario" + e.getMessage());
                    
                }
                return operacion;
    }
    
   public boolean actualizarAlojamiento(AlojamientoVO alojamientoVO){
        
        String sql ="UPDATE alojamientos SET nombre = ?, direccion = ?, direccion = ?, tipo = ?, capacidad = ? precio_noche = ?";
                try{
                    Connection conn = BdConexion.getConexion();
                    PreparedStatement sentencia = conn.prepareStatement (sql);
                    
                    sentencia.setString(1, alojamientoVO.getNombre());
                    sentencia.setString(2, alojamientoVO.getDireccion());
                    sentencia.setString(3, alojamientoVO.getCiudad());
                    sentencia.setString(4, alojamientoVO.getTipo().name());
                    sentencia.setInt(5, alojamientoVO.getCapacidad());
                    sentencia.setDouble(6, alojamientoVO.getPrecio_noche());
                    int filasAfectadas = sentencia.executeUpdate();
                    operacion = (filasAfectadas > 0);

                }catch(Exception e){
                    System.out.println("Error al insertar usuario" + e.getMessage());
                    
                }
                return operacion;
    }
   public boolean eliminarAlojamiento(AlojamientoVO alojamientoVO){
        
        String sql ="DELETE FROM alojamientos WHERE nombre = ? AND direccion = ? AND ciudad = ? AND tipo = ? AND capacidad = ? AND precio_noche = ?";
                try{
                    Connection conn = BdConexion.getConexion();
                    PreparedStatement sentencia = conn.prepareStatement (sql);
                    
                    sentencia.setString(1, alojamientoVO.getNombre());
                    sentencia.setString(2, alojamientoVO.getDireccion());
                    sentencia.setString(3, alojamientoVO.getCiudad());
                    sentencia.setString(4, alojamientoVO.getTipo().name());
                    sentencia.setInt(5, alojamientoVO.getCapacidad());
                    sentencia.setDouble(6, alojamientoVO.getPrecio_noche());
                    int filasAfectadas = sentencia.executeUpdate();
                    operacion = (filasAfectadas > 0);


                }catch(Exception e){
                    System.out.println("Error al insertar usuario" + e.getMessage());
                    
                }
                return operacion;
    }
}
