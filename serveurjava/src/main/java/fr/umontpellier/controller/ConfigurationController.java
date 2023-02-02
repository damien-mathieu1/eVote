package fr.umontpellier.controller;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class ConfigurationController {

    @FXML
    private TextField mailText;
    @FXML
    private TextField aVote;

    @FXML
    private TextField passwordText;
    @FXML
    private Button addButton;

    @FXML
    private TextField choixDeuxField;

    @FXML
    private TextField choixUnField;

    @FXML
    private DatePicker dateFin;

    @FXML
    private Button editButton;

    @FXML
    private TextField hourField;

    @FXML
    private TextField loginText;

    @FXML
    private TextField questionLabel;

    @FXML
    private Button startButton;

    @FXML
    private Button supprButton;

    //vueModifierUtilisateurs
    @FXML
    private TextField search;

    @FXML
    private ListView<String> listUsers;

    @FXML
    private Button delete;

    @FXML
    private Tab userTab;

}




