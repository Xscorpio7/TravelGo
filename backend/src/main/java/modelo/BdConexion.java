/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author Alejo
 */
public class BdConexion {
    private static final String url="jdbc:mysql://localhost:3306/travelgo_bd";
    private static final String user="root";
    private static final String password="";

    public static Connection getConexion() throws ClassNotFoundException, SQLException{
        Class.forName("com.mysql.jdbc.Drive");
        Connection conn= DriverManager.getConnection("url, user, password");
        System.out.println("Conexion Ok");
        return  conn;
    }
}
