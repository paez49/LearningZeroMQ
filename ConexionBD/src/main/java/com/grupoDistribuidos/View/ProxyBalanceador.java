package com.grupoDistribuidos.View;

import com.grupoDistribuidos.Model.Entidades.Producto;
import com.grupoDistribuidos.Controller.FachadaOCR;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ.Socket;

public class ProxyBalanceador {

    public static void main(String[] argv) throws Exception {
        FachadaOCR fco = new FachadaOCR();
        List<Producto> res = fco.ConsultarProductos();
        System.out.println(res.toString());

        Producto prod = fco.ObtenerProductoXID(49);
        if (prod.getCantiProducto() > 1 && prod != null) {
            prod.setCantiProducto(prod.getCantiProducto() - 1);
            fco.actualizarProducto(prod.getIdProducto(), prod.getCantiProducto());
            if (true) {
                System.out.println("Se modificó");
                System.out.println("PRODUCTO: "+prod.toString());
            }
        }
        /*
         * try (ZContext context = new ZContext()) {
         * Socket server = context.createSocket(SocketType.REP);
         * server.bind("tcp://*:5555");
         * System.out.println("Esperando cliente");
         * String request = "PETICIÓN RECIBIDA";
         * while (true) {
         * String peticion = server.recvStr();
         * System.out.println("PETICION: " + peticion);
         * String[] arrOfStr = peticion.split(" ");
         * Thread.sleep(100); // Do some heavy work
         * server.send(request);
         * }
         * }
         */

    }
}