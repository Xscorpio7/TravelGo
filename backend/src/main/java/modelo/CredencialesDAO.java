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
public class CredencialesDAO {
    private boolean operacion=false;
    public boolean agregarCredenciales(CredencialesVO credencialesVO){
        
        String sql ="INSERT INTO credenciales(correo, contrasena, tipo_usuario, esta_activo) values(?, ?, ?, ?)";
                try{
                    Connection conn = BdConexion.getConexion();
                    PreparedStatement sentencia = conn.prepareStatement (sql);
                    
                    sentencia.setString(1, credencialesVO.getCorreo());
                    sentencia.setString(2, credencialesVO.getContraseña());
                    sentencia.setString(3, credencialesVO.getTipo_usuario().name());
                    sentencia.setBoolean(5, credencialesVO.isEstado_activo());
                    sentencia.executeUpdate();
                    operacion =true;

                }catch(Exception e){
                    System.out.println("Error al insertar usuario" + e.getMessage());
                    
                }
                return operacion;
}
     public boolean actualizarCredenciales(CredencialesVO credencialesVO){
        try{
        String sql ="UPDATE credenciales SET correo= ?, contrasena = ?, tipo_usuario = ?, esta_activo = ?";
                    Connection conn = BdConexion.getConexion();
                    PreparedStatement sentencia = conn.prepareStatement (sql);
                    
                    sentencia.setString(1, credencialesVO.getCorreo());
                    sentencia.setString(2, credencialesVO.getContraseña());
                    sentencia.setString(3, credencialesVO.getTipo_usuario().name());
                    sentencia.setBoolean(5, credencialesVO.isEstado_activo());
                    int filasAfectadas = sentencia.executeUpdate();
                    operacion = (filasAfectadas > 0);

                }catch(Exception e){
                    System.out.println("Error al insertar usuario" + e.getMessage());
                    
                }
                return operacion;
}
      public boolean eliminarCredenciales(CredencialesVO credencialesVO){
        
        String sql ="DELETE FROM usuarios WHERE correo = ? AND contrasena = ? AND tipo_usuario = ? AND esta_activo = ?";
                try{
                    Connection conn = BdConexion.getConexion();
                    PreparedStatement sentencia = conn.prepareStatement (sql);
                    
                    sentencia.setString(1, credencialesVO.getCorreo());
                    sentencia.setString(2, credencialesVO.getContraseña());
                    sentencia.setString(3, credencialesVO.getTipo_usuario().name());
                    sentencia.setBoolean(5, credencialesVO.isEstado_activo());
                    int filasAfectadas = sentencia.executeUpdate();
                    operacion = (filasAfectadas > 0);

                }catch(Exception e){
                    System.out.println("Error al insertar usuario" + e.getMessage());
                    
                }
                return operacion;
}
      
}
