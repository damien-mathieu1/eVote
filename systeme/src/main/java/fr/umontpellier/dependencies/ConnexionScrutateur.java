package fr.umontpellier.dependencies;

import Cryptage.MessageCrypte;
import Cryptage.PublicKey;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Classe permettant de gérer la connexion d'un scrutateur
 */
public class ConnexionScrutateur {

    /**
     * L'adresse du scrutateur
     */
    final static String SCRUTATEUR_IP = "localhost";

    /**
     * Le port du scrutateur
     */
    final static int SCRUTATEUR_PORT = 5057;

    /**
     * Méthode permettant de récupérer le résultat d'un vote à partir du nom du vote
     *
     * @param nom           Nom du vote
     * @param messageCrypte Message crypté du vote
     * @return the message decrypte
     */
    public static int getMessageDecrypte(String nom, MessageCrypte messageCrypte) {
        Socket connectionAuScrutateur;

        ObjectInputStream inputScrutateur;
        ObjectOutputStream outputScrutateur;

        int messageDecrypte = 0;

        try {
            connectionAuScrutateur = new Socket(SCRUTATEUR_IP, SCRUTATEUR_PORT);

            inputScrutateur = new ObjectInputStream(connectionAuScrutateur.getInputStream());
            outputScrutateur = new ObjectOutputStream(connectionAuScrutateur.getOutputStream());

            outputScrutateur.writeUTF("decrypt");
            outputScrutateur.flush();

            outputScrutateur.writeUTF(nom);
            outputScrutateur.flush();

            outputScrutateur.writeObject(messageCrypte);
            outputScrutateur.flush();

            messageDecrypte = (Integer) inputScrutateur.readObject();

            connectionAuScrutateur.close();
            outputScrutateur.close();
            inputScrutateur.close();

        } catch (IOException e) {
            System.err.println("Impossible de déchiffrer les votes: Erreur de connexion au scrutateur. Veuillez vérifier que le scrutateur est en ligne.");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }


        return messageDecrypte;
    }

    /**
     * Méthode permettant de récupérer la clé publique d'un vote à partir du nom du vote
     *
     * @param nom Nom du vote
     * @return the clé publique du vote
     */
    public static PublicKey getPublicKey(String nom) {
        Socket connectionAuScrutateur;

        ObjectInputStream inputScrutateur;
        ObjectOutputStream outputScrutateur;

        PublicKey publicKey = null;

        try {
            connectionAuScrutateur = new Socket(SCRUTATEUR_IP, SCRUTATEUR_PORT);

            inputScrutateur = new ObjectInputStream(connectionAuScrutateur.getInputStream());
            outputScrutateur = new ObjectOutputStream(connectionAuScrutateur.getOutputStream());

            outputScrutateur.writeUTF("newvote");
            outputScrutateur.flush();

            outputScrutateur.writeUTF(nom);
            outputScrutateur.flush();

            publicKey = (PublicKey) inputScrutateur.readObject();

            connectionAuScrutateur.close();
            outputScrutateur.close();
            inputScrutateur.close();

        } catch (IOException e) {
            System.err.println("Erreur de connexion au scrutateur. Veuillez vérifier que le scrutateur est en ligne.");
            System.exit(1);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }


        return publicKey;
    }

}
