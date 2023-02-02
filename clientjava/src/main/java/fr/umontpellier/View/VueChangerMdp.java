package fr.umontpellier.View;

import fr.umontpellier.Controller.vueChangerMdpController;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;


/**
 * Vue de changement de mot de passe.
 */
public class VueChangerMdp extends Stage {

    /**
     * Login de l'utilisateur.
     */
    public String login;

    /**
     * Constructeur de la vue. Charge le fichier fxml et initialise le controller.
     *
     * @param login          le login de l'utilisateur.
     * @param stagePourStyle le stage de l'ancienne vue pour récupérer le style.
     * @param input          le flux d'entrée du serveur.
     * @param output         le flux de sortie vers le serveur.
     * @throws IOException si le serveur n'est pas trouvé.
     */
    public VueChangerMdp(String login, Stage stagePourStyle, ObjectInputStream input, ObjectOutputStream output) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/vueChangerMotDePasseModern.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        this.resizableProperty().setValue(Boolean.FALSE);
        vueChangerMdpController controller = fxmlLoader.getController();
        controller.setLogin(login);
        controller.setStage(this);
        controller.setCoServ(input, output);
        this.setScene(scene);
        this.setTitle("Changer votre mot de passe");
        this.sizeToScene();
        this.getScene().getStylesheets().addAll(stagePourStyle.getScene().getStylesheets());
        this.show();
    }
}

