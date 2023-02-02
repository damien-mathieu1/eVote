package fr.umontpellier.Controller;

import Cryptage.MessageCrypte;
import Cryptage.PublicKey;

import fr.umontpellier.View.VueChoixVote;

import javafx.fxml.FXML;
import javafx.scene.control.ToggleButton;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;


/**
 * Controller de la vue de vote
 */
public class VueVoteController {

    /**
     * Le premier choix de vote.
     */
    @FXML
    public ToggleButton choix1, /**
     * Le deuxième choix de vote.
     */
    choix2;

    /**
     * L'intitulé du referendum.
     */
    @FXML
    public Text questionInterface, /**
     * L'utilisateur qui vote.
     */
    user;

    /**
     * Permet de savoir si l'utilisateur a voté ou non.
     */
    public static int vote = -1;

    /**
     * Flux d'entrée du serveur.
     */
    public ObjectInputStream entreeServeur;

    /**
     * Flux de sortie vers le serveur.
     */
    public ObjectOutputStream sortieVersServeur;

    /**
     * Login de l'utilisateur.
     */
    private String login;

    /**
     * Stage de la vue.
     */
    private Stage stage;

    /**
     * PublicKey du referendum.
     */
    private PublicKey publicKeyVote;

    /**
     * Setter du stage de la vue.
     *
     * @param stage le stage de la vue.
     */
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    /**
     * Setter du login de l'utilisateur.
     *
     * @param login le login de l'utilisateur.
     */
    public void setLogin(String login) {
        this.login = login;
    }

    /**
     * Initialise la vue.
     *
     * @param line the line
     */
    public void init(String line) {
        try {
            line = entreeServeur.readUTF();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String[] split = line.split(":", -1);
        questionInterface.setText(split[0]);
        choix1.setText(split[1]);
        choix2.setText(split[2]);

        PublicKey publicKey;
        try{
            publicKey = (PublicKey) entreeServeur.readObject();
            System.out.println("Public key received");
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        this.publicKeyVote = publicKey;

        user.setText(login);
    }

    /**
     * bouton de validation du vote.
     * recupère quel choix a été choisi et envoie le vote au serveur après l'avoir crypté grâce à la clé publique du referendum.
     */
    public void validerBouton() {
        if (choix1.isSelected()) {
            vote = 1;
        } else if (choix2.isSelected()) {
            vote = 0;
        } else {
            vote = -1;
        }
        if (vote != -1) {
            try {
                HashMap<String, MessageCrypte> infoVote = new HashMap<>();
                infoVote.put(login, new MessageCrypte(vote, publicKeyVote));
                sortieVersServeur.writeObject(infoVote);
                sortieVersServeur.flush();

                try {
                    sortieVersServeur.writeUTF("client");
                    sortieVersServeur.flush();
                    entreeServeur.readUTF();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                this.stage.close();
                new VueChoixVote(login,entreeServeur,sortieVersServeur, stage);

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * setter du flux de sortie vers le serveur.
     *
     * @param output le flux de sortie vers le serveur.
     */
    public void setOutput(ObjectOutputStream output) {
        sortieVersServeur = output;
    }

    /**
     * setter du flux d'entrée du serveur.
     *
     * @param input le flux d'entrée du serveur.
     */
    public void setInput(ObjectInputStream input) {
        entreeServeur = input;
    }
}
