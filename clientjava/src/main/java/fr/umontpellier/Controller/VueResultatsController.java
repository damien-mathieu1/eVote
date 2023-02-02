package fr.umontpellier.Controller;

import fr.umontpellier.View.VueChoixVote;
import javafx.fxml.FXML;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.*;

/**
 * Controller de la vue des résultats concernant un referendum precis
 */
public class VueResultatsController {
    /**
     * 'Progress bar' (personalisée) pour le choix 1
     */
    @FXML
    Rectangle progress1, /**
     * 'Progress bar' (personalisée) pour le choix 2
     */
    progress2;

    /**
     * Texte qui contient la question du referendum
     */
    @FXML
    Text question, /**
     * Texte qui contient le choix 1
     */
    choix1txt, /**
         * Texte qui contient le choix 2
         */

    choix2txt, /**
     * Le nombre de votes pour le choix 1
     */
    nbVotesChoix1, /**
     * Le nombre de votes pour le choix 2
     */
    nbVotesChoix2;

    /**
     * Stage de la vue.
     */
    private Stage stage;

    /**
     * Le flux d'entrée du serveur.
     */
    private ObjectInputStream entreeServeur;

    /**
     * Le flux de sortie vers le serveur.
     */
    private ObjectOutputStream output;

    /**
     * Le login de l'utilisateur.
     */
    private String login;

    /**
     * Setter du stage de la vue.
     *
     * @param stage the stage
     */
    public void setStage(Stage stage) {
        this.stage = stage;
    }


    /**
     * Fonction qui initialise la vue. Affiche les résultats du referendum.
     */
    public void init() {
        String resultats = "";
        try {
            resultats = entreeServeur.readUTF();
        } catch (Exception e) {
            e.printStackTrace();
        }
        String[] resultatsTab = resultats.split(":");
        String questionTxt = resultatsTab[0];
        String choix1 = resultatsTab[1];
        String choix2 = resultatsTab[2];
        String nbVotesChoix1 = resultatsTab[3];
        String nbVotants = resultatsTab[4];

        question.setText(questionTxt);
        choix1txt.setText(choix1);
        choix2txt.setText(choix2);

        double v = Double.parseDouble(nbVotesChoix1) / Double.parseDouble(nbVotants);

        progress1.setWidth(v * 303);
        progress2.setWidth((Math.abs(1 - v)) * 303);

        this.nbVotesChoix1.setText(nbVotesChoix1);
        this.nbVotesChoix2.setText(Integer.toString(Integer.parseInt(nbVotants) - Integer.parseInt(nbVotesChoix1)));

        if (v > 0.5) {
            progress1.setId("progressGagne");
            progress2.setId("progressPerdu");
        } else if (v == 0.5) {
            progress1.setId("progressGagne");
            progress2.setId("progressGagne");
        } else {
            progress2.setId("progressGagne");
            progress1.setId("progressPerdu");
        }
    }

    /**
     * Bouton 'ok' pour revenir à la vue de choix de vote.
     */
    public void exitButton() {
        try {
            output.writeUTF("client");
            output.flush();
            entreeServeur.readUTF();
        } catch (IOException e) {
            e.printStackTrace();
        }

        new VueChoixVote(login, entreeServeur, output, stage);
        stage.close();
    }

    /**
     * Setter du flux d'entrée du serveur.
     *
     * @param entreeServeur flux d'entrée du serveur
     */
    public void setInput(ObjectInputStream entreeServeur) {
        this.entreeServeur = entreeServeur;
    }

    /**
     * Setter du flux de sortie vers le serveur.
     *
     * @param output flux de sortie vers le serveur
     */
    public void setOutput(ObjectOutputStream output) {
        this.output = output;
    }

    /**
     * Setter du login de l'utilisateur.
     *
     * @param login le login de l'utilisateur
     */
    public void setLogin(String login) {
        this.login = login;
    }
}
