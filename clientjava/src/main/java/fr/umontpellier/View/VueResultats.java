package fr.umontpellier.View;

import fr.umontpellier.Controller.VueResultatsController;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Vue des resultats d'un referendum
 */
public class VueResultats extends Stage {

    /**
     * Constructeur de la vue des resultats d'un referendum
     * @param login Le login de l'utilisateur
     * @param entreeServeur Le flux d'entrée du serveur
     * @param output Le flux de sortie vers le serveur
     * @param stagePourStyle Le stage de la vue précédente
     * @throws IOException si fermement du flux d'entrée ou de sortie impossible
     */
    public VueResultats(String login, ObjectInputStream entreeServeur, ObjectOutputStream output, Stage stagePourStyle) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/vueResultatsModern.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        this.resizableProperty().setValue(Boolean.FALSE);
        VueResultatsController controller = fxmlLoader.getController();
        controller.setInput(entreeServeur);
        controller.setLogin(login);
        controller.setOutput(output);
        controller.init();
        this.setOnCloseRequest(event -> {
            try {
                output.close();
                entreeServeur.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        controller.setStage(this);
        this.setScene(scene);
        this.setTitle("Résultats");
        this.sizeToScene();
        this.getScene().getStylesheets().addAll(stagePourStyle.getScene().getStylesheets());
        this.show();
    }
}