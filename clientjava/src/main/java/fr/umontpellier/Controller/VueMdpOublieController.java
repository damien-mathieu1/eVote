package fr.umontpellier.Controller;


import fr.umontpellier.View.VueChangerMdp;
import fr.umontpellier.View.VueLogin;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.stage.Stage;


import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;


/**
 * Controller de la vue de changement de mot de passe
 */
public class VueMdpOublieController {

    /**
     * Le flux d'entrée du serveur.
     */
    public ObjectInputStream entreeServeur;

    /**
     * Le flux de sortie vers le serveur.
     */
    public ObjectOutputStream sortieVersServeur;

    /**
     * Textfield pour le login.
     */
    @FXML
    public TextField loginResetPassword;

    /**
     * Message d'erreur s'il y a une erreur.
     */
    @FXML
    public Text textInfo;

    /**
     * Image d'erreur si il y a une erreur.
     */
    @FXML
    public ImageView exclamationErreur;

    /**
     * Stage de la vue.
     */
    private Stage stage;

    /**
     * Setter du stage de la vue.
     *
     * @param stage le stage de la vue.
     */
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    /**
     * Setter du flux d'entrée du serveur.
     *
     * @param input  le flux d'entrée du serveur.
     * @param output le flux de sortie du serveur.
     */
    public void setCoServ(ObjectInputStream input, ObjectOutputStream output) {
        this.entreeServeur = input;
        this.sortieVersServeur = output;
    }

    /**
     * S'exécute lorsque l'utilisateur clique sur le bouton "retour".
     * retourne à la vue de login.
     *
     * @throws IOException si la connexion au serveur échoue.
     */
    @FXML
    public void boutonRetour() throws IOException {
        sortieVersServeur.writeObject("backLogin");
        sortieVersServeur.flush();
        new VueLogin(stage);
        this.stage.close();


    }

    /**
     * S'exécute lorsque l'utilisateur clique sur le bouton "envoyer".
     * Demande au serveur d'envoyer un mail de réinitialisation de mot de passe.
     *
     * @throws IOException si la connexion au serveur échoue.
     */
    @FXML
    public void receiveCode() throws IOException {
        if (loginResetPassword.getText().isEmpty()){
            textInfo.setText("Veuillez entrer un login");
            exclamationErreur.setVisible(true);
        }
        else {
            String message = "envoiCode";
            sortieVersServeur.writeUTF(message);
            sortieVersServeur.flush();
            sortieVersServeur.writeUTF(loginResetPassword.getText());
            sortieVersServeur.flush();
            if(entreeServeur.readUTF().equals("codeEnvoye")) {
                textInfo.setText("Code envoyé");
                exclamationErreur.setVisible(true);
            }
            else {
                textInfo.setText("Nom d'utilisateur invalide");
                exclamationErreur.setVisible(true);
            }
        }
    }

    /**
     * S'exécute lorsque l'utilisateur clique sur le bouton "vous avez un code?".
     * Ouvre la vue de changement de mot de passe si le login de l'utilisateur est valide.
     *
     * @throws IOException si la connexion au serveur échoue.
     */
    @FXML
    public void rentrerCode() throws IOException {
        if (loginResetPassword.getText().isEmpty()){
            exclamationErreur.setVisible(true);
            textInfo.setText("Veuillez entrer un nom d'utilisateur");
        } else{
            String message = "rentrerCode";
            sortieVersServeur.writeUTF(message);
            sortieVersServeur.flush();
            sortieVersServeur.writeUTF(loginResetPassword.getText());
            sortieVersServeur.flush();
            if (entreeServeur.readUTF().equals("loginOk")) {
                sortieVersServeur.writeUTF("changermdp");
                sortieVersServeur.flush();
                new VueChangerMdp(loginResetPassword.getText(), stage, entreeServeur, sortieVersServeur);
                this.stage.close();
            }
            else {
                exclamationErreur.setVisible(true);
                textInfo.setText("Nom d'utilisateur invalide");
            }

        }/*else {
            if (checkLoginBd()==1){
                VueChangerMdp vueChangerMdp = new VueChangerMdp(loginResetPassword.getText(), this.stage);
                this.stage.close();
            }
            else{
                exclamationErreur.setVisible(true);
                textInfo.setText("Login incorrect");
            }*/

        //}
    }
}
