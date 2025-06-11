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
import modelo.UsuarioVO.Genero;


/**
 *
 * @author Alejo
 */
public class UsuarioDAO {
    private boolean operacion=false;
    public boolean agregarUsuario(UsuarioVO usuarioVO){
        
        String sql ="INSERT INTO usuarios(nombre_completo, telefono, nacionalidad, fecha_nacimiento, genero) values(?, ?, ?, ?, ?)";
                try{
                    Connection conn = BdConexion.getConexion();
                    PreparedStatement sentencia = conn.prepareStatement (sql);
                    
                    sentencia.setString(1, usuarioVO.getNombre_completo());
                    sentencia.setString(2, usuarioVO.getTelefono());
                    sentencia.setString(3, usuarioVO.getNacionalidad());
                    sentencia.setDate(4, java.sql.Date.valueOf(usuarioVO.getFecha_nacimiento()));
                    sentencia.setString(5, usuarioVO.getGenero().name());
                    sentencia.executeUpdate();
                    operacion =true;

                }catch(Exception e){
                    System.out.println("Error al insertar usuario" + e.getMessage());
                    
                }
                return operacion;
    }
    
    public boolean actualizarUsuario(UsuarioVO usuarioVO){
        
        String sql ="UPDATE usuarios SET nombre_completo = ?, telefono = ?, nacionalidad = ?, fecha_nacimiento = ?, genero = ?";
                try{
                    Connection conn = BdConexion.getConexion();
                    PreparedStatement sentencia = conn.prepareStatement (sql);
                    
                    sentencia.setString(1, usuarioVO.getNombre_completo());
                    sentencia.setString(2, usuarioVO.getTelefono());
                    sentencia.setString(3, usuarioVO.getNacionalidad());
                    sentencia.setDate(4, java.sql.Date.valueOf(usuarioVO.getFecha_nacimiento()));
                    sentencia.setString(5, usuarioVO.getGenero().name());
                    int filasAfectadas = sentencia.executeUpdate();
                    operacion = (filasAfectadas > 0);

                }catch(Exception e){
                    System.out.println("Error al insertar usuario" + e.getMessage());
                    
                }
                return operacion;
    }
    public boolean eliminarUsuario(UsuarioVO usuarioVO){
        
        String sql ="DELETE FROM usuarios WHERE nombre_completo = ? AND telefono = ? AND nacionalidad = ? AND fecha_nacimiento = ? AND genero = ?";
                try{
                    Connection conn = BdConexion.getConexion();
                    PreparedStatement sentencia = conn.prepareStatement (sql);
                    
                    sentencia.setString(1, usuarioVO.getNombre_completo());
                    sentencia.setString(2, usuarioVO.getTelefono());
                    sentencia.setString(3, usuarioVO.getNacionalidad());
                    sentencia.setDate(4, java.sql.Date.valueOf(usuarioVO.getFecha_nacimiento()));
                    sentencia.setString(5, usuarioVO.getGenero().name());
                    int filasAfectadas = sentencia.executeUpdate();
                    operacion = (filasAfectadas > 0);

                }catch(Exception e){
                    System.out.println("Error al insertar usuario" + e.getMessage());
                    
                }
                return operacion;
    }
    
    public List<UsuarioVO> listaUsuarios(){
        List<UsuarioVO> listaUsuarios = new ArrayList<>();
        String sql ="Select * from usuarios";
        
        try {
            Connection conn = BdConexion.getConexion();
            Statement sentencia = conn.createStatement();
            ResultSet resultado = sentencia.executeQuery(sql);
            
            
            while(resultado.next()){
                UsuarioVO usuarioVO = new UsuarioVO();
                usuarioVO.setId(resultado.getInt("id"));
                usuarioVO.setNombre_completo(resultado.getString("nombre_completo"));
                usuarioVO.setTelefono(resultado.getString("telefono"));
                usuarioVO.setNacionalidad(resultado.getString("nacionalidad"));
                usuarioVO.setFecha_nacimiento(resultado.getDate("fecha_nacimiento").toLocalDate());
                String generoStr = resultado.getString("genero"); 
                usuarioVO.setGenero(Genero.valueOf(generoStr));
                listaUsuarios.add(usuarioVO);
                
                
                
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return listaUsuarios;
    }
}
