/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controlador;

import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import Modelo.SubastaCln;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 *
 * @author Luis
 */
public class Modelo_Tabla{
    private DefaultTableModel modelo;
    public ConexionServer cs;
    ArrayList <SubastaCln> ar;
    String estado = "ALL";
    public Modelo_Tabla(ConexionServer cs) throws IOException{
        this.cs = cs;
    }

    public void setAr(ArrayList<SubastaCln> ar) {
        this.ar = ar;
    }
    
    
    
    public Modelo_Tabla(ConexionServer cs,String estado){
        this.cs = cs;
        this.estado = estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
    
    

    public TableModel Modelo() throws SQLException, IOException{
        System.out.println(cs.getPalabraSecreta(cs.getBuffer()));        
        String col[] = {"Usuario","Articulo","Fecha de Inicio","Fecha Fin","Estado","Puja Actual"};
        modelo = new DefaultTableModel(col, 0);
        for(int i = 0;i<ar.size();i++){
        String obj[] = {ar.get(i).getNombreVend(),ar.get(i).getNombreProd(),ar.get(i).getFechaInicio(),ar.get(i).getFechaFin(),ar.get(i).getEstado(),String.valueOf(ar.get(i).getPrecioActual())};
            modelo.addRow(obj);  
        }
        return modelo;
    }
    
}
