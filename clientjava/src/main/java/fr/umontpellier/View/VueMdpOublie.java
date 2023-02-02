package fr.umontpellier.View;

import fr.umontpellier.Controller.VueMdpOublieController;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Vue de mot de passe oublié, qui permet d'envoyer un code de vérification par mail afin de pouvoir changer son mot de passe.
 */
public class VueMdpOublie extends Stage {

    /**
     * Constructeur de la vue. Charge le fichier fxml et initialise le controller.
     *
     * @param stagePourStyle le stage de l'ancienne vue pour récupérer le style.
     * @param input          le flux d'entrée du serveur.
     * @param output         le flux de sortie vers le serveur.
     * @throws IOException si le fichier fxml n'est pas trouvé.
     */
    public VueMdpOublie(Stage stagePourStyle, ObjectInputStream input, ObjectOutputStream output) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/vueMotDePasseOublieModern.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        VueMdpOublieController controller = fxmlLoader.getController();
        controller.setStage(this);
        controller.setCoServ(input, output);
        this.resizableProperty().setValue(Boolean.FALSE);
        this.setScene(scene);
        this.setTitle("Mot de passe oublié");
        this.sizeToScene();
        this.getScene().getStylesheets().addAll(stagePourStyle.getScene().getStylesheets());
        this.show();
    }
}