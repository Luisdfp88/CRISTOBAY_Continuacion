/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Modelo;

import Controlador.Conexion;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 *
 * @author Luis
 */
public class CargaSubastas {
    public ResultSet rs;
    ArrayList<Subasta> a;
    
    public CargaSubastas() throws SQLException{
        Conexion con = new Conexion();
        rs = con.getConexion().executeQuery("SELECT * FROM subastar");
        con.getConexion().close();
    }
    
    public ArrayList<Subasta> cargarSubastas() throws SQLException{
        a = new ArrayList<>();
        while(rs.next()){
            a.add(new Subasta(rs.getInt("id_usuario"),rs.getInt("id_articulo"),rs.getString("fecha_inicio"),rs.getString("fecha_fin"),rs.getString("estado"),rs.getInt("precio_salida")));
        }
        return a;
    }
}
