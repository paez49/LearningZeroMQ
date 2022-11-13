package com.grupoDistribuidos;

import java.util.Scanner;


import org.zeromq.SocketType;
import org.zeromq.ZMQ.Poller;
import org.zeromq.ZMQ.Socket;
import org.zeromq.ZContext;
import org.zeromq.ZMsg;

public class Cliente {

    public static void main(String[] args) throws Exception {
        try(ZContext context = new ZContext()){
            System.out.println("Ingrese peticion");
            String peticion = "hola";
            Scanner entradaEscaner = new Scanner (System.in); 
            // 
            Socket socketCliente = context.createSocket(SocketType.REQ);
            ZHelper.setId(socketCliente);

            socketCliente.connect("tcp://25.63.93.84:5555");
            int i =0;
            while(true){
                peticion = entradaEscaner.nextLine();
                socketCliente.send(peticion);
                ZMsg reply = ZMsg.recvMsg(socketCliente);
                System.out.println("Cliente: "+reply.toString());
                System.out.println(i);
                i++;
            }
            
        }
    }
}