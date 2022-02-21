/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controlador;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Conexion {
        static Statement s;
    public Conexion(){
        try{
            s =DriverManager.getConnection("jdbc:mysql://localhost:3306/cristobay_db","root","root").createStatement();  
        }catch(SQLException e){
         
        }
    }
    public Statement getConexion(){
        return s; 
    }
}