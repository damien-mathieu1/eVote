package Scrutateur;

import Cryptage.KeyGenCryptage;
import Cryptage.MessageCrypte;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashMap;

/**
 * Scrutateur.PublicKeySender est une classe qui étend Thread.
 * Elle permet de :
 * <ul>
 *     <li>Envoyer une clé publique au serveur ou au client lorsqu'il le demande</li>
 *     <li>Recevoir un message crypté, le déchiffrer, et le renvoyer en clair</li>
 * </ul>
 */
public class PublicKeySender extends Thread {

    final ObjectInputStream ois;
    final ObjectOutputStream oos;
    final Socket s;
    private final HashMap<String, KeyGenCryptage> keygen;


    /**
     * Constructeur de la classe Scrutateur.PublicKeySender
     *
     * @param s      Socket correspondant au client
     * @param oos    ObjectOutputStream permettant d'envoyer des informations au client
     * @param ois    ObjectInputStream permettant de recevoir des informations du client
     * @param keygen KeyGenCryptage contenant la clé publique et privée du cryptage
     */
    public PublicKeySender(Socket s, ObjectOutputStream oos, ObjectInputStream ois, HashMap<String, KeyGenCryptage> keygen) {
        this.s = s;
        this.ois = ois;
        this.oos = oos;
        this.keygen = keygen;
    }

    /**
     * Méthode implémentée de Thread, permettant de lancer le thread
     * Elle permet de :
     * <ul>
     *     <li>Envoyer une clé publique au serveur ou au client lorsqu'il le demande</li>
     *     <li>Recevoir un message crypté, le déchiffrer, et le renvoyer en clair</li>
     * </ul>
     */
    @Override
    public void run() {

            try {
                String line = ois.readUTF();

                if (line.equals("newvote")) {
                    //envoyer la clé publique
                    String nom = ois.readUTF();
                    if (keygen.containsKey(nom)) {
                        oos.writeObject(keygen.get(nom).getPublicKey());
                    } else {
                        KeyGenCryptage keyGenCryptage = new KeyGenCryptage(100);
                        keygen.put(nom, keyGenCryptage);
                        oos.writeObject(keyGenCryptage.getPublicKey());
                        oos.flush();
                        System.out.println("nouvelle clé crée pour le vote: " + nom);
                    }
                } else if (line.equals("decrypt")) {
                    String nom = ois.readUTF();
                    MessageCrypte messageCrypte = (MessageCrypte) ois.readObject();
                    int message = messageCrypte.decrypt(keygen.get(nom).getPrivateKey(), keygen.get(nom).getPublicKey());
                    oos.writeObject(message);
                    oos.flush();
                }

            } catch (IOException | ClassNotFoundException ex) {
                throw new RuntimeException(ex);
            }




        try {// closing resources
            this.oos.close();

        } catch (IOException e) {
            e.printStackTrace();
        }


    }

}
