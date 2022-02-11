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
public class CargaArticulos {
    public ResultSet rs;
    ArrayList<Articulo> a;
    
    public CargaArticulos() throws SQLException{
        Conexion con = new Conexion();
        rs = con.getConexion().executeQuery("SELECT * FROM articulo");
        con.getConexion().close();
    }
    
    public ArrayList<Articulo> cargarArticulos() throws SQLException{
        a = new ArrayList<>();
        while(rs.next()){
            a.add(new Articulo(rs.getInt("id_articulo"),rs.getString("nombre"),rs.getString("descripcion"),rs.getString("imagen")));
        }
        return a;
    }
}
