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
                outputLine = ptc.procesarInput(inputLine);
                out.println(outputLine);
                System.out.println();
            }
            
            sc.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
