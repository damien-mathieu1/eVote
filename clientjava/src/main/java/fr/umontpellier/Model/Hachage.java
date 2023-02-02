package fr.umontpellier.Model;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class Hachage {

    String hash;
    public Hachage(String password) throws NoSuchAlgorithmException {
        hash = hachage(password);
    }
    public static String hachage(String password) throws NoSuchAlgorithmException {


        MessageDigest msg = MessageDigest.getInstance("SHA-256");
        byte[] hash = msg.digest(password.getBytes(StandardCharsets.UTF_8));

        // convertir bytes en hexadécimal
        StringBuilder s = new StringBuilder();
        for (byte b : hash) {
            s.append(Integer.toString((b & 0xff) + 0x100, 16).substring(1));
        }
        return s.toString();
    }

    public String toString(){
        return "Voici votre hash : " +  hash;
    }
}