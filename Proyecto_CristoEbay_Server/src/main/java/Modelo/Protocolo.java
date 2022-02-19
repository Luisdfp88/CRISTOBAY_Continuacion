/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Modelo;

import Controlador.Conexion;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;

/**
 *
 * @author Luis
 */
public class Protocolo {
    public ResultSet rs;
    public ResultSet rsa;
    public ResultSet rsv;
    public ResultSet rsp;
    int lim = 255;
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
                                        output = "PROTOCOLCRISTOBAY1.0#ERROR#BAD_LOGIN";
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
                    int pujaAlta = 0;
                    if(input.split("#")[2].equals(usuariosLogeados.get(i))&&input.split("#")[3].equals(tokens.get(i))){
                        String stra = "";
                        if(input.split("#")[1].split("_")[2].equals("ALL")){
                            rs = c.getConexion().executeQuery("SELECT * FROM subastar");
                        }else{
                            switch (input.split("#")[1].split("_")[2]) {
                                case "OPEN":
                                    stra="Abierta";
                                    break;
                                case "CLOSEDBYBUY":
                                    stra="Cerrada por compra";
                                    break;
                                case "CLOSEDBYDROP":
                                    stra="Cerrada por eliminación";
                                    break;
                                case "CLOSEDBYTIME":
                                    stra="Cerrada por tiempo";
                                    break;
                                case "CREATED":
                                    stra="creada";
                                    break;
                                default:
                                    break;
                            }
                            rs = c.getConexion().executeQuery("SELECT * FROM subastar WHERE estado = '"+stra+"'");
                        }
                        
                        rsa = c.getConexion().executeQuery("SELECT * FROM articulo");
                        rsv  = c.getConexion().executeQuery("SELECT * FROM usuario");
                        rsp = c.getConexion().executeQuery("SELECT * FROM pujar");
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
                            rsp.beforeFirst();
                            pujaAlta=0;
                            while(rsp.next()){
                                if(rsp.getInt("id_articulo")==rs.getInt("id_articulo")){
                                    if(rsp.getInt("cantidad_pujada")>pujaAlta){
                                        pujaAlta = rsp.getInt("cantidad_pujada");
                                    }
                                }
                            }
                            output = output+"#"+rs.getInt("id_articulo")+"@"+rs.getString("fecha_inicio")+"@"+rs.getString("fecha_fin")+"@"+rs.getString("estado")+"@\""+nombreArt+"\"@"+nombreUsu+"@"+rs.getString("precio_salida")+"@"+pujaAlta;
        //                    rsa.next();
        //                    rsv.next();
                        }
                        System.out.print(output);
                    }
                    else{
                        output = "PROTOCOLCRISTOBAY1.0#ERROR#AUCTION_NOT_AVAILABLE";
                    }
                }
                
            }else if(input.contains("GET_SUBASTA#")){
                rsa = c.getConexion().executeQuery("SELECT * FROM articulo WHERE id_articulo = '"+input.split("#")[4].split("@")[0]+"'");
                rs = c.getConexion().executeQuery("SELECT * FROM subastar WHERE id_articulo = '"+input.split("#")[4].split("@")[0]+"'");
                rsa.first();
                rs.first();
                String str = ("C:"+rsa.getString("imagen"));
                System.out.println(rsa.getString("imagen"));
                File file = new File(str);
                byte[] fileContent = FileUtils.readFileToByteArray(file);
                output = "PROTOCOLCRISTOBAY1.0#GET_SUBASTA#"+rsa.getInt("id_articulo")+"@"+rs.getString("fecha_inicio")+"@"+rs.getString("fecha_fin")+"#"+rsa.getString("descripcion")+"#"+"jpg"+"#"+Base64.getEncoder().encodeToString(fileContent).length();
                System.out.println(output);
            }else if(input.contains("PREPARED_TO_RECEIVE")){
                //TODO Enviar cada paquete
                rsa = c.getConexion().executeQuery("SELECT * FROM articulo WHERE id_articulo = '"+input.split("#")[4]+"'");
                rsa.first();
                String str = ("C:"+rsa.getString("imagen"));
                File file = new File(str);
                int g = Integer.valueOf(input.split("#")[6])-1;
                int h = Integer.valueOf(input.split("#")[7]);
                int cadDePaso = lim*Integer.valueOf(input.split("#")[7]);
                int tamanoUltPaq;
                byte[] fileContent = FileUtils.readFileToByteArray(file);
                String fotoenBase64 = Base64.getEncoder().encodeToString(fileContent);
                if(g == h){
                    tamanoUltPaq  = Integer.valueOf(input.split("#")[8]);
                    System.out.println(tamanoUltPaq);
                    output = "PROTOCOLCRISTOBAY1.0#"+input.split("#")[4]+"#"+fotoenBase64.substring(cadDePaso,cadDePaso+tamanoUltPaq)+"#"+input.split("#")[7];
                }else{
                    output = "PROTOCOLCRISTOBAY1.0#"+input.split("#")[4]+"#"+fotoenBase64.substring(cadDePaso,cadDePaso+lim)+"#"+input.split("#")[7];
                }
                
                
            }
        return output;
    }
}
