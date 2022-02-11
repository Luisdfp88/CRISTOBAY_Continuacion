/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Modelo;

/**
 *
 * @author Luis
 */
public class Subasta {
    public int id_usuario;
    public int id_articulo;
    public String fecha_inicio;
    public String fecha_fin;
    public String estado;
    public int precio_salida;

    public Subasta(){
        
    }
    
    public Subasta(int idu, int ida, String fein, String fefi, String est, int prsa){
        this.id_usuario = idu;
        this.id_articulo = ida;
        this.fecha_inicio = fein;
        this.fecha_fin = fefi;
        this.estado = est;
        this.precio_salida = prsa;
        
    }
    
    public int getId_usuario() {
        return id_usuario;
    }

    public void setId_usuario(int id_usuario) {
        this.id_usuario = id_usuario;
    }

    public int getId_articulo() {
        return id_articulo;
    }

    public void setId_articulo(int id_articulo) {
        this.id_articulo = id_articulo;
    }

    public String getFecha_inicio() {
        return fecha_inicio;
    }

    public void setFecha_inicio(String fecha_inicio) {
        this.fecha_inicio = fecha_inicio;
    }

    public String getFecha_fin() {
        return fecha_fin;
    }

    public void setFecha_fin(String fecha_fin) {
        this.fecha_fin = fecha_fin;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public int getPrecio_salida() {
        return precio_salida;
    }

    public void setPrecio_salida(int precio_salida) {
        this.precio_salida = precio_salida;
    }
    
    
    
}
