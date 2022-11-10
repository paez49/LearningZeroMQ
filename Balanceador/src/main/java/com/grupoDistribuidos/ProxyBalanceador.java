package com.grupoDistribuidos;

import org.zeromq.SocketType;
import org.zeromq.ZMQ.Socket;
import org.zeromq.ZContext;
import org.zeromq.ZMQException;

public class ProxyBalanceador {
    public static void main(String[] args) throws Exception {
            System.out.println("Iniciando Balanceador-Proxy");
            ZContext context = new ZContext();
            BackEnd backend = new BackEnd(context);
            FrontEnd frontend = new FrontEnd(context);
            frontend.start();
            backend.start();
        }
    }