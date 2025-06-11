/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controlador;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import modelo.UsuarioDAO;
import modelo.UsuarioVO;

/**
 *
 * @author Alejo
 */
@WebServlet(name = "UsuarioControlador", urlPatterns = {"/UsuarioControlador"})
public class UsuarioControlador extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            UsuarioDAO usuarioDAO = new UsuarioDAO();
            List<UsuarioVO> listaUsuarios=usuarioDAO.listaUsuarios();
            request.setAttribute("listaUsuarios",listaUsuarios);
            RequestDispatcher listar =request.getRequestDispatcher("vistas/listaUsuarios.jsp");
            listar.forward(request,response);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String operacion = request.getParameter("operacion");
        if ("agregar".equals(operacion)){
            try {
            String nombre_completo= request.getParameter("nombre_completo");
            String telefono= request.getParameter("telefono");
            String nacionalidad = request.getParameter("nacionalidad");
            
        } catch (Exception e) {
        }
        }
        
        
    }

   
   

}
