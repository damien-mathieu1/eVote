package fr.umontpellier.dependencies;

import Cryptage.MessageCrypte;
import Cryptage.PublicKey;

import java.time.LocalDateTime;
import java.util.HashMap;

import static fr.umontpellier.dependencies.ConnexionScrutateur.getMessageDecrypte;

/**
 * Classe Referendum
 * Cette classe permet de stocker les informations d'un vote
 */
public class Referendum implements java.io.Serializable {

    /**
     * Le nom du Referendum
     */
    private final String nom;
    /**
     * Question du Referendum
     */
    private final String question;

    /**
     * Choix 1 du referendum
     */
    private final String choixUn;

    /**
     * Choix 2 du referendum
     */
    private final String choixDeux;

    /**
     * Date de fin du referendum
     */
    private final LocalDateTime dateDeFin;

    /**
     * Bulletins associant login et choix cryptés
     */
    private final HashMap<String, MessageCrypte> bulletins;

    /**
     * Resultat du referendum si il est terminé, sinon null
     */
    private String resultat;

    /**
     * Clé publique du vote
     */
    private PublicKey publicKey;


    /**
     * Constructeur de la classe referendum
     *
     * @param nom       the nom
     * @param question  Question du vote
     * @param choixUn   Choix 1 du vote
     * @param choixDeux Choix 2 du vote
     * @param dateDeFin Date de fin du vote
     */
    public Referendum(String nom, String question, String choixUn, String choixDeux, LocalDateTime dateDeFin) {
        this.nom = nom;
        this.question = question;
        this.choixUn = choixUn;
        this.choixDeux = choixDeux;
        this.dateDeFin = dateDeFin;
        this.resultat = "";
        this.bulletins = new HashMap<>();
    }

    /**
     * Getter de la date de fin du referendum
     *
     * @return Date de fin du referendum
     */
    public LocalDateTime getDateDeFin() {
        return dateDeFin;
    }

    /**
     * toString de la classe referendum, affiche les informations du referendum sous forme de String formaté pour l'affichage dans le GUI
     *
     * @return String formaté pour l'affichage dans le GUI
     */
    public String toString() {
        return question + ":" + choixUn + ":" + choixDeux;
    }

    /**
     * Resultat du referendum si il est terminé, sinon null : formaté pour l'affichage dans le GUI
     *
     * @param message       the message
     * @param nombreDeVotes the nombre de votes
     * @return the string
     */
    public String resultatVotePourGUI(int message, int nombreDeVotes) {
        return this.question + ":" +
                this.choixUn + ":" +
                this.choixDeux + ":" +
                message + ":" +
                nombreDeVotes;
    }

    /**
     * Calcule les résultats du referendum si pas encore fait
     */
    public void calculerResultats() {
        if (this.resultat.equals("")) {
            int nombreDeVotes = bulletins.size();
            MessageCrypte messageCrypte = new MessageCrypte(0, publicKey);

            //agregation de tout les votes
            for (String key : bulletins.keySet()) {
                messageCrypte = messageCrypte.agregate(bulletins.get(key), publicKey);
            }

            //decryptage du message
            int message = getMessageDecrypte(nom, messageCrypte);

            this.resultat = resultatVotePourGUI(message, nombreDeVotes);
        }

    }

    /**
     * Ajoute un bulletin de vote au referendum
     *
     * @param bulletin the bulletin
     */
    public synchronized void addVote(HashMap<String, MessageCrypte> bulletin) {
        bulletins.putAll(bulletin);
    }

    /**
     * Getter du resultat du referendum
     *
     * @return le resultat
     */
    public String getResultat() {
        return resultat;
    }

    /**
     * Getter du nom du referendum
     *
     * @return le nom
     */
    public String getNom() {
        return nom;
    }

    /**
     * Setter de la clé publique du referendum
     *
     * @param publicKey la clé publique
     */
    public void setPublicKey(PublicKey publicKey) {
        this.publicKey = publicKey;
    }

    /**
     * Getter de la clé publique du referendum
     *
     * @return La clé publique
     */
    public PublicKey getPublicKey() {
        return publicKey;
    }
}
