package com.grupoDistribuidos.View;

import org.zeromq.ZMQ.Socket;

import com.grupoDistribuidos.Controller.FachadaOCR;
import com.grupoDistribuidos.Model.Entidades.Producto;
import com.grupoDistribuidos.Model.Entidades.Usuario;

import org.zeromq.*;

import java.util.List;
import java.util.Random;
import org.zeromq.ZMQ.Poller;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;

public class Servidor {
    private final static int HEARTBEAT_LIVENESS = 3; // 3-5 is reasonable
    private final static int HEARTBEAT_INTERVAL = 1000; // msecs
    private final static int INTERVAL_INIT = 1000; // Initial reconnect
    private final static int INTERVAL_MAX = 32000; // After exponential backoff

    // Paranoid Pirate Protocol constants
    private final static String PPP_READY = "\\001"; // Signals worker is ready
    private final static String PPP_HEARTBEAT = "\\002"; // Signals worker heartbeat

    private static Socket worker_socket(ZContext ctx) {
        Socket worker = ctx.createSocket(SocketType.DEALER);
        worker.connect("tcp://25.63.93.84:5557");

        // Tell queue we're ready for work
        System.out.println("[SERVER] Listo para recibir peticiones\n");
        ZFrame frame = new ZFrame(PPP_READY);
        frame.send(worker, 0);

        return worker;
    }

    public static void main(String[] args) throws NoSuchAlgorithmException, InvalidKeySpecException {
        FachadaOCR fco = new FachadaOCR();

        try (ZContext ctx = new ZContext()) {
            Socket worker = worker_socket(ctx);

            Poller poller = ctx.createPoller(1);
            poller.register(worker, Poller.POLLIN);

            // If liveness hits zero, queue is considered disconnected
            int liveness = HEARTBEAT_LIVENESS;
            int interval = INTERVAL_INIT;

            // Send out heartbeats at regular intervals
            long heartbeat_at = System.currentTimeMillis() + HEARTBEAT_INTERVAL;
            String resultado = "";
            String mensaje = "";
            String mensajeR = "";
            String[] separar;
            String peticion = "";
            boolean logueado = false;
            boolean comprado = false;
            while (true) {
                int i = 0;
                i++;
                System.out.println("i"+i);
                int rc = poller.poll(HEARTBEAT_INTERVAL);
                if (rc == -1)
                    break; // Interrupted

                if (poller.pollin(0)) {
                    // Get message
                    // - 3-part envelope + content -> request
                    // - 1-part HEARTBEAT -> heartbeat
                    ZMsg msg = ZMsg.recvMsg(worker);
                    if (msg == null)
                        break;

                    if (msg.size() == 3) {

                        System.out.println("[SERVER] Enviando resultado petición\n");
                        resultado = "";
                        mensaje = msg.toString();

                        mensajeR = mensaje.replace("[", "");
                        mensajeR = mensajeR.replace("]", "");

                        separar = mensajeR.split(",");
                        System.out.println("[SERVER] MENSAJE: " + mensajeR);

                        peticion = separar[2];

                        peticion = peticion.replace(" ", "");
                        System.out.println("[SERVER] PETICION: " + peticion);
                        String[] peticionR = peticion.split("-");
                        switch (peticionR[0]) {
                            case "C":
                                List<Producto> res = fco.ConsultarProductos();
                                for (Producto res2 : res) {
                                    resultado += res2.toString() + "_";
                                }
                                System.out.println(resultado);
                                msg.removeLast();
                                msg.addLast(resultado);
                                msg.send(worker);
                                break;
                            case "L":
                                Usuario user = fco.obtenerUsuarioContrasena(peticionR[1]);
                                logueado = validatePassword(peticionR[2], user.getPasword());
                                if (logueado) {
                                    msg.removeLast();
                                    resultado = "1";
                                    msg.addLast(resultado);
                                    msg.send(worker);
                                }else{
                                    msg.removeLast();
                                    resultado = "0";
                                    msg.addLast(resultado);
                                    msg.send(worker);
                                }
                                break;
                            case "K":
                                Producto prod = fco.ObtenerProductoXID(Integer.parseInt(peticionR[1]));
                                if (prod.getCantiProducto() > 1 && prod != null) {
                                    prod.setCantiProducto(prod.getCantiProducto() - 1);
                                    comprado = fco.actualizarProducto(prod.getIdProducto(), prod.getCantiProducto());
                                }
                                if (comprado) {
                                    resultado = "Compra exitosa.";
                                    msg.removeLast();
                                    msg.addLast(resultado);
                                    System.out.println(msg.toString());
                                    msg.send(worker);
                                } else {
                                    resultado = "No se pudo comprar el articulo.";
                                    msg.removeLast();
                                    msg.addLast(resultado);
                                    System.out.println(msg.toString());
                                    msg.send(worker);
                                }
                                break;
                        }

                        liveness = HEARTBEAT_LIVENESS;
                    } else
                    // When we get a heartbeat message from the queue, it
                    // means the queue was (recently) alive, so reset our
                    // liveness indicator:
                    if (msg.size() == 1) {
                        ZFrame frame = msg.getFirst();
                        String frameData = new String(
                                frame.getData(), ZMQ.CHARSET);
                        if (PPP_HEARTBEAT.equals(frameData))
                            liveness = HEARTBEAT_LIVENESS;
                        else {
                            System.out.println("E: invalid message\n");
                            msg.dump(System.out);
                        }
                        msg.destroy();
                    } else {
                        System.out.println("E: invalid message\n");
                        msg.dump(System.out);
                    }
                    interval = INTERVAL_INIT;
                } else
                // If the queue hasn't sent us heartbeats in a while,
                // destroy the socket and reconnect. This is the simplest
                // most brutal way of discarding any messages we might have
                // sent in the meantime.
                if (--liveness == 0) {
                    System.out.println(
                            "W: heartbeat failure, can't reach queue\n");
                    System.out.printf(
                            "W: reconnecting in %sd msec\n", interval);
                    if (interval < INTERVAL_MAX)
                        interval *= 2;
                    ctx.destroySocket(worker);
                    worker = worker_socket(ctx);
                    liveness = HEARTBEAT_LIVENESS;
                }
                // Send heartbeat to queue if it's time
                if (System.currentTimeMillis() > heartbeat_at) {
                    long now = System.currentTimeMillis();
                    heartbeat_at = now + HEARTBEAT_INTERVAL;
                    System.out.println("[SERVER] HeartBeat balanceador\n");
                    ZFrame frame = new ZFrame(PPP_HEARTBEAT);
                    
                    
                    frame.send(worker, 0);
                }
                
            }
        }
    }

