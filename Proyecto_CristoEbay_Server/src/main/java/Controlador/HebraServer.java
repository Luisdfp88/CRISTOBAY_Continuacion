/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controlador;

import Modelo.Protocolo;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 *
 * @author Luis
 */
public class HebraServer extends Thread{
    private Socket sc;
    private Protocolo ptc;
    private int tamanoPaquete;
    
    public HebraServer(Socket sc, Protocolo ptc){
        super("HebraServer");
        this.sc = sc;
        this.ptc = ptc;
    }
    
    public HebraServer getHebraServer(){
        return this;
    }
    public Socket useSocket(){
        return sc;
    }
    @Override
    public void run(){
        try(
            PrintWriter out = new PrintWriter(sc.getOutputStream(),true);
            BufferedReader in = new BufferedReader(new InputStreamReader(sc.getInputStream()));
        ){
            String inputLine, outputLine;
            while((inputLine = in.readLine()) != null){
                if(inputLine.contains("PREPARED_TO_RECEIVE")){
                    for(int i = 0;i<Integer.valueOf(inputLine.split("#")[6]);i++){
                            
                            outputLine = ptc.procesarInput(inputLine+"#"+i+"#"+tamanoPaquete);
                            tamanoPaquete = tamanoPaquete-255;
                        out.println(outputLine);
                    }
                }else{
                    outputLine = ptc.procesarInput(inputLine);
                    if(outputLine.contains("GET_SUBASTA#")){
                        tamanoPaquete = Integer.valueOf(outputLine.split("#")[5]);
                    }
                    out.println(outputLine);
                }
                System.out.println();
            }
            
            sc.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
