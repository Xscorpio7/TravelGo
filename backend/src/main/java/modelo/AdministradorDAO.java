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
public class AdministradorDAO {
    private boolean operacion=false;
    public boolean agregarAdministrador(AdministradorVO administradorVO){
        
        String sql ="INSERT INTO administradores(credencial_id, nombre, cargo) values(?, ?, ?)";
                try{
                    Connection conn = BdConexion.getConexion();
                    PreparedStatement sentencia = conn.prepareStatement (sql);
                    
                    sentencia.setInt(1, administradorVO.getCredencial_id());
                    sentencia.setString(2, administradorVO.getNombre());
                    sentencia.setString(3, administradorVO.getCargo());
                    sentencia.executeUpdate();
                    operacion =true;

                }catch(Exception e){
                    System.out.println("Error al insertar usuario" + e.getMessage());
                    
                }
                return operacion;
    }
     public boolean actualizarAdministrador(AdministradorVO administradorVO){
        
        String sql ="UPDATE administradores SET credencial_id = ?, nombre = ?, cargo = ?";
                try{
                    Connection conn = BdConexion.getConexion();
                    PreparedStatement sentencia = conn.prepareStatement (sql);
                    
                    sentencia.setInt(1, administradorVO.getCredencial_id());
                    sentencia.setString(2, administradorVO.getNombre());
                    sentencia.setString(3, administradorVO.getCargo());
                    int filasAfectadas = sentencia.executeUpdate();
                    operacion = (filasAfectadas > 0);

                }catch(Exception e){
                    System.out.println("Error al insertar usuario" + e.getMessage());
                    
                }
                return operacion;
    }
      public boolean eliminarAdministrador(AdministradorVO administradorVO){
        
        String sql ="DELETE FROM usuarios WHERE credencial_id = ? AND nombre = ? AND cargo = ?";
                try{
                    Connection conn = BdConexion.getConexion();
                    PreparedStatement sentencia = conn.prepareStatement (sql);
                    
                    sentencia.setInt(1, administradorVO.getCredencial_id());
                    sentencia.setString(2, administradorVO.getNombre());
                    sentencia.setString(3, administradorVO.getCargo());
                    int filasAfectadas = sentencia.executeUpdate();
                    operacion = (filasAfectadas > 0);

                }catch(Exception e){
                    System.out.println("Error al insertar usuario" + e.getMessage());
                    
                }
                return operacion;
    }
      public List<AdministradorVO> listaAdministrador(){
        List<AdministradorVO> listaAdministrador = new ArrayList<>();
        String sql ="Select * from administradores";
        
        try {
            Connection conn = BdConexion.getConexion();
            Statement sentencia = conn.createStatement();
            ResultSet resultado = sentencia.executeQuery(sql);
            
            
            while(resultado.next()){
                AdministradorVO administradorVO = new AdministradorVO();
                administradorVO.setCredencial_id(resultado.getInt("credencial_id"));
                administradorVO.setNombre(resultado.getString("nombre"));
                administradorVO.setCargo(resultado.getString("cargo"));
                
                listaAdministrador.add(administradorVO);
                
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return listaAdministrador;
    }
}
