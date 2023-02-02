package fr.umontpellier.View;

import fr.umontpellier.Controller.VueVoteController;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.*;

/**
 * Vue de vote.
 */
public class VueVote extends Stage {

    /**
     * Constructeur de la vue de vote. Lance la fenêtre de vote si la date limite n'est pas dépassée. sinon, lance la fenêtre de résultat.
     * @param login Le login de l'utilisateur.
     * @param input Le flux d'entrée du serveur.
     * @param output Le flux de sortie vers le serveur.
     * @param stagePourStyle La fenêtre de style.
     * @throws IOException S'il y a un problème avec le serveur.
     */
    public VueVote(String login, ObjectInputStream input, ObjectOutputStream output, Stage stagePourStyle) throws IOException, ClassNotFoundException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/vueVoteModifiable.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        this.resizableProperty().setValue(Boolean.FALSE);
        VueVoteController controller = fxmlLoader.getController();
        controller.setLogin(login);

        controller.setInput(input);
        controller.setOutput(output);

        this.setOnCloseRequest(event -> {
            try {
                output.close();
                input.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        this.setScene(scene);
        this.getScene().getStylesheets().addAll(stagePourStyle.getScene().getStylesheets());

        String line = input.readUTF();

        if (line.equals("resultats")) {
            new VueResultats(login, input, output, this);
            this.close();
        } else {
            controller.init(line);
            controller.setStage(this);
            this.setTitle("Vote");
            this.sizeToScene();
            this.show();
        }
    }
}
