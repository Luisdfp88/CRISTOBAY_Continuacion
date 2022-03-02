/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controlador;

import Modelo.Protocolo;
import Modelo.randomWordGen;
import Vista.VentanaServidor;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.Iterator;

/**
 *
 * @author Luis
 */
public class Server {
   
    public ArrayList<HebraServer> ArrayHebras;
    
    Protocolo ptc = new Protocolo(ArrayHebras);
    static VentanaServidor vs;
    private Server sv = this;
    boolean listening = true;
    private ServerSocket svsc;
    Thread thr = new Thread(){
            @Override
            public void run(){
                ArrayHebras = new ArrayList<>();
                try{
                    svsc = new ServerSocket(7000);
                    while(listening){
                        String token = new randomWordGen().generarPalabra();
                        ArrayHebras.add(new HebraServer(svsc.accept(), ptc, vs,token));
                        ptc.setHebras(ArrayHebras);
                        ArrayHebras.get(ArrayHebras.size()-1).start();
                        
                    }
                    
                }catch(IOException e){
                System.err.println(e);
                
                }
            }
        };
    public void IniciarServidor(){
        thr.start();
    }
    
    public Server(VentanaServidor vs){
        this.vs = vs;
    }
    public void ApagarServidor() throws IOException, Throwable{
        for (HebraServer clientes : ArrayHebras) {
            clientes.out.println("VAYAUSTEDCONDIOS");
            clientes.cerrarSocket();
        }
        ArrayHebras.clear();
        ptc.tokens.clear();
        ptc.usuariosLogeados.clear();
        listening = false;
        vs.limpiarListaUsuarios();
        svsc.close();
        
        
    }
    public ArrayList<HebraServer> getClientesConectados(){
        return ArrayHebras;
    }
}
