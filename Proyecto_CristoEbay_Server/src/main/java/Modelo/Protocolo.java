/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Modelo;

import Controlador.Conexion;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Luis
 */
public class Protocolo {
    public ResultSet rs;
    public Socket sc;
    PrintWriter out;
    String output;
    public String procesarInput(String input) throws IOException, SQLException{
        System.out.println("AQUiiiiiii");
            Conexion c = new Conexion();
            System.out.println(input);
            if(input.contains("LOGIN")){
                int aux1 = input.indexOf("#", 28);
                try {
                    rs = c.getConexion().executeQuery("SELECT * FROM usuario WHERE login='"+input.substring(27, aux1)+"';");
                    c.getConexion().close();
                    sc = new Socket("localhost",6666);
                    out = new PrintWriter(sc.getOutputStream(), true);
                    if(rs.next()){
                        if(input.substring(aux1+1, input.length()).equals(rs.getString("clave"))){
                            output = ("PROTOCOLCRISTOBAY1.0#WELLCOME#"+input.substring(27, aux1)+"#WITH_TOKEN#"+new randomWordGen().generarPalabra());
                            System.out.println("Login correcto");

                        }else{
                            System.out.println(input.substring(27, aux1+1));
                            output = ("PROTOCOLCRISTOBAY1.0#ERROR#BAD_LOGIN");
                            System.out.println("Contraseña incorrecta");

                        }
                    }else{
                        System.out.println(input.substring(27, aux1+1));
                        output = "Usuario no existe";
                        System.out.println("No users");
                    }
                } catch (SQLException ex) {
                    Logger.getLogger(Protocolo.class.getName()).log(Level.SEVERE, null, ex);
                }
            }else if(input.contains("GET_SUBASTAS")){
                rs = c.getConexion().executeQuery("SELECT * FROM subastar");
                c.getConexion().close();
                sc = new Socket("localhost",6666);
                out = new PrintWriter(sc.getOutputStream(), true);
                int j = 0;
                while(rs.next()){
                    j++;
                }
                String str = "PROTOCOLCRISTOBAY1.0#AUCTION_AVAILABLE#"+j;
                while(rs.next()){
                    str = str+"#"+rs.getString("id_articulo")+"@"+rs.getString("fecha_inicio")+"@"+rs.getString("fecha_fin")+"@"+rs.getString("estado");
                }
                
                output = str;
            }
        return output;
    }
}
