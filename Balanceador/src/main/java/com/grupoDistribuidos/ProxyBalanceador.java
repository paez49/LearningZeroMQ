package com.grupoDistribuidos;

import java.util.LinkedList;
import java.util.Queue;

import org.zeromq.SocketType;
import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Poller;
import org.zeromq.ZMQ.Socket;
import org.zeromq.ZContext;

public class ProxyBalanceador {
    public static void main(String[] args) throws Exception {
        try (ZContext context = new ZContext()) {
            System.out.println("INICIANDO PROXY");
            Socket frontend = context.createSocket(SocketType.ROUTER);
            Socket backend = context.createSocket(SocketType.ROUTER);
            frontend.bind("tcp://*:5559");
            backend.bind("tcp://*:5560");

            Queue<String> colaServidores = new LinkedList<String>();
            while (true) {
                Poller items = context.createPoller(2);
                items.register(backend, Poller.POLLIN);
                if (colaServidores.size() > 0) {
                    items.register(frontend, Poller.POLLIN);
                }
                if (items.poll() < 0) {
                    break;
                }
                if (items.pollin(0)) {

                    // Queue worker address for LRU routing
                    colaServidores.add(backend.recvStr());

                    // Second frame is empty
                    String empty = backend.recvStr();
                    assert (empty.length() == 0);

                    // Third frame is READY or else a client reply address
                    String clientAddr = backend.recvStr();

                    // If client reply, send rest back to frontend
                    if (!clientAddr.equals("READY")) {

                        empty = backend.recvStr();
                        assert (empty.length() == 0);

                        String reply = backend.recvStr();
                        frontend.sendMore(clientAddr);
                        frontend.sendMore("");
                        frontend.send(reply);
                    }
                }
                if (items.pollin(1)) {
                    // Now get next client request, route to LRU worker
                    // Client request is [address][empty][request]
                    String clientAddr = frontend.recvStr();

                    String empty = frontend.recvStr();
                    assert (empty.length() == 0);

                    String request = frontend.recvStr();

                    String workerAddr = colaServidores.poll();

                    backend.sendMore(workerAddr);
                    backend.sendMore("");
                    backend.sendMore(clientAddr);
                    backend.sendMore("");
                    backend.send(request);
                }

            }
        }
        /*
         * System.out.println("Iniciando Balanceador-Proxy");
         * ZContext context = new ZContext();
         * BackEnd backend = new BackEnd(context);
         * FrontEnd frontend = new FrontEnd(context);
         * frontend.start();
         * backend.start();
         */
    }
}