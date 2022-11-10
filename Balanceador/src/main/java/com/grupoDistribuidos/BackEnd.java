package com.grupoDistribuidos;

import org.zeromq.SocketType;
import org.zeromq.ZMQ.Socket;
import org.zeromq.ZContext;
import org.zeromq.ZMQException;

public class BackEnd extends Thread {
    ZContext context = new ZContext();

    BackEnd(ZContext n_context) {
        context = n_context;
    }

    @Override
    public void run() {
        try {
            Socket socketB = context.createSocket(SocketType.DEALER);
            socketB.bind("tcp://*:5560");

            while (true) {
                System.out.println("Hilo backend activo");
                String mensaje = socketB.recvStr();
                System.out.println("bacekdn: " + mensaje);
            }
        } catch (ZMQException e) {
            System.out.println(e.getMessage());
        } catch (Exception E) {
            E.printStackTrace();
        }
    }

}
