/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controlador;

import Modelo.Articulo;
import Modelo.SubastaCln;
import Vista.Login;
import Vista.Ventana;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import org.apache.commons.io.FileUtils;

/**
 *
 * @author Luis
 */
public class ConexionServer  extends Thread{
    static PrintWriter out;
    static BufferedReader in;
    static Socket sc;
    int paquetes = 0;
    int a,b;
    Login lg;
    Ventana v;
    String buffer;
    String str = "";
    int prov = 0;
    String aux = "";
    int idArticulo;
    public int articuloSeleccionado;
    ArrayList<SubastaCln> subastas = new ArrayList<>();
    Modelo_Tabla mt = new Modelo_Tabla(this);
    
    
    public ConexionServer() throws IOException{
        sc = new Socket("localhost",6666);
        out = new PrintWriter(sc.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(sc.getInputStream()));
    }
    
    public ConexionServer(Login lg) throws IOException{
        sc = new Socket("localhost",6666);
        out = new PrintWriter(sc.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(sc.getInputStream()));
        this.lg = lg;
    }
    
    @Override
    public void run(){
        try {
            String inputLine;
            while((inputLine = in.readLine()) != null){
                if(inputLine.contains("WELLCOME")){
                    buffer = inputLine;
                    subastas.clear();
                    this.pedirSubastasPorEstado("ALL");
                    v = new Ventana(this);
                    lg.dispose();
                }else if(inputLine.contains("BAD_LOGIN")){
                    System.out.println("Ha fallado");
                }else if(inputLine.contains("AUCTION_AVAILABLE")){
                    String[] cadDiv = inputLine.split("#");
                    int contSub = 3;
                    int numSubastas = Integer.valueOf(cadDiv[2]);
                    subastas.clear();
                    for(int i = 0;i<numSubastas;i++){
                        String sub[] = cadDiv[contSub].split("@");
                        subastas.add(new SubastaCln(Integer.valueOf(sub[0]),sub[1],sub[2],sub[3],cadDiv[contSub].substring(cadDiv[contSub].indexOf("\"")+1,cadDiv[contSub].lastIndexOf("\"")),sub[sub.length-3],Integer.valueOf(sub[sub.length-2]),Integer.valueOf(sub[sub.length-1])));
                        contSub++;
                    }
                    mt.setAr(subastas);
                    v.getTablaSubastas().setModel(mt.Modelo());
                    
                    v.setVisible(true);
                }else if(inputLine.contains("AUCTION_NOT_AVAILABLE")){
                    System.out.println("fallo al obtener las subastas");
                }else if(inputLine.contains("GET_SUBASTA")){
                    v.setDescripcionArticulo(inputLine.split("#")[3]);
                    paquetes = Integer.valueOf(inputLine.split("#")[5]);
                    if(paquetes%256==0){
                        a = paquetes/256;
                        b = a;
                    }else{
                        a= (paquetes/256);
                        b = a-1;
                    }
                    System.out.println(b);
                    out.println("PROTOCOLCRISTOBAY1.0#PREPARED_TO_RECEIVE#"+buffer.split("#")[2]+"#"+this.getPalabraSecreta(buffer)+"#"+this.getArticuloSeleccionado()+"#SIZE_PACKAGE#"+a);
                    str= "";
                }else if(inputLine.contains("BITS")){
                    prov = Integer.valueOf(inputLine.split("#")[3].split("S")[1]);
                    str = str + inputLine.split("#")[2];
                    if(prov==a-1){
                        
                        byte[] decodedBytes = Base64.getDecoder().decode(str);
                        Image image = ImageIO.read(new ByteArrayInputStream(decodedBytes));
                        v.getImagenLabel().setIcon(this.getScaledImage(image));
                        System.out.println("Imagen recibida!");
                    }
                }else if(inputLine.contains("BID_ACCEPTED")){
                    this.pedirSubastasPorEstado(v.getEstado());
                }else if(inputLine.contains("VAYAUSTEDCONDIOS")){
                    new Login().setVisible(true);
                    v.dispose();
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(ConexionServer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(ConexionServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public ArrayList<SubastaCln> getSubastas() {
        return subastas;
    }

    
    
    public int getArticuloSeleccionado() {
        return articuloSeleccionado;
    }
    public void setLogin(Login lg){
        this.lg = lg;
    }

    public Modelo_Tabla getMt() {
        return mt;
    }
    
    
    public void setVentana(Ventana v){
        this.v = v;
    }
    
    public Ventana getVentana(){
        return v;
    }

    public String getBuffer() {
        return buffer;
    }

    public void setArticuloSeleccionado(int articuloSeleccionado) {
        this.articuloSeleccionado = subastas.get(articuloSeleccionado).getCodProd();
    }
    
    
    public ConexionServer getConexionServer(){
        return this;
    }
    public void logearse() throws IOException{
        out.println("PROTOCOLCRISTOBAY1.0#LOGIN#"+lg.getNombreUsu()+"#"+lg.getPassUsu());
//        in = new BufferedReader(new InputStreamReader(sc.getInputStream()));
//        
//        return buffer;
    }
    
    public String getPalabraSecreta(String str){
        String st = str.split("#")[4];
        return st;
    }
    
    public void pedirSubastasPorEstado(String estado){
        String str = "ALL";
        switch (estado) {
            case "Abiertas":
                str="OPEN";
                break;
            case "Cerradas por eliminación":
                str="CLOSEDBYDROP";
                break;
            case "Cerradas por compra":
                str="CLOSEDBYBUY";
                break;
            case "Cerradas por tiempo":
                str="CLOSEDBYTIME";
                break;
            case "Creadas":
                str="CREATED";
                break;
            default:
                break;
        }
        out.println("PROTOCOLCRISTOBAY1.0#GET_SUBASTAS_"+str+"#"+lg.getNombreUsu()+"#"+lg.getCs().getPalabraSecreta(buffer));
    }
    
//    public ArrayList<SubastaCln> getSubastas() throws IOException{
//        //he.start();
//        subastas.clear();
//        String str="";
//        int contSub = 3;
//        in = new BufferedReader(new InputStreamReader(sc.getInputStream()));
//        str = in.readLine();
//        String[] cadDiv = str.split("#");
//        if(str.contains("ERROR")){
//            System.out.println(str);
//            System.out.println("Hubo un error en su solicitud, intentelo de nuevo mas tarde");
//        }else if(str.contains("AUCTION_AVAILABLE")){
//            System.out.println(str);
//            int numSubastas = Integer.valueOf(cadDiv[2]);
//            
//            for(int i = 0;i<numSubastas;i++){
//                String sub[] = cadDiv[contSub].split("@");
//                
//                subastas.add(new SubastaCln(Integer.valueOf(sub[0]),sub[1],sub[2],sub[3],cadDiv[contSub].substring(cadDiv[contSub].indexOf("\"")+1,cadDiv[contSub].lastIndexOf("\"")),sub[sub.length-3],Integer.valueOf(sub[sub.length-2]),Integer.valueOf(sub[sub.length-1])));
//                contSub++;
//            }
//        }
//        
//        return subastas;
//    }
    
    public void pedirDetallesArticulo(int id) throws IOException{
        System.out.println("PROTOCOLCRISTOBAY1.0#GET_SUBASTA#"+buffer.split("#")[2]+"#"+this.getPalabraSecreta(buffer)+"#"+subastas.get(id).getCodProd()+"@"+subastas.get(id).getFechaInicio()+"@"+subastas.get(id).getFechaFin());
        out.println("PROTOCOLCRISTOBAY1.0#GET_SUBASTA#"+buffer.split("#")[2]+"#"+this.getPalabraSecreta(buffer)+"#"+subastas.get(id).getCodProd()+"@"+subastas.get(id).getFechaInicio()+"@"+subastas.get(id).getFechaFin());
//        String str = "";
//        in = new BufferedReader(new InputStreamReader(sc.getInputStream()));
//        str = in.readLine();
//        return str;
    }
    
    public void pedirImagen(int img) throws IOException{
//        int paquetes = Integer.valueOf(img.split("#")[5]);
//        int a,b;
//        if(paquetes%256==0){
//            a = paquetes/256;
//            b = a;
//        }else{
//            a= (paquetes/256);
//            b = a-1;
//        }
//        System.out.println(a);
//        String str = "";
//        int prov = 0;
        
//        String aux = "";
//        while(prov!=b){
//            System.out.println("Inicio del bucle");
//            in = new BufferedReader(new InputStreamReader(sc.getInputStream()));
//            aux = in.readLine();
//            System.out.println(aux);
//            prov = Integer.valueOf(aux.split("#")[3].split("S")[1]);
//            System.out.println(prov);
//            in = new BufferedReader(new InputStreamReader(sc.getInputStream()));
//            str = str + aux.split("#")[2];
//        }
//        System.out.println(str);
//        byte[] decodedBytes = Base64.getDecoder().decode(str);
//        Image image = ImageIO.read(new ByteArrayInputStream(decodedBytes));
//        String string = new String(decodedBytes);
//        System.out.println(string);
//        
//        return image;
    }
    
    public Icon getScaledImage(Image srcImg){
        BufferedImage resizedImg = new BufferedImage(303, 303, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = resizedImg.createGraphics();

        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.drawImage(srcImg, 0, 0, 303, 303, null);
        g2.dispose();

        Icon icon = new ImageIcon(resizedImg);
        return icon;
    }
    
    public void pujar(int i, int p) throws IOException{
        out.println("PROTOCOLCRISTOBAY1.0#BID_PRODUCT#"+buffer.split("#")[2]+"#"+this.getPalabraSecreta(buffer)+"#"+subastas.get(i).getCodProd()+"@"+subastas.get(i).getFechaInicio()+"@"+subastas.get(i).getFechaFin()+"#"+p);
        
    }
    
    public void cerrarSesion() throws IOException{
        out.println("PROTOCOLCRISTOBAY1.0#BYE#"+lg.getNombreUsu()+"#"+this.getPalabraSecreta(buffer));
        
    }
    
    public Modelo_Tabla actualizarModelo() throws IOException{
        Modelo_Tabla mt = new Modelo_Tabla(this);
        return mt;
    }
}
