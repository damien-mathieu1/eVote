package fr.umontpellier.Controller;

import fr.umontpellier.Model.Hachage;
import fr.umontpellier.View.VueChoixVote;
import fr.umontpellier.View.VueMdpOublie;
import fr.umontpellier.View.VueVote;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Objects;

import static java.lang.System.exit;

/**
 * Controller de la vue de changement de mot de passe
 */
public class VueLoginController {
    /**
     * Textfield pour le login.
     */
    @FXML
    public TextField login,

    /**
     * TextField pour le mot de passe.
     */
    password;

    /**
     * Image d'erreur si il y a une erreur.
     */
    @FXML
    public ImageView imageAttention;

    /**
     * Texte
     */
    @FXML
    private Text textInfo, compteurEssaie,messageEssais;

    /**
     * ComboBox pour le choix du thème.
     */
    @FXML
    private ComboBox<String> comboBoxTheme;

    private int EssaieRestant;

    /**
     * Stage de la vue.
     */
    private Stage stage;

    /**
     * IP du serveur.
     */
    final static String SERVER_URNE_IP = "localhost";

    /**
     * Socket de connexion au serveur.
     */
    private Socket connectionAuServeur = null;

    /**
     * Le flux d'entrée du serveur.
     */
    public ObjectInputStream entreeServeur = null;

    /**
     * Le flux de sortie vers le serveur.
     */
    public ObjectOutputStream sortieVersServeur = null;

    /**
     * Setteur du stage de la vue.
     *
     * @param stage le stage de la vue.
     */
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    /**
     * setter du nombre d'essais restant.
     */
    public void setEssaiInit() {
        String s = Integer.toString(EssaieRestant);
        compteurEssaie.setText(s);
    }

    /**
     * Fonction qui lance la vue de changement de mot de passe.
     * lorsque l'utilisateur clique sur le bouton "Mot de passe oublié".
     *
     * @throws IOException si le serveur n'est pas joignable.
     */
    @FXML
    public void changeMdp() throws IOException {
        sortieVersServeur.writeUTF("mdpoublie");
        sortieVersServeur.flush();
        new VueMdpOublie(stage, entreeServeur, sortieVersServeur);
        this.stage.close();
    }

    /**
     * Fonction qui lance l'identification de l'utilisateur au serveur.
     * Si l'identification est réussie, on lance la vue de vote.
     * Sinon, on affiche un message d'erreur.
     */
    @FXML
    public void connexionBouton() {
        try {
            sortieVersServeur.writeUTF("login");
            sortieVersServeur.flush();
            sortieVersServeur.writeUTF(login.getText() + ":" + Hachage.hachage(password.getText()));
            sortieVersServeur.flush();

            String line = entreeServeur.readUTF();
            switch (line) {
                case "connexionauthorise" -> {
                    sortieVersServeur.writeUTF("client");
                    sortieVersServeur.flush();
                    line = entreeServeur.readUTF();
                    if (line.equals("vote")) {
                        new VueVote(login.getText(), entreeServeur, sortieVersServeur, stage);
                        this.stage.close();
                    } else if (line.equals("plusieursvotes")) {
                        new VueChoixVote(login.getText(), entreeServeur, sortieVersServeur, stage);
                        this.stage.close();
                    } else {
                        System.out.println("erreur");
                    }
                }
                case "badlogin" -> {
                    textInfo.setText("Login incorrect");
                    textInfo.setVisible(true);
                    imageAttention.setStyle("-fx-opacity: 1");
                }
                case "badpassword" -> {
                    textInfo.setText("Mot de passe incorrect");
                    textInfo.setVisible(true);
                    imageAttention.setStyle("-fx-opacity: 1");
                    line = entreeServeur.readUTF();
                    System.out.println(line);
                    messageEssais.setVisible(true);
                    compteurEssaie.setText(line);
                    compteurEssaie.setVisible(true);
                    imageAttention.setStyle("-fx-opacity: 1");
                }

                case "bloque" ->{
                    textInfo.setText("Vous avez été bloqué pendant 5min");
                    textInfo.setVisible(true);
                    imageAttention.setStyle("-fx-opacity: 1");
                    compteurEssaie.setVisible(false);
                    messageEssais.setVisible(false);
                }

                //TODO : ajouter un compteur d'essai
            }

        } catch (IOException | NoSuchAlgorithmException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Fonction exécutée lorsqu'un thème est sélectionné dans la combobox.
     * Elle change le thème de l'application.
     */
    @FXML
    public void themeSelection() {
        String theme = comboBoxTheme.getValue();
        switch (theme) {
            case "Dark" -> {
                stage.getScene().getStylesheets().clear();
                stage.getScene().getStylesheets().add(Objects.requireNonNull(getClass().getResource("/css/styledark.css")).toExternalForm());
            }
            case "Light" -> {
                stage.getScene().getStylesheets().clear();
                stage.getScene().getStylesheets().add(Objects.requireNonNull(getClass().getResource("/css/style.css")).toExternalForm());
            }
            case "Sea" -> {
                stage.getScene().getStylesheets().clear();
                stage.getScene().getStylesheets().add(Objects.requireNonNull(getClass().getResource("/css/styleSea.css")).toExternalForm());
            }
            case "Rose" -> {
                stage.getScene().getStylesheets().clear();
                stage.getScene().getStylesheets().add(Objects.requireNonNull(getClass().getResource("/css/stylePink.css")).toExternalForm());
            }
        }
    }

    /**
     * Fonction qui initialise la vue.
     * Elle initialise la combobox des thèmes.
     * Elle essaye de se connecter au serveur.
     * Si la connexion échoue, on affiche un message d'erreur.
     */
    public void init() {
        ArrayList<String> themes = new ArrayList<>();
        themes.add("Light");
        themes.add("Dark");
        themes.add("Sea");
        themes.add("Rose");
        comboBoxTheme.setItems(FXCollections.observableList(themes));

        try {
            connectionAuServeur = new Socket(SERVER_URNE_IP, 5056);
            sortieVersServeur = new ObjectOutputStream(connectionAuServeur.getOutputStream());
            entreeServeur = new ObjectInputStream(connectionAuServeur.getInputStream());
        } catch (IOException e) {
            //javafx warning alert
            Alert alert = new Alert(Alert.AlertType.ERROR);

            alert.setTitle("Erreur de connexion");
            alert.setHeaderText("Serveur non trouvé");
            alert.setContentText("Le serveur n'est pas disponible. Vérifiez votre connexion internet ou contactez l'administrateur du service.");

            alert.showAndWait();
            exit(0);
        }

    }
}
