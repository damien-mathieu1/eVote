package fr.umontpellier.View;

import fr.umontpellier.Controller.VueChoixVoteController;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Vue de choix de referendum.
 */
public class VueChoixVote extends Stage {

    /**
     * Constructeur de la vue de choix de referendum.
     * @param login Le login de l'utilisateur.
     * @param input Le flux d'entrée du serveur.
     * @param output Le flux de sortie vers le serveur.
     * @param stagePourStyle La fenêtre de style.
     */
    public VueChoixVote(String login, ObjectInputStream input, ObjectOutputStream output, Stage stagePourStyle) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/vueChoixVote.fxml"));
        try {
            this.setScene(new Scene(fxmlLoader.load()));
            this.resizableProperty().setValue(Boolean.FALSE);
            VueChoixVoteController controller = fxmlLoader.getController();
            controller.setLogin(login);
            controller.setInput(input);
            controller.setOutput(output);
            controller.init();
            controller.setStage(this);
            this.setOnCloseRequest(event -> {
                try {
                    output.close();
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            this.setTitle("Choix du vote");
            this.sizeToScene();
            this.getScene().getStylesheets().addAll(stagePourStyle.getScene().getStylesheets());
            this.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
