/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package newpackage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;


public class RegistrarDAO {
    public void registrarUsuario(String nombre, String apellido, String correo, String contraseña, String telefono){
    String sql = "INSERT INTO datosUsuario (nombre,apellido,correo,contaseña,telefono) VALUES(?,?)";
    try(Connection conn=Conexion.getConexion();
           PreparedStatement stmt= conn.prepareStatement(sql)){
        stmt.setString(1,nombre);
        stmt.setString(2,correo);
        stmt.setString(3,contraseña);
        stmt.setString(4,telefono);
        stmt.setString(5,apellido);
        stmt.executeUpdate();

    }catch(SQLException e){
        e.printStackTrace();
    }
}
}
