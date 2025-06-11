/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

/**
 *
 * @author Alejo
 */
public class CredencialesVO {
    private int id;
    private String correo;
    private String contraseña;
    private Tipo_usuario tipo_usuario;
    public enum Tipo_usuario{
        usuario,
        admin,
    }
    private boolean estado_activo=true;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getContraseña() {
        return contraseña;
    }

    public void setContraseña(String contraseña) {
        this.contraseña = contraseña;
    }

    public Tipo_usuario getTipo_usuario() {
        return tipo_usuario;
    }

    public void setTipo_usuario(Tipo_usuario tipo_usuario) {
        this.tipo_usuario = tipo_usuario;
    }

    public boolean isEstado_activo() {
        return estado_activo;
    }

    public void setEstado_activo(boolean estado_activo) {
        this.estado_activo = estado_activo;
    }
    
    
}
