/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package newpackage;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "GuardarUsuarios", urlPatterns = {"/GuardarUsuarios"})
public class GuardarUsuarios extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String nombre = request.getParameter("nombre");
        String correo = request.getParameter("correo");
        String contrasena = request.getParameter("contrasena");
        String pais = request.getParameter("pais");
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conexion = DriverManager.getConnection("jdbc:mysql://localhost:3306/principal", "root", "");
            PreparedStatement sentencia = conexion.prepareStatement(
                    "INSERT INTO usuarios(nombre, correo, contrasena, pais) VALUES (?, ?, ?, ?)");

            sentencia.setString(1, nombre);
            sentencia.setString(2, correo);
            sentencia.setString(3, contrasena);
            sentencia.setString(4, pais);
            sentencia.executeUpdate();

            sentencia.close();
            conexion.close();
            request.setAttribute("nombreUsuario", nombre);
            RequestDispatcher redireccion = request.getRequestDispatcher("confirmación.jsp");
            redireccion.forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().println("Error" + e.toString());

        }
    }

}
