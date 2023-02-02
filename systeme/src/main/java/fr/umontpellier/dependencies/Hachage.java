package fr.umontpellier.dependencies;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Classe permettant de hacher un mot de passe
 */
public class Hachage {

    /**
     * Le hash du mot de passe
     */
    String hash;

    /**
     * Constructeur de la classe Hachage qui permet de hacher un mot de passe
     *
     * @param password Le mot de passe à hacher
     * @throws NoSuchAlgorithmException si l'algorithme de hachage n'existe pas
     */
    public Hachage(String password) throws NoSuchAlgorithmException {
        hash = hachage(password);
    }

    /**
     * Méthode permettant de hacher un mot de passe
     *
     * @param password Le mot de passe à hacher
     * @return Le hash du mot de passe
     * @throws NoSuchAlgorithmException si l'algorithme de hachage n'existe pas
     */
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