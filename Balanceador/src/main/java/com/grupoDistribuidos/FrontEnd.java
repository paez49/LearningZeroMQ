package com.grupoDistribuidos;

import org.zeromq.SocketType;
import org.zeromq.ZMQ.Socket;
import org.zeromq.ZContext;
import org.zeromq.ZMQException;

public class FrontEnd extends Thread {
    ZContext context = new ZContext();

    FrontEnd(ZContext n_context) {
        context = n_context;
    }

    @Override
    public void run() {
        try {
            Socket socketF = context.createSocket(SocketType.ROUTER);
            socketF.bind("tcp://*:5559");
            while (true) {
                System.out.println("Hilo frontend activo");
                String message = socketF.recvStr();
                System.out.println("FrontEnd: "+message);
                socketF.send("message");
            }
        } catch (ZMQException e) {
            System.out.println(e.getMessage());
        } catch (Exception E) {
            E.printStackTrace();
        }
    }
}
