package fr.umontpellier.View;

import fr.umontpellier.Controller.VueLoginController;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

/**
 * Vue de la page de login.
 */
public class VueLogin extends Stage {

    /**
     * Constructeur de la vue de la page de login. Utilisé quand l'application est lancée.
     *
     */
    public VueLogin() {
        try {
            FXMLLoader loaderVueLogin = new FXMLLoader(getClass().getResource("/fxml/vueLoginModern.fxml"));
            Scene scene = new Scene(loaderVueLogin.load());
            this.resizableProperty().setValue(Boolean.FALSE);
            VueLoginController controller = loaderVueLogin.getController();
            controller.setEssaiInit();
            controller.setStage(this);
            controller.init();
            this.setScene(scene);
            this.setTitle("Connexion");
            this.sizeToScene();
            this.getScene().getStylesheets().add(Objects.requireNonNull(getClass().getResource("/css/style.css")).toExternalForm());
            this.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Constructeur de la vue de la page de login. Utilisé lorsque l'application est déjà lancée, et que l'utilisateur retourne à la page de login.
     * @param stagePourTheme pour reprendre le thème de la fenêtre passée en paramètre.
     */
    public VueLogin(Stage stagePourTheme){
        try {
            FXMLLoader loaderVueLogin = new FXMLLoader(getClass().getResource("/fxml/vueLoginModern.fxml"));
            Scene scene = new Scene(loaderVueLogin.load());
            this.resizableProperty().setValue(Boolean.FALSE);
            VueLoginController controller = loaderVueLogin.getController();
            controller.setEssaiInit();
            controller.setStage(this);
            controller.init();
            this.setScene(scene);
            this.setTitle("Connexion");
            this.sizeToScene();
            this.getScene().getStylesheets().addAll(stagePourTheme.getScene().getStylesheets());
            this.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
