/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controlador;

import Modelo.SubastaCln;
import Vista.Login;
import Vista.Ventana;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

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
        String str="";
        try {
            in = new BufferedReader(new InputStreamReader(sc.getInputStream()));
            str = in.readLine();
            System.out.println(str);
        } catch (IOException ex) {
            Logger.getLogger(Login.class.getName()).log(Level.SEVERE, null, ex);
        }
        buffer=str;
        return str;
    }
    
    public String getPalabraSecreta(String str){
        String st = str.split("#")[5];
        return st;
    }
    
    public void pedirSubastasPorEstado(String estado){
        
        System.out.println(lg.getNombreUsu());
        out.println("PROTOCOLCRISTOBAY1.0#GET_SUBASTAS_"+estado+"#"+lg.getNombreUsu()+"#"+this.getPalabraSecreta(buffer));
    }
    
    public ArrayList<SubastaCln> getSubastas() throws IOException{
        
        subastas = new ArrayList<>();
        String str="";
        String[] cadDiv = str.split("#");
        int contSub = 4;
        int contProd = 5;
        in = new BufferedReader(new InputStreamReader(sc.getInputStream()));
        if((str = in.readLine()).contains("ERROR")){
            System.out.println("Hubo un error en su solicitud, intentelo de nuevo mas tarde");
        }else if((str = in.readLine()).contains("AUCTION_AVAILABLE")){
            System.out.println(str);
            int numSubastas = Integer.valueOf(cadDiv[3]);
            for(int i = numSubastas;i>0;i--){
                subastas.add(new SubastaCln(Integer.valueOf(cadDiv[contProd].split("@")[1]),cadDiv[contSub].split("@")[2],cadDiv[contSub].split("@")[3],cadDiv[contSub].split("@")[4]));
                contSub=contSub+2;
                contProd=contProd+2;
            }
        }
        
        return subastas;
    }
}
