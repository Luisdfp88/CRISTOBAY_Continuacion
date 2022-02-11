/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controlador;

import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import Controlador.*; 
import Vista.Ventana;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 *
 * @author Luis
 */
public class Modelo_Tabla{
    private DefaultTableModel modelo;
    ConexionServer cs;

    public TableModel cargarTabla() throws SQLException, IOException{
        cs = new ConexionServer();
        System.out.println(cs.respuestaLogin());
        System.out.println(cs.getPalabraSecreta(cs.respuestaLogin()));
        cs.pedirSubastasPorEstado("abierta");
        cs.getSubastas();
        String col[] = {"Usuario","Articulo","Fecha Inicio","Fecha Fin","Puja Actual"};
        modelo = new DefaultTableModel(col, 0);
        for(int i = 0;i<cs.getSubastas().size();i++){
        String obj[] = {"User",String.valueOf(cs.getSubastas().get(0).getCodProd()),cs.getSubastas().get(0).getFechaInicio(),cs.getSubastas().get(0).getFechaFin(),cs.getSubastas().get(0).getEstado()};
            modelo.addRow(obj);  
        }
        return modelo;
    }
    
}
