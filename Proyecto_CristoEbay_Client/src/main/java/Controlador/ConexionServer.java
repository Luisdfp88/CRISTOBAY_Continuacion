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
public class ConexionServer {
    static PrintWriter out;
    static BufferedReader in;
    static Socket sc;
    Login lg;
    Ventana v;
    String buffer;
    ArrayList<SubastaCln> subastas;
    
    public ConexionServer() throws IOException{
        sc = new Socket("localhost",6666);
        out = new PrintWriter(sc.getOutputStream(), true);
    }
    
    public ConexionServer(Login lg) throws IOException{
        sc = new Socket("localhost",6666);
        out = new PrintWriter(sc.getOutputStream(), true);
        this.lg = lg;
    }
    
    public void setLogin(Login lg){
        this.lg = lg;
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
    
    
    public ConexionServer getConexionServer(){
        return this;
    }
    public void logearse(){
        out.println("PROTOCOLCRISTOBAY1.0#LOGIN#"+lg.getNombreUsu()+"#"+lg.getPassUsu());
    }
    
    public String respuestaLogin(){
        
        try {
            in = new BufferedReader(new InputStreamReader(sc.getInputStream()));
            buffer = in.readLine();
            System.out.println(buffer);
        } catch (IOException ex) {
            Logger.getLogger(Login.class.getName()).log(Level.SEVERE, null, ex);
        }
        return buffer;
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
    
    public ArrayList<SubastaCln> getSubastas() throws IOException{
        
        subastas = new ArrayList<>();
        String str="";
        int contSub = 3;
        in = new BufferedReader(new InputStreamReader(sc.getInputStream()));
        str = in.readLine();
        String[] cadDiv = str.split("#");
        if(str.contains("ERROR")){
            System.out.println(str);
            System.out.println("Hubo un error en su solicitud, intentelo de nuevo mas tarde");
        }else if(str.contains("AUCTION_AVAILABLE")){
            System.out.println(str);
            int numSubastas = Integer.valueOf(cadDiv[2]);
            
            for(int i = 0;i<numSubastas;i++){
                String sub[] = cadDiv[contSub].split("@");
                subastas.add(new SubastaCln(Integer.valueOf(sub[0]),sub[1],sub[2],sub[3],sub[4].substring(1, sub[4].lastIndexOf("\"")),sub[5],Integer.valueOf(sub[6]),Integer.valueOf(sub[7])));
                contSub++;
            }
        }
        
        return subastas;
    }
    
    public String pedirDetallesArticulo(int id) throws IOException{
        out.println("PROTOCOLCRISTOBAY1.0#GET_SUBASTA#"+buffer.split("#")[2]+"#"+this.getPalabraSecreta(buffer)+"#"+subastas.get(id).getCodProd()+"@"+subastas.get(id).getFechaInicio()+"@"+subastas.get(id).getFechaFin());
        String str = "";
        in = new BufferedReader(new InputStreamReader(sc.getInputStream()));
        str = in.readLine();
        return str;
    }
    
    public Image pedirImagen(String img) throws IOException{
        int paquetes = Integer.valueOf(img.split("#")[5]);
        int a,b;
        if(paquetes%256==0){
            a = paquetes/256;
            b = a;
        }else{
            a= (paquetes/256)+1;
            b = a-1;
        }
        System.out.println(a);
        String str = "";
        int prov = 0;
        out.println("PROTOCOLCRISTOBAY1.0#PREPARED_TO_RECEIVE#"+buffer.split("#")[2]+"#"+this.getPalabraSecreta(buffer)+"#"+img.split("#")[2].split("@")[0]+"#SIZE_PACKAGE#"+a);
        String aux = "";
        while(prov!=b){
            System.out.println("Inicio del bucle");
            in = new BufferedReader(new InputStreamReader(sc.getInputStream()));
            aux = in.readLine();
            System.out.println(aux);
            prov = Integer.valueOf(aux.split("#")[3]);
            in = new BufferedReader(new InputStreamReader(sc.getInputStream()));
            str = str + aux.split("#")[2];
        }
        System.out.println(str);
        byte[] decodedBytes = Base64.getDecoder().decode(str);
        Image image = ImageIO.read(new ByteArrayInputStream(decodedBytes));
        String string = new String(decodedBytes);
        System.out.println(string);
        
        return image;
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
}
