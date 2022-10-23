package com.grupoDistribuidos;

import javax.naming.ldap.SortKey;

import org.zeromq.SocketType;
import org.zeromq.ZMQ.Poller;
import org.zeromq.ZMQ.Socket;
import org.zeromq.ZContext;

public class Cliente {

    public static void main(String[] args) throws Exception {
        try(ZContext context = new ZContext()){
            Socket socket = context.createSocket(SocketType.REQ);
            socket.connect("tcp://10.43.100.203:5559");
            Poller poller = context.createPoller(1);
            poller.register(socket, Poller.POLLIN);
            String mensaje = String.format("%s", "hola");
            socket.send(mensaje);
            String hola = socket.recvStr();
            System.out.println(hola);
        }
    }
}