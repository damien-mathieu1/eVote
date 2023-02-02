package Scrutateur;

import Cryptage.KeyGenCryptage;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;


public class Scrutateur {

    /**
     * Classe permettant l'exécution du scrutateur.
     * A chaque client connecté, il l'envoie dans un Thread Scrutateur.PublicKeySender
     * afin de pouvoir envoyer des informations à plusieurs clients
     *
     * @see PublicKeySender
     * @throws IOException lorsque le serveur ne peut pas être démarré
     */
    public static void main(String[] args) throws IOException {
        //Création d'un serveur sur le port 5057

        System.out.println("**********************************");
        System.out.println("*           Scrutateur           *");
        System.out.println("**********************************");

        ServerSocket scrutateurServer = new  ServerSocket(5057);
        System.out.println("Scrutateur lancé");
        System.out.println("**********************************");

        HashMap<String, KeyGenCryptage> keyGenCryptageHashMap = new HashMap<>();

        while (true) {

            Socket clientSocket = null;

            try {
                // socket object to receive incoming client requests
                clientSocket = scrutateurServer.accept();
                System.out.println("Nouveau client connecté : " + clientSocket);

                // obtaining input and out streams
                ObjectOutputStream oos = new ObjectOutputStream(clientSocket.getOutputStream());
                ObjectInputStream ois = new ObjectInputStream(clientSocket.getInputStream());

                //Créer un nouveau thread pour chaque client
                PublicKeySender t = new PublicKeySender(clientSocket, oos, ois, keyGenCryptageHashMap);
                t.start();

            } catch (Exception e) {
                assert clientSocket != null;
                clientSocket.close();
                e.printStackTrace();
            }
        }
    }
}
