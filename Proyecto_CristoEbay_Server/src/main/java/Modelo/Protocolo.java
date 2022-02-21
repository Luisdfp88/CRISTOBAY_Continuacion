/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Modelo;

import Controlador.Conexion;
import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
    public Conexion c = new Conexion();
    ArrayList<Usuario> usuarios= new ArrayList<>();
    ArrayList<Subasta> subastas= new ArrayList<>();
    ArrayList<Articulo> articulos= new ArrayList<>();
    ArrayList<Puja> pujas= new ArrayList<>();
    int lim = 255;
    String output;
    ArrayList<String> tokens = new ArrayList<>();
    ArrayList<String> usuariosLogeados = new ArrayList<>();
    public String procesarInput(String input) throws IOException, SQLException{ 
        System.out.println(input);
        if(input.contains("LOGIN")){
            int aux1 = input.indexOf("#", 28);
            try {
                rs = c.getConexion().executeQuery("SELECT * FROM usuario WHERE login='"+input.substring(27, aux1)+"';");
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
                    int j = 0;
                    if(input.split("#")[1].split("_")[2].equals("ALL")){
                        rs = c.getConexion().executeQuery("SELECT * FROM subastar");
                        while(rs.next()){
                            j++;
                        }
                        rs = c.getConexion().executeQuery("SELECT * FROM subastar");
                        subastas.clear();
                        while(rs.next()){
                            subastas.add(new Subasta(rs.getInt("id_usuario"),rs.getInt("id_articulo"),rs.getString("fecha_inicio"),rs.getString("fecha_fin"),rs.getString("estado"),rs.getInt("precio_salida")));
                        }
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
                        while(rs.next()){
                            j++;
                        }
                        subastas.clear();
                        rs = c.getConexion().executeQuery("SELECT * FROM subastar WHERE estado = '"+stra+"'");
                        while(rs.next()){
                            subastas.add(new Subasta(rs.getInt("id_usuario"),rs.getInt("id_articulo"),rs.getString("fecha_inicio"),rs.getString("fecha_fin"),rs.getString("estado"),rs.getInt("precio_salida")));
                        }
                    }

                    rs = c.getConexion().executeQuery("SELECT * FROM articulo");
                    articulos.clear();
                    while(rs.next()){
                        articulos.add(new Articulo(rs.getInt("id_articulo"),rs.getString("nombre"),rs.getString("descripcion"),rs.getString("imagen")));
                    }
                    usuarios.clear();
                    rs  = c.getConexion().executeQuery("SELECT * FROM usuario");
                    while(rs.next()){
                        usuarios.add(new Usuario(rs.getInt("id_usuario"),rs.getString("login"),rs.getString("nombre")));
                    }
                    pujas.clear();
                    rs = c.getConexion().executeQuery("SELECT * FROM pujar");
                    while(rs.next()){
                        pujas.add(new Puja(rs.getInt("id_usuario"),rs.getInt("id_articulo"),rs.getString("fecha_y_hora"),rs.getString("fecha_inicio"),rs.getString("fecha_fin"),rs.getInt("cantidad_pujada")));
                    }
                    
                    output = "PROTOCOLCRISTOBAY1.0#AUCTION_AVAILABLE#"+j;
                    for(int h = 0;h<subastas.size();h++){
                        for(int k = 0;k<articulos.size();k++){
                            if(articulos.get(k).getId_articulo()==subastas.get(h).getId_articulo()){
                                nombreArt = articulos.get(k).getNombre();
                            }
                        }
                        for(int l = 0;l<usuarios.size();l++){
                            if(usuarios.get(l).getId_usuario()==subastas.get(h).getId_usuario()){
                                nombreUsu = usuarios.get(l).getNombre();
                            }
                        }
                        pujaAlta = 0;
                        for(int m = 0;m<pujas.size();m++){
                            if(pujas.get(m).getId_articulo()==subastas.get(h).getId_articulo()){
                                if(pujas.get(m).getCantidad_pujada()>pujaAlta){
                                    pujaAlta = pujas.get(m).getCantidad_pujada();
                                }
                            }
                        }
                        output = output+"#"+subastas.get(h).getId_articulo()+"@"+subastas.get(h).getFecha_inicio()+"@"+subastas.get(h).getFecha_fin()+"@"+subastas.get(h).getEstado()+"@\""+nombreArt+"\"@"+nombreUsu+"@"+subastas.get(h).getPrecio_salida()+"@"+pujaAlta;
                    }
                    System.out.print(output);
                }
                else{
                    output = "PROTOCOLCRISTOBAY1.0#ERROR#AUCTION_NOT_AVAILABLE";
                }
            }

        }else if(input.contains("GET_SUBASTA#")){
            rs = c.getConexion().executeQuery("SELECT * FROM articulo WHERE id_articulo = '"+input.split("#")[4].split("@")[0]+"'");
            rs.next();
            Articulo artDetalles = new Articulo(rs.getInt("id_articulo"),rs.getString("nombre"),rs.getString("descripcion"),rs.getString("imagen"));
            rs = c.getConexion().executeQuery("SELECT * FROM subastar WHERE id_articulo = '"+input.split("#")[4].split("@")[0]+"'");
            rs.next();
            Subasta subDetalles = new Subasta(rs.getInt("id_usuario"),rs.getInt("id_articulo"),rs.getString("fecha_inicio"),rs.getString("fecha_fin"),rs.getString("estado"),rs.getInt("precio_salida"));
            String str = ("C:"+artDetalles.getImagen());
            System.out.println(artDetalles.getImagen());
            File file = new File(str);
            byte[] fileContent = FileUtils.readFileToByteArray(file);
            output = "PROTOCOLCRISTOBAY1.0#GET_SUBASTA#"+artDetalles.getId_articulo()+"@"+subDetalles.getFecha_inicio()+"@"+subDetalles.getFecha_fin()+"#"+artDetalles.getDescripcion()+"#"+artDetalles.getImagen().split("\\.")[1]+"#"+Base64.getEncoder().encodeToString(fileContent).length();
            System.out.println(output);
        }else if(input.contains("PREPARED_TO_RECEIVE")){
            rs = c.getConexion().executeQuery("SELECT * FROM articulo WHERE id_articulo = '"+input.split("#")[4]+"'");
            rs.next();
            Articulo artDetalles = new Articulo(rs.getInt("id_articulo"),rs.getString("nombre"),rs.getString("descripcion"),rs.getString("imagen"));
            String str = ("C:"+artDetalles.getImagen());
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
        }else if(input.contains("BID_PRODUCT")){
            int cantPuj = Integer.valueOf(input.split("#")[5]);
            rs = c.getConexion().executeQuery("SELECT * FROM subastar WHERE id_articulo = "+input.split("#")[4].split("@")[0]);
            rs.next();
            Subasta subasta = new Subasta(rs.getInt("id_usuario"),rs.getInt("id_articulo"),rs.getString("fecha_inicio"),rs.getString("fecha_fin"),rs.getString("estado"),rs.getInt("precio_salida"));
            rs = c.getConexion().executeQuery("SELECT * FROM pujar WHERE id_articulo = '"+input.split("#")[4].split("@")[0]+"'");
            ArrayList<Puja> pujasArticulo = new ArrayList<>();
            while(rs.next()){
                pujasArticulo.add(new Puja(rs.getInt("id_usuario"),rs.getInt("id_articulo"),rs.getString("fecha_y_hora"),rs.getString("fecha_inicio"),rs.getString("fecha_fin"),rs.getInt("cantidad_pujada")));
            }
            for(int x = 0;x<pujasArticulo.size();x++){
                if(pujasArticulo.get(x).getCantidad_pujada()>cantPuj){
                    return "PROTOCOLCRISTOBAY1.0#BID_REJECTED#"+input.split("#")[4].split("@")[0]+"@"+input.split("#")[4].split("@")[1]+"@"+input.split("#")[4].split("@")[2]+"#"+input.split("#")[2];
                }
            }       
            rs = c.getConexion().executeQuery("SELECT * FROM usuario WHERE login = '"+input.split("#")[2]+"'");
            rs.next();
            Usuario userLogged = new Usuario(rs.getInt("id_usuario"),rs.getString("login"),rs.getString("nombre"));
            
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime now = LocalDateTime.now();
            
            System.out.println(subasta.getEstado());
            if(subasta.getEstado().equals("abierta")){
                if(subasta.getId_usuario()==userLogged.getId_usuario()){
                    output = "PROTOCOLCRISTOBAY1.0#BID_REJECTED#"+input.split("#")[4].split("@")[0]+"@"+input.split("#")[4].split("@")[1]+"@"+input.split("#")[4].split("@")[2]+"#"+input.split("#")[2];
                    System.out.println(output);
                }else{
                    System.out.println("INSERT INTO pujar (fecha_y_hora, id_usuario, id_articulo,fecha_inicio,fecha_fin,cantidad_pujada) VALUES ('"+now.format(formatter)+"',"+userLogged.getId_usuario()+","+input.split("#")[4].split("@")[0]+",'"+input.split("#")[4].split("@")[1]+"','"+input.split("#")[4].split("@")[2]+"',"+input.split("#")[5]+")");
                    c.getConexion().execute("INSERT INTO pujar (fecha_y_hora, id_usuario, id_articulo,fecha_inicio,fecha_fin,cantidad_pujada) VALUES ('"+LocalDateTime.now()+"',"+userLogged.getId_usuario()+","+input.split("#")[4].split("@")[0]+",'"+input.split("#")[4].split("@")[1]+"','"+input.split("#")[4].split("@")[2]+"',"+input.split("#")[5]+")");
                    output = "PROTOCOLCRISTOBAY1.0#BID_ACCEPTED#"+input.split("#")[4].split("@")[0]+"@"+input.split("#")[4].split("@")[1]+"@"+input.split("#")[4].split("@")[2]+"#"+input.split("#")[2]+"#<fecha_y_hora_puja>@"+cantPuj;
                }
            }else{
                output = "PROTOCOLCRISTOBAY1.0#BID_REJECTED#"+input.split("#")[4].split("@")[0]+"@"+input.split("#")[4].split("@")[1]+"@"+input.split("#")[4].split("@")[2]+"#"+input.split("#")[2];
                System.out.println(output);
            }
        }else if(input.contains("BYE")){
            for(int i = 0; i < usuariosLogeados.size();i++){
                if(usuariosLogeados.get(i).equals(input.split("#")[2])){
                    usuariosLogeados.remove(i);
                    tokens.remove(i);
                    output = "PROTOCOLCRISTOBAY1.0#VAYAUSTEDCONDIOS#"+input.split("#")[2]+"#"+input.split("#")[3];
                }
            }
        }
       
        return output;
    }
}
