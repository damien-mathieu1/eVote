package fr.umontpellier.server;// Java implementation of fr.umontpellier.server.Server side
// It contains two classes : fr.umontpellier.server.Server and fr.umontpellier.server.ClientHandler
// Save file as fr.umontpellier.server.Server.java

import Cryptage.MessageCrypte;
import fr.umontpellier.dependencies.ConnexionScrutateur;
import fr.umontpellier.dependencies.Referendum;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;


/**
 * Classe qui gère le serveur
 */
public class Systeme {

    /**
     * Méthode principale du serveur
     */
    public static void main(String[] args) throws IOException {
        runServer();
    }

    /**
     * Méthode principale du serveur, elle permet de lancer le serveur et de gérer les connexions entrantes pour le déroulement des référendums
     *
     * @throws IOException the io exception
     */
    public static void runServer() throws IOException {

        System.out.println("***********************************");
        System.out.println("*             Système             *");
        System.out.println("***********************************");
        System.out.println("Serveur démarré");

        ServerSocket serveur = null;
        try {
            serveur = new ServerSocket(5056);
        } catch (IOException e) {
            System.out.println("Impossible de démarrer le serveur");
            System.exit(1);
        }

        Socket client;


        ArrayList<Referendum> listeDeReferendums = new ArrayList<>();

        //Création des référendums d'exemple:
        Referendum referendum = new Referendum("Le retrait des frites à la cantine le jeudi", "Voulez vous toujours des frites à la cantine le jeudi?", "Oui", "Non", LocalDateTime.now().plusSeconds(10));
        referendum.setPublicKey(ConnexionScrutateur.getPublicKey(referendum.getNom()));

        HashMap<String, MessageCrypte> bulletins = new HashMap<>();
        bulletins.put("aaaa", new MessageCrypte(0, referendum.getPublicKey()));
        bulletins.put("aaaaaaa", new MessageCrypte(1, referendum.getPublicKey()));
        bulletins.put("zdefgvrbfgcvdfg", new MessageCrypte(1, referendum.getPublicKey()));
        bulletins.put("zdefgvrbfgcvdfzdfgg", new MessageCrypte(1, referendum.getPublicKey()));
        bulletins.put("sdfvdfgdfvd", new MessageCrypte(1, referendum.getPublicKey()));
        bulletins.put("sfgegefvdfvdfvdfvdv", new MessageCrypte(1, referendum.getPublicKey()));

        referendum.addVote(bulletins);

        Referendum referendum1 = new Referendum("Un menu végan tout les jours", "Voulez vous un menu végan tout les jours?", "Oui", "Non", LocalDateTime.now().plusSeconds(30));

        listeDeReferendums.add(referendum);

        referendum1.setPublicKey(ConnexionScrutateur.getPublicKey(referendum1.getNom()));
        listeDeReferendums.add(referendum1);

        //Boucle infinie pour gérer les connexions entrantes
        while (true) {
            client = serveur.accept();
            System.out.println("Nouvelle connexion entrante : " + client);

            ObjectOutputStream output = new ObjectOutputStream(client.getOutputStream());
            ObjectInputStream input = new ObjectInputStream(client.getInputStream());

            LoginHandler loginHandler = new LoginHandler(client, output, input, listeDeReferendums);
            loginHandler.start();
        }

    }

}

