package com.grupoDistribuidos;

import java.util.Scanner;


import org.zeromq.SocketType;
import org.zeromq.ZMQ.Poller;
import org.zeromq.ZMQ.Socket;
import org.zeromq.ZContext;

public class Cliente {

    public static void main(String[] args) throws Exception {
        try(ZContext context = new ZContext()){
            System.out.println("Ingrese peticion");
            String peticion = "hola";
            Scanner entradaEscaner = new Scanner (System.in); 
            // peticion = entradaEscaner.nextLine();
            Socket socketCliente = context.createSocket(SocketType.REQ);
            ZHelper.setId(socketCliente);

            socketCliente.connect("tcp://25.63.93.84:5559");
            String reply = "";
            while(true){
                socketCliente.send(peticion);
                reply = socketCliente.recvStr();
                System.out.println("Cliente: "+reply);
            }
            
        }
    }
}