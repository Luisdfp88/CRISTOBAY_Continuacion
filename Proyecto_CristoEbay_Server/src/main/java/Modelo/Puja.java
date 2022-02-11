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
public class Puja {
    public int id_usuario;
    public int id_articulo;
    public String fecha_puja;
    public String fecha_inicio;
    public String fecha_fin;
    public int cantidad_pujada;

    public Puja(){
        
    }
    
    public Puja(int idu, int ida, String fepu, String fein, String fefi, int capu){
        this.id_usuario = idu;
        this.id_articulo = ida;
        this.fecha_puja = fepu;
        this.fecha_inicio = fein;
        this.fecha_fin = fefi;
        this.cantidad_pujada = capu;
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

    public String getFecha_puja() {
        return fecha_puja;
    }

    public void setFecha_puja(String fecha_puja) {
        this.fecha_puja = fecha_puja;
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

    public int getCantidad_pujada() {
        return cantidad_pujada;
    }

    public void setCantidad_pujada(int cantidad_pujada) {
        this.cantidad_pujada = cantidad_pujada;
    }
    
    
    
}
