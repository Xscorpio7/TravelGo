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
public class TransporteDAO {

    private boolean operacion = false;

    public boolean agregarTransporte(TransporteVO transporteVO) {

        String sql = "INSERT INTO transporte(nombre_completo, telefono, nacionalidad, fecha_nacimiento, genero) values(?, ?, ?, ?, ?)";
        try {
            Connection conn = BdConexion.getConexion();
            PreparedStatement sentencia = conn.prepareStatement(sql);

            sentencia.setString(1, transporteVO.getTipo().name());
            sentencia.setString(2, transporteVO.getProveedor());
            sentencia.setString(3, transporteVO.getNumero_trasnporte());
            sentencia.setTimestamp(4, java.sql.Timestamp.valueOf(transporteVO.getSalida()));
            sentencia.setTimestamp(5, java.sql.Timestamp.valueOf(transporteVO.getLlegada()));
            sentencia.setString(6, transporteVO.getOrigen());
            sentencia.setString(7, transporteVO.getDestino());

            sentencia.executeUpdate();
            operacion = true;

        } catch (Exception e) {
            System.out.println("Error al insertar usuario" + e.getMessage());

        }
        return operacion;
    }

    public boolean actualiarTransporte(TransporteVO transporteVO) {

        String sql = "UPDATE transporte SET tipo = ?, proveedor = ?, numero_transporte = ?, salida = ?, llegada = ?, origen = ?, destino = ?";
        try {
            Connection conn = BdConexion.getConexion();
            PreparedStatement sentencia = conn.prepareStatement(sql);

            sentencia.setString(1, transporteVO.getTipo().name());
            sentencia.setString(2, transporteVO.getProveedor());
            sentencia.setString(3, transporteVO.getNumero_trasnporte());
            sentencia.setTimestamp(4, java.sql.Timestamp.valueOf(transporteVO.getSalida()));
            sentencia.setTimestamp(5, java.sql.Timestamp.valueOf(transporteVO.getLlegada()));
            sentencia.setString(6, transporteVO.getOrigen());
            sentencia.setString(7, transporteVO.getDestino());
            int filasAfectadas = sentencia.executeUpdate();
            operacion = (filasAfectadas > 0);

            sentencia.executeUpdate();
            operacion = true;

        } catch (Exception e) {
            System.out.println("Error al insertar usuario" + e.getMessage());

        }
        return operacion;
    }

    public boolean eliminarTransporte(TransporteVO transporteVO) {

        String sql = "DELETE FROM transporte WHERE tipo = ?, proveedor = ?, numero_transporte = ?, salida = ?, llegada = ?, origen = ?, destino = ?";
        try {
            Connection conn = BdConexion.getConexion();
            PreparedStatement sentencia = conn.prepareStatement(sql);

            sentencia.setString(1, transporteVO.getTipo().name());
            sentencia.setString(2, transporteVO.getProveedor());
            sentencia.setString(3, transporteVO.getNumero_trasnporte());
            sentencia.setTimestamp(4, java.sql.Timestamp.valueOf(transporteVO.getSalida()));
            sentencia.setTimestamp(5, java.sql.Timestamp.valueOf(transporteVO.getLlegada()));
            sentencia.setString(6, transporteVO.getOrigen());
            sentencia.setString(7, transporteVO.getDestino());
            int filasAfectadas = sentencia.executeUpdate();
            operacion = (filasAfectadas > 0);

            sentencia.executeUpdate();
            operacion = true;

        } catch (Exception e) {
            System.out.println("Error al insertar usuario" + e.getMessage());

        }
        return operacion;
    }
}
