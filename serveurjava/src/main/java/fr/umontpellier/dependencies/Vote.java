package fr.umontpellier.dependencies;

import Cryptage.MessageCrypte;

import java.time.LocalDateTime;
import java.util.HashMap;

/**
 * Classe Vote
 * Cette classe permet de stocker les informations d'un vote
 */
public class Vote implements java.io.Serializable {

    /**
     * Le nom du vote
     */
    private final String nom;
    /**
     * Question du vote
     */
    private final String question;

    /**
     * Choix 1 du vote
     */
    private final String choixUn;

    /**
     * Choix 2 du vote
     */
    private final String choixDeux;

    /**
     * Date de fin du vote
     */
    private final LocalDateTime dateDeFin;
    /**
     * Bulletins associant login et choix cryptés
     */
    private final HashMap<String, MessageCrypte> bulletins;

    /**
     * Resultat du vote si il est terminé, sinon null
     */
    private String resultat = "";


    /**
     * Constructeur de la classe Vote
     *
     * @param question  Question du vote
     * @param choixUn   Choix 1 du vote
     * @param choixDeux Choix 2 du vote
     * @param dateDeFin Date de fin du vote
     */
    public Vote(String nom, String question, String choixUn, String choixDeux, LocalDateTime dateDeFin) {
        this.nom = nom;
        this.question = question;
        this.choixUn = choixUn;
        this.choixDeux = choixDeux;
        this.dateDeFin = dateDeFin;
        this.resultat = "";
        this.bulletins = new HashMap<>();
    }

    /**
     * Getter de la question du vote
     *
     * @return Question du vote
     */
    public String getQuestion() {
        return question;
    }

    /**
     * Getter de choix 1 du vote
     *
     * @return Choix 1 du vote
     */
    public String getChoixUn() {
        return choixUn;
    }

    /**
     * Getter de choix 2 du vote
     *
     * @return Choix 2 du vote
     */
    public String getChoixDeux() {
        return choixDeux;
    }

    /**
     * Getter de la date de fin du vote
     *
     * @return Date de fin du vote
     */
    public LocalDateTime getDateDeFin() {
        return dateDeFin;
    }

    /**
     * toString de la classe Vote, affiche les informations du vote sous forme de String formaté pour l'affichage dans le GUI
     *
     * @return String formaté pour l'affichage dans le GUI
     */
    public String toString() {
        return question + ":" + choixUn + ":" + choixDeux;
    }

    public String getVoteString() {
        return nom + ":" + question + ":" + choixUn + ":" + choixDeux;
    }

    public String resultatVotePourGUI(int message, int nombreDeVotes) {
        return this.question + ":" +
                this.choixUn + ":" +
                this.choixDeux + ":" +
                message + ":" +
                nombreDeVotes;
    }

    public synchronized void addVote(HashMap<String, MessageCrypte> bulletin) {
        bulletins.putAll(bulletin);
    }

    public String getResultat() {
        return resultat;
    }

    public String getNom() {
        return nom;
    }
}
