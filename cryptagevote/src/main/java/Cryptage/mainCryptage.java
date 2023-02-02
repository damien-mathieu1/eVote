package Cryptage;

import java.util.Scanner;

public class mainCryptage {
    public static void main(String[] args) {
        //Génération de la clé d'une taille de 100 bits
        KeyGenCryptage keyGenCryptage = new KeyGenCryptage(100);
        PublicKey publicKey = keyGenCryptage.getPublicKey();
        PrivateKey privateKey = keyGenCryptage.getPrivateKey();

        System.out.println("Clé publique = " + publicKey.getP() + ", " + publicKey.getG() + ", " + publicKey.getH());
        System.out.println("Clé privée = " + privateKey.getX());
        System.out.println();

        //Création d'un message clair saisi par l'utilisateur
        Scanner scanner = new Scanner(System.in);
        System.out.println("Entrez un message à crypter : ");
        String message = scanner.nextLine();

        //Affichage du message clair puis du même message mais crypté
        System.out.println("message 1 = " + message);
        MessageCrypte messageCrypte1 = new MessageCrypte(Integer.parseInt(message), publicKey);
        System.out.println("message crypte 1: " + messageCrypte1);
        System.out.println();

        //Création d'un deuxième message clair saisi par l'utilisateur
        System.out.println("Entrez un deuxième message à crypter : ");
        message = scanner.nextLine();
        System.out.println("message 2 = " + message);

        //Affichage du message clair puis du même message mais crypté
        MessageCrypte messageCrypte2 = new MessageCrypte(Integer.parseInt(message), publicKey);
        System.out.println("message crypte 2 :" + messageCrypte2);
        System.out.println();

        //Affichage de l'agrégation des deux messages cryptés
        System.out.println("Agrégation des deux messages cryptés...");
        MessageCrypte MessageAgrege = messageCrypte1.agregate(messageCrypte2, publicKey);
        System.out.println("message agregé :" + MessageAgrege);
        System.out.println();

        //Affichage du message clair correspondant à message1+message2 à partir du message crypté
        System.out.println("Déchiffrement du message agregé...");
        System.out.println("message decrypté : " + MessageAgrege.decrypt(privateKey, publicKey));
    }
}

