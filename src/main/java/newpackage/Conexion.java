/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package newpackage;

import java.sql.Connection;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;



public class Conexion {
    private static final String URL ="jdbc:mysql://localhost:3306/principal";
    private static final String USUARIO ="root";
    private static final String CONTRASENA ="";
    

    public static Connection getConexion() throws SQLException {
        return DriverManager.getConnection(URL,USUARIO,CONTRASENA);
    }
    
}
