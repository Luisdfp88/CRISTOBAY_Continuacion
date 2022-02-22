/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controlador;

import Modelo.Protocolo;
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
    private BufferedReader in;
    public PrintWriter out;
    
    public HebraServer(Socket sc, Protocolo ptc) throws IOException{
        super("HebraServer");
        this.sc = sc;
        this.ptc = ptc;
        out = new PrintWriter(sc.getOutputStream(),true);
        in = new BufferedReader(new InputStreamReader(sc.getInputStream()));
        
    }
    
    public HebraServer getHebraServer(){
        return this;
    }
    public Socket useSocket(){
        return sc;
    }
    @Override
    public void run(){
        {
            try {
                String inputLine, outputLine;
                while((inputLine = in.readLine()) != null){
                    System.out.println(inputLine);
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
                            this.finalize();
                        }
                        out.println(outputLine);
                    }
                    
                    System.out.println();
                }   sc.close();
            } catch (IOException ex) {
                Logger.getLogger(HebraServer.class.getName()).log(Level.SEVERE, null, ex);
            } catch (SQLException ex) {
                Logger.getLogger(HebraServer.class.getName()).log(Level.SEVERE, null, ex);
            } catch (Throwable ex) {
                Logger.getLogger(HebraServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
