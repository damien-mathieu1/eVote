package fr.umontpellier.Controller;

import fr.umontpellier.Model.Hachage;
import fr.umontpellier.View.VueLogin;
import fr.umontpellier.View.VueMdpOublie;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Pattern;

/**
 * Controller de la vue de changement de mot de passe
 */
public class vueChangerMdpController {

    /**
     * Le flux d'entrée du serveur.
     */
    public ObjectInputStream entreeServeur;

    /**
     * Le flux de sortie vers le serveur.
     */
    public ObjectOutputStream sortieVersServeur;

    /**
     * Textfield pour le code de vérification reçu par mail.
     */
    @FXML
    public TextField codeMail,
    /**
     * TextField pour le nouveau mot de passe.
     */
    password,
    /**
     * TextField pour la confirmation du nouveau mot de passe.
     */
    passwordRetype;

    /**
     * Message d'erreur si il y a une erreur.
     */
    @FXML
    public Text messageErreur;

    /**
     * Image d'erreur si il y a une erreur.
     */
    @FXML
    public ImageView exclamationErreur;

    /**
     * Login de l'utilisateur.
     */
    public String login;

    /**
     * Stage de la fenêtre.
     */
    private Stage stage;

    /**
     * Setter du stage
     *
     * @param stage le stage
     */
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    /**
     * Setter des flux d'entrée et de sortie.
     *
     * @param input  the input
     * @param output the output
     */
    public void setCoServ(ObjectInputStream input, ObjectOutputStream output) {
        this.entreeServeur = input;
        this.sortieVersServeur = output;
    }

    /**
     * Setter du login.
     *
     * @param login the login
     */
    public void setLogin(String login) {
        this.login = login;
    }

    /**
     * Fonction lancée lors du clic sur le bouton "retour". Permets de revenir à la fenêtre de demande de code.
     *
     * @throws IOException Si la connexion au serveur échoue.
     */
    @FXML
    public void boutonRetour() throws IOException {
        sortieVersServeur.writeUTF("backMdpOublie");
        sortieVersServeur.flush();
        sortieVersServeur.writeUTF("mdpoublie");
        sortieVersServeur.flush();
        new VueMdpOublie(stage, entreeServeur, sortieVersServeur);
        this.stage.close();
    }

    /**
     * Fonction lancée lors du clic sur le bouton "valider". Permets de changer le mot de passe de l'utilisateur.
     *
     * @throws NoSuchAlgorithmException Si l'algorithme de hachage n'existe pas.
     * @throws IOException              Si la connexion au serveur échoue.
     */
    @FXML
    public void changePassword() throws NoSuchAlgorithmException, IOException {
        sortieVersServeur.writeUTF("changePassword");
        sortieVersServeur.flush();
        sortieVersServeur.writeUTF(login);
        sortieVersServeur.flush();
        String code = entreeServeur.readUTF();
        if (code.equals("pasdecode")) {
            sortieVersServeur.writeUTF("fin");
            sortieVersServeur.flush();
            exclamationErreur.setVisible(true);
            messageErreur.setText("Pas de code disponible pour le compte " + login);
        } else if (codeMail.getText().isEmpty()) {
            sortieVersServeur.writeUTF("fin");
            sortieVersServeur.flush();
            exclamationErreur.setVisible(true);
            messageErreur.setText("Rentrez un code");
        } else if (!code.equals(Hachage.hachage(codeMail.getText()))) {
            sortieVersServeur.writeUTF("fin");
            sortieVersServeur.flush();
            exclamationErreur.setVisible(true);
            messageErreur.setText("Code incorrect");
        } else if (password.getText().equals(passwordRetype.getText())) {
            if (checkPasswordStrenght(password.getText())) {
                sortieVersServeur.writeUTF("changementMdp");
                sortieVersServeur.writeUTF(Hachage.hachage(password.getText()));
                sortieVersServeur.flush();
                String reponse = entreeServeur.readUTF();
                if (reponse.equals("ok")) {
                    messageErreur.setText("Mot de passe changé");
                    exclamationErreur.setVisible(true);
                    new VueLogin();
                    this.stage.close();
                } else {
                    exclamationErreur.setVisible(true);
                    messageErreur.setText("Erreur lors du changement de mot de passe");
                }
            }
            else {
                sortieVersServeur.writeUTF("fin");
                sortieVersServeur.flush();
                //*CODE POUR AFFICHER UN POP UP EXPLIQUANT LES CONTRAINTES DU MDP*//*
                final Stage dialog = new Stage();
                dialog.initOwner(stage);
                VBox dialogVbox = new VBox(20);
                dialogVbox.getChildren().add(new Text("Le mot de passe doit contenir :\n● Au moins 8 caractères\n● Au moins une majuscule\n● Au moins une minuscule\n● Au moins un chiffre\n● Au moins un caractère spécial"));
                Scene dialogScene = new Scene(dialogVbox, 215, 100);
                dialogVbox.setStyle("-fx-background-color: #FFFFFF;");
                dialogVbox.getChildren().get(0).setStyle("-fx-background-color: red;");
                dialog.setResizable(false);
                dialog.setTitle("Erreur mot de passe");
                dialog.setScene(dialogScene);
                dialog.show();
                exclamationErreur.setVisible(true);
                messageErreur.setText("Mot de passe trop faible");
            }
        }
        else{
            sortieVersServeur.writeUTF("fin");
            sortieVersServeur.flush();
            exclamationErreur.setVisible(true);
            messageErreur.setText("Les mots de passe ne correspondent pas");
        }
}


    /**
     * Fonction qui vérifie la force du mot de passe.
     *
     * @param pass le mot de passe
     * @return true si le mot de passe est assez fort, false sinon
     */
    public boolean checkPasswordStrenght(String pass) {
        return Pattern.compile("^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$%^&*-.]).{8,}$").matcher(pass).find();
    }


}
