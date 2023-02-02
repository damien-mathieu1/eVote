package fr.umontpellier.client;

import fr.umontpellier.View.VueLogin;
import javafx.application.Application;
import javafx.stage.Stage;

public class VoteIHM extends Application {

    @Override
    public void start(Stage stage) {
        new VueLogin();
    }


    public static void main(String[] args) {
        launch();
    }
}