    private static String generateStorngPasswordHash(String password)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        int iterations = 1000;
        char[] chars = password.toCharArray();
        byte[] salt = getSalt();

        PBEKeySpec spec = new PBEKeySpec(chars, salt, iterations, 64 * 8);
        SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");

        byte[] hash = skf.generateSecret(spec).getEncoded();
        return iterations + ":" + toHex(salt) + ":" + toHex(hash);
    }

    private static byte[] getSalt() throws NoSuchAlgorithmException {
        SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
        byte[] salt = new byte[16];
        sr.nextBytes(salt);
        return salt;
    }

    private static String toHex(byte[] array) throws NoSuchAlgorithmException {
        BigInteger bi = new BigInteger(1, array);
        String hex = bi.toString(16);

        int paddingLength = (array.length * 2) - hex.length();
        if (paddingLength > 0) {
            return String.format("%0" + paddingLength + "d", 0) + hex;
        } else {
            return hex;
        }
    }

    private static boolean validatePassword(String originalPassword, String storedPassword)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        if(storedPassword != null){
            String[] parts = storedPassword.split(":");
            int iterations = Integer.parseInt(parts[0]);
    
            byte[] salt = fromHex(parts[1]);
            byte[] hash = fromHex(parts[2]);
    
            PBEKeySpec spec = new PBEKeySpec(originalPassword.toCharArray(),
                    salt, iterations, hash.length * 8);
            SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            byte[] testHash = skf.generateSecret(spec).getEncoded();
    
            int diff = hash.length ^ testHash.length;
            for (int i = 0; i < hash.length && i < testHash.length; i++) {
                diff |= hash[i] ^ testHash[i];
            }
            return diff == 0;
        }
        return false;
        
    }

    private static byte[] fromHex(String hex) throws NoSuchAlgorithmException {
        byte[] bytes = new byte[hex.length() / 2];
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = (byte) Integer.parseInt(hex.substring(2 * i, 2 * i + 2), 16);
        }
        return bytes;
    }
}
