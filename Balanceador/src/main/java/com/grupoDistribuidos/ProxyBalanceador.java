package com.grupoDistribuidos;

import org.zeromq.SocketType;
import org.zeromq.ZMQ.Socket;
import org.zeromq.ZContext;

public class ProxyBalanceador {
    private static class backEnd extends Thread {

        @Override
        public void run() {
            try (ZContext context = new ZContext()) {
                System.out.println("Iniciando backEnd");
                Socket socketB = context.createSocket(SocketType.DEALER);
                socketB.bind("tcp://*:5560");
                // String mensaje = socketB.recvStr();
                
            }
        }
    }

    private static class frontEnd extends Thread {
        @Override
        public void run() {

            try (ZContext context = new ZContext()) {
                System.out.println("Iniciando frontEnd");
                Socket socketF = context.createSocket(SocketType.ROUTER);
                // Por acá se recibiran las peticiones
                socketF.bind("tcp://*:5559");
                while(true){
                    String message = socketF.recvStr();
                    
                    System.out.println(message);
                    socketF.send("message");

                }
                
            }
        }

    }

    public static void main(String[] args) throws Exception {
        System.out.println("Iniciando Balanceador-Proxy");

        new backEnd().start();
        new frontEnd().start();

    }
}