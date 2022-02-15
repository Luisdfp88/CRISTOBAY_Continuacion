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
public class SubastaCln {
    int codProd;
    String fechaInicio;
    String fechaFin;
    String estado;
    String nombreProd;
    String nombreVend;
    int PrecioInicial;
    int PrecioActual;
    public SubastaCln(int c, String fi, String ff, String e, String np, String nv, int pi, int pa){
        codProd = c;
        fechaInicio = fi;
        fechaFin = ff;
        estado = e;
        nombreProd = np;
        nombreVend = nv;
        PrecioInicial = pi;
        PrecioActual = pa;
    }

    public String getNombreProd() {
        return nombreProd;
    }

    public void setNombreProd(String nombreProd) {
        this.nombreProd = nombreProd;
    }

    public String getNombreVend() {
        return nombreVend;
    }

    public void setNombreVend(String nombreVend) {
        this.nombreVend = nombreVend;
    }

    public int getPrecioInicial() {
        return PrecioInicial;
    }

    public void setPrecioInicial(int PrecioInicial) {
        this.PrecioInicial = PrecioInicial;
    }

    public int getPrecioActual() {
        return PrecioActual;
    }

    public void setPrecioActual(int PrecioActual) {
        this.PrecioActual = PrecioActual;
    }

    
    public int getCodProd() {
        return codProd;
    }

    public void setCodProd(int codProd) {
        this.codProd = codProd;
    }

    public String getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(String fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public String getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(String fechaFin) {
        this.fechaFin = fechaFin;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
    
}
