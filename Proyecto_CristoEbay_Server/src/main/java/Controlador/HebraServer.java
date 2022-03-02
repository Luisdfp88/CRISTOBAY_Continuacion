/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controlador;

import Modelo.Protocolo;
import Vista.VentanaServidor;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Luis
 */
public class HebraServer extends Thread{
    private Socket sc;
    private Protocolo ptc;
    private int tamanoPaquete;
    private Server sv;
    private VentanaServidor vs;
    private BufferedReader in;
    public PrintWriter out;
    public String nombreUsuario;
    
    public HebraServer(Socket sc, Protocolo ptc, VentanaServidor vs, String token) throws IOException{
        super(token);
        this.sc = sc;
        this.ptc = ptc;
        out = new PrintWriter(sc.getOutputStream(),true);
        in = new BufferedReader(new InputStreamReader(sc.getInputStream()));
        this.vs = vs;
        this.ptc.tokens.add(token);
    }
    
    public void cerrarSocket() throws IOException{
        out.close();
        sc.close();
    }
    
    public HebraServer getHebraServer(){
        return this;
    }
    public Socket useSocket(){
        return sc;
    }
    @Override
    public synchronized void run(){
        {
            try {
                String inputLine, outputLine;
                while((inputLine = in.readLine()) != null){
                    if(inputLine.contains("LOGIN")){
                        nombreUsuario=inputLine.split("#")[2];
                    }
                    vs.escribirConsola(inputLine);
                    if(inputLine.contains("PREPARED_TO_RECEIVE")){
                        
                        this.setName(inputLine.split("#")[3]);
                        for(int i = 0;i<Integer.valueOf(inputLine.split("#")[6]);i++){
                            outputLine = ptc.procesarInput(inputLine+"#"+i+"#"+tamanoPaquete);
                            tamanoPaquete = tamanoPaquete-255;
                            System.out.println(outputLine);
                            out.println(outputLine);
                        }
                    }else{
                        outputLine = ptc.procesarInput(inputLine);
                        if(outputLine.contains("GET_SUBASTA#")){
                            tamanoPaquete = Integer.valueOf(outputLine.split("#")[5]); 
                        }else if(outputLine.contains("VAYAUSTEDCONDIOS")){
                            out.println(outputLine);
                            out.close();
                            in.close();
                            sc.close();
                            vs.limpiarListaUsuarios();
                            for(int j = 0;j<sv.ArrayHebras.size();j++){
                                if(outputLine.split("#")[3].equals(this.getName())){
                                    sv.ArrayHebras.remove(j);
                                    ptc.usuariosLogeados.remove(j);
                                    ptc.tokens.remove(j);

                                }
                                vs.escribirUsuarios(ptc.usuariosLogeados.get(j));
                                
                            }
                            vs.limpiarListaUsuarios();
                            for(int i = 0;i<ptc.usuariosLogeados.size();i++){
                                vs.escribirUsuarios(ptc.usuariosLogeados.get(i));
                            }
                            
                            this.finalize();
                        }else if(outputLine.contains("WELLCOME")){
                            vs.limpiarListaUsuarios();
                            for (int i = 0;i<ptc.usuariosLogeados.size();i++) {
                                vs.escribirUsuarios(ptc.usuariosLogeados.get(i));
                            }
                        }
                        System.out.println(outputLine);
                        out.println(outputLine);
                    }
                    
                    System.out.println();
                }   
                sc.close();
            } catch (IOException ex) {
                System.out.println("1");
                try {
                    ptc.c.getConexion().execute("UPDATE usuario SET connected = '0' WHERE login = '"+nombreUsuario+"'");
                    vs.limpiarListaUsuarios();
                    for(int i = 0;i<ptc.usuariosLogeados.size();i++){
                        if(nombreUsuario.equals(ptc.usuariosLogeados.get(i))){
                            ptc.usuariosLogeados.remove(i);
                            ptc.tokens.remove(i);
                            ptc.arrayHebras.remove(i);
                            sv.ArrayHebras.remove(i);
                        }
                        vs.escribirUsuarios(ptc.usuariosLogeados.get(i));
                    }
                    System.out.println("UPDATE usuario SET connected = '0' WHERE login = '"+nombreUsuario+"'");
                } catch (SQLException ex1) {
                    Logger.getLogger(HebraServer.class.getName()).log(Level.SEVERE, null, ex1);
                }
            } catch (SQLException ex) {
                System.out.println("2");
                
            } catch (Throwable ex) {
                System.out.println("3");
            } 
        }
    }
}
