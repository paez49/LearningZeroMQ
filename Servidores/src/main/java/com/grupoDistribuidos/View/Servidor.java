package com.grupoDistribuidos.View;

import com.grupoDistribuidos.Model.Entidades.Producto;
import com.grupoDistribuidos.Model.Entidades.Usuario;
import com.grupoDistribuidos.Controller.FachadaOCR;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ.Socket;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;

public class Servidor {

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
                System.out.println("PRODUCTO: " + prod.toString());
            }
<<<<<<< HEAD
=======
        } else {
            System.out.println("No existe");
>>>>>>> 5425f21533d3d8f0652afbefac30223071550534
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
<<<<<<< HEAD

        String originalPassword = "eskere";

        String generatedSecuredPasswordHash = generateStorngPasswordHash(originalPassword);
        System.out.println(generatedSecuredPasswordHash);
        //String originalPassword = "password";
        boolean matched = validatePassword("password", generatedSecuredPasswordHash);
=======
        Usuario user = fco.obtenerUsuarioContrasena("Juan");
        boolean matched = validatePassword("distribuidos", user.getPasword());
>>>>>>> 5425f21533d3d8f0652afbefac30223071550534
        System.out.println(matched);
        user = fco.obtenerUsuarioContrasena("Natalia");
        matched = validatePassword("Distribuidos", user.getPasword());
        System.out.println(matched);
        user = fco.obtenerUsuarioContrasena("Laura");
        matched = validatePassword("sofia456", user.getPasword());
        System.out.println(matched);
        user = fco.obtenerUsuarioContrasena("Rafael");
        matched = validatePassword("12345678", user.getPasword());
        System.out.println(matched);
        user = fco.obtenerUsuarioContrasena("Mateo");
        matched = validatePassword("guau123", user.getPasword());
        System.out.println(matched);
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

    private static byte[] fromHex(String hex) throws NoSuchAlgorithmException {
        byte[] bytes = new byte[hex.length() / 2];
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = (byte) Integer.parseInt(hex.substring(2 * i, 2 * i + 2), 16);
        }
        return bytes;
    }
}
