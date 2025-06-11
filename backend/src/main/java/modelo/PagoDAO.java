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
public class PagoDAO {
    private boolean operacion=false;
    public boolean agregarPago(PagoVO pagoVO){
        
        String sql ="INSERT INTO pagos(metodo_pago, monto, estado, fecha_pago, ) values(?, ?, ?, ?)";
                try{
                    Connection conn = BdConexion.getConexion();
                    PreparedStatement sentencia = conn.prepareStatement (sql);
                    
                    sentencia.setString(1, pagoVO.getMetodo_pago().name());
                    sentencia.setDouble(2, pagoVO.getMonto());
                    sentencia.setString(3, pagoVO.getEstado().name());
                    sentencia.setTimestamp(4, java.sql.Timestamp.valueOf(pagoVO.getFecha_pago()));

                    sentencia.executeUpdate();
                    operacion =true;

                }catch(Exception e){
                    System.out.println("Error al insertar usuario" + e.getMessage());
                    
                }
                return operacion;
    }
    public boolean actualizarPago(PagoVO pagoVO){
        
        String sql ="UPDATE pagos SET metodo_pago = ?, monto = ?, estado = ?, fecha_pago = ?";
                try{
                    Connection conn = BdConexion.getConexion();
                    PreparedStatement sentencia = conn.prepareStatement (sql);
                    
                    sentencia.setString(1, pagoVO.getMetodo_pago().name());
                    sentencia.setDouble(2, pagoVO.getMonto());
                    sentencia.setString(3, pagoVO.getEstado().name());
                    sentencia.setTimestamp(4, java.sql.Timestamp.valueOf(pagoVO.getFecha_pago()));

                    int filasAfectadas = sentencia.executeUpdate();
                    operacion = (filasAfectadas > 0);
                    
                }catch(Exception e){
                    System.out.println("Error al insertar usuario" + e.getMessage());
                    
                }
                return operacion;
    }
    public boolean eliminarPagos(PagoVO pagoVO){
        
        String sql ="DELETE FROM pagos WHERE metodo_pago = ? AND monto = ? AND estado = ? AND fecha_pago = ?";
                try{
                    Connection conn = BdConexion.getConexion();
                    PreparedStatement sentencia = conn.prepareStatement (sql);
                    
                    sentencia.setString(1, pagoVO.getMetodo_pago().name());
                    sentencia.setDouble(2, pagoVO.getMonto());
                    sentencia.setString(3, pagoVO.getEstado().name());
                    sentencia.setTimestamp(4, java.sql.Timestamp.valueOf(pagoVO.getFecha_pago()));
                    int filasAfectadas = sentencia.executeUpdate();
                    operacion = (filasAfectadas > 0);

                }catch(Exception e){
                    System.out.println("Error al insertar usuario" + e.getMessage());
                    
                }
                return operacion;
    }
}
