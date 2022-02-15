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
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Luis
 */
public class Protocolo {
    public ResultSet rs;
    public ResultSet rsa;
    public ResultSet rsv;
    String output;
    ArrayList<String> tokens = new ArrayList<>();
    ArrayList<String> usuariosLogeados = new ArrayList<>();
    public String procesarInput(String input) throws IOException, SQLException{
            Conexion c = new Conexion();
            System.out.println(input);
            if(input.contains("LOGIN")){
                int aux1 = input.indexOf("#", 28);
                try {
                    rs = c.getConexion().executeQuery("SELECT * FROM usuario WHERE login='"+input.substring(27, aux1)+"';");
                    c.getConexion().close();
                    if(rs.next()){
                            if(input.substring(aux1+1, input.length()).equals(rs.getString("clave"))){
                                String token = new randomWordGen().generarPalabra();
                                output = ("PROTOCOLCRISTOBAY1.0#WELLCOME#"+input.substring(27, aux1)+"#WITH_TOKEN#"+token);
                                for(int h = 0;h<usuariosLogeados.size();h++){
                                    if(input.substring(27, aux1).equals(usuariosLogeados.get(h))){
                                        System.out.println("Cuenta en uso");
                                        output = "Cuenta en uso";
                                        return output;
                                    }
                                }
                                usuariosLogeados.add(input.substring(27, aux1));
                                System.out.println("Login correcto");
                                tokens.add(token);
                                for(int i = 0;i<tokens.size();i++){
                                    System.out.println(tokens.get(i));
                                }
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
                for(int i = 0;i<usuariosLogeados.size();i++){
                    String nombreArt = "";
                    String nombreUsu = "";
                    if(input.split("#")[2].equals(usuariosLogeados.get(i))&&input.split("#")[3].equals(tokens.get(i))){
                        rs = c.getConexion().executeQuery("SELECT * FROM subastar");
                        rsa = c.getConexion().executeQuery("SELECT * FROM articulo");
                        rsv  = c.getConexion().executeQuery("SELECT * FROM usuario");
                        c.getConexion().close();
                        int j = 0;
                        while(rs.next()){
                            j++;
                        }
                        rs.beforeFirst();
                        output = "PROTOCOLCRISTOBAY1.0#AUCTION_AVAILABLE#"+j;
                        while(rs.next()){
                            
                            rsa.beforeFirst();
                            while(rsa.next()){
                                if(rsa.getInt("id_articulo")==(rs.getInt("id_articulo"))){
                                    nombreArt = rsa.getString("nombre");
                                }
                            }
                            rsv.beforeFirst();
                            while(rsv.next()){
                                if(rsv.getInt("id_usuario")==(rs.getInt("id_usuario"))){
                                    nombreUsu = rsv.getString("nombre");
                                }
                            }
                            output = output+"#"+rs.getInt("id_articulo")+"@"+rs.getString("fecha_inicio")+"@"+rs.getString("fecha_fin")+"@"+rs.getString("estado")+"@"+nombreArt+"@"+nombreUsu+"@"+rs.getString("precio_salida")+3;
        //                    rsa.next();
        //                    rsv.next();
                        }
                        System.out.print(output);
                    }
                    else{
                        output = "Invalid Token";
                    }
                }
                
            }
        return output;
    }
}
