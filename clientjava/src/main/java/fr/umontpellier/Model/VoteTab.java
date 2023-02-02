package fr.umontpellier.Model;

import javafx.beans.property.SimpleStringProperty;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * Classe qui représente un référendum, utilisée pour la table des référendums dans la vue choix vote
 */
public class VoteTab {

    /**
     * Nom du référendum
     */
    private SimpleStringProperty nomVote;

    /**
     * Temps restant pour voter
     */
    private SimpleStringProperty tempsRestant;

    /**
     * date de fin du référendum
     */
    private LocalDateTime dateDeFin;

    /**
     * Constructeur de la classe VoteTab
     *
     * @param nomVote      Nom du référendum
     * @param tempsRestant Temps restant pour voter
     */
    public VoteTab(String nomVote, String tempsRestant) {
        this.nomVote = new SimpleStringProperty(nomVote);
        this.dateDeFin = LocalDateTime.parse(tempsRestant);
        this.tempsRestant = new SimpleStringProperty(Long.toString(dateDeFin.until(LocalDateTime.now(), ChronoUnit.SECONDS)));
        updateTempsRestant();
    }

    /**
     * Méthode qui met à jour le temps restant pour voter sur le référendum en fonction de la date de fin
     */
    public void updateTempsRestant(){
        //remaining time
        long seconds = LocalDateTime.now().until(dateDeFin, ChronoUnit.SECONDS);
        if(seconds < 0){
            tempsRestant.set("Terminé");
        }else{
            //display remaining time in format hh:mm:ss
            //if remaining time is more than  than 1 day, display only days
            //if remaining time is less than 1 hour, display only minutes and seconds
            //if remaining time is less than 1 minute, display only seconds

            if(seconds > 86400){
                tempsRestant.set(seconds / 86400 + " jours");
            }else if(seconds < 3600){
                tempsRestant.set(seconds / 60 + " minutes " + seconds % 60 + " secondes");
            }else{
                tempsRestant.set(seconds / 3600 + " heures " + (seconds % 3600) / 60 + " minutes");
            }
        }

    }

    public String getNomVote() {
        return nomVote.get();
    }

    public String getTempsRestant() {
        return tempsRestant.get();
    }

    public void setNomVote(String nomVote) {
        this.nomVote.set(nomVote);
    }

    public void setTempsRestant(String tempsRestant) {
        this.tempsRestant.set(tempsRestant);
    }

    public SimpleStringProperty nomVoteProperty() {
        return nomVote;
    }

    public SimpleStringProperty tempsRestantProperty() {
        return tempsRestant;
    }

    @Override
    public String toString() {
        return nomVote.get();
    }
}