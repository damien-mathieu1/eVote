package fr.umontpellier.server;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Objects;

public class configIHM extends Application {

    public static Stage stage;

    public static void main() {
        launch();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getClassLoader().getResource("fxml/Configuration.fxml")));
        primaryStage.setTitle("Configuration");
        primaryStage.setScene(new Scene(root, 578, 430));
        //prefHeight="455.0" prefWidth="578.0"
        primaryStage.resizableProperty().setValue(Boolean.FALSE);
        primaryStage.show();
    }

}
