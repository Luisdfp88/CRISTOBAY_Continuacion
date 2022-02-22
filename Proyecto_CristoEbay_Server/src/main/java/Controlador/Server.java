/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controlador;

import Modelo.Protocolo;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;

/**
 *
 * @author Luis
 */
public class Server {
   
    static ArrayList<HebraServer> ArrayHebras = new ArrayList<>();
    static Protocolo ptc = new Protocolo(ArrayHebras);
    public static void main(String args[]) throws IOException {
        int portNumber = Integer.parseInt(args[0]);
        boolean listening = true;
        ArrayHebras = new ArrayList<>();
        Thread thr = new Thread(){
            @Override
            public void run(){
                try(ServerSocket svsc = new ServerSocket(portNumber)){
                    while(listening){
                        ArrayHebras.add(new HebraServer(svsc.accept(), ptc));
                        ArrayHebras.get(ArrayHebras.size()-1).start();
                        
                    }
                }catch(IOException e){
                System.err.println("No se ha podido escuchar el puerto " + portNumber);
                System.exit(-1);
                }
            }
        };
        thr.start();
    }
    
    public ArrayList<HebraServer> getClientesConectados(){
        return ArrayHebras;
    }
}
