package fr.umontpellier.Controller;


import fr.umontpellier.Model.VoteTab;
import fr.umontpellier.View.VueVote;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

/**
 * Controller de la vue de choix du referendum à voter.
 */
public class VueChoixVoteController {

    /**
     * stage de la vue.
     */
    private Stage stage;

    /**
     * Login de l'utilisateur.
     */
    private String login;

    /**
     * Le flux d'entrée du serveur.
     */
    private ObjectInputStream input;

    /**
     * Le flux de sortie vers le serveur.
     */
    private ObjectOutputStream output;

    /**
     * Tableau des référendums.
     */
    private ArrayList<String> listeNomVote = new ArrayList<>();

    /**
     * Thread permettant l'affichage du temps restant sur chaque référendum.
     */
    private Thread t;

    /**
     * Tableau des référendums et de leur temps restant.
     */
    @FXML
    TableView<VoteTab> voteEtTempsRestant = new TableView<>();

    /**
     * Colonne du tableau contenant le nom du référendum.
     */
    @FXML
    TableColumn<VoteTab, String> nomVote = new TableColumn<>();

    /**
     * Colonne du tableau contenant le temps restant du référendum.
     */
    @FXML
    TableColumn<VoteTab, String> tempsRestant = new TableColumn<>();


    /**
     * setter du stage de la vue.
     *
     * @param stage le stage
     */
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    /**
     * setter du login de l'utilisateur.
     *
     * @param login le login
     */
    public void setLogin(String login){
        this.login = login;
    }

    /**
     * setter du flux d'entrée du serveur.
     *
     * @param input flux d'entrée
     */
    public void setInput(ObjectInputStream input) {
        this.input = input;
    }

    /**
     * setter du flux de sortie vers le serveur.
     *
     * @param output flux de sortie
     */
    public void setOutput(ObjectOutputStream output) {
        this.output = output;
    }

    /**
     * Fonction permettant de récupérer la liste des référendums en cours, et de les afficher dans le tableau.
     * elle crée un thread qui permet d'afficher le temps restant sur chaque référendum.
     * exécutée lors de l'initialisation de la vue.
     */
    public void init(){
        ArrayList<String> listeVote;
        ArrayList<VoteTab> voteTabs = new ArrayList<>();
        ArrayList<String> listeNomsVote = new ArrayList<>();

        try{
            listeVote = (ArrayList<String>) input.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        for (String s : listeVote) {
            String[] split = s.split(";");
            voteTabs.add(new VoteTab(split[0], split[1]));
            listeNomsVote.add(split[0]);
        }

        this.listeNomVote = listeNomsVote;
        //disable the possibility to edit the table
        voteEtTempsRestant.setEditable(false);
        //disable the possibility to select multiple rows
        voteEtTempsRestant.getSelectionModel().setCellSelectionEnabled(false);
        voteEtTempsRestant.getSelectionModel().setSelectionMode(javafx.scene.control.SelectionMode.SINGLE);
        //disable the possibility to reorder the columns
        voteEtTempsRestant.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        this.nomVote.setCellValueFactory(new PropertyValueFactory<>("nomVote"));
        this.tempsRestant.setCellValueFactory(new PropertyValueFactory<>("tempsRestant"));
        this.voteEtTempsRestant.setItems(FXCollections.observableArrayList(voteTabs));

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    for (VoteTab v : voteEtTempsRestant.getItems()){
                        v.updateTempsRestant();
                    }
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        return;
                    }

                    if (!stage.isShowing()){
                        return;
                    }
                }
            }
        });

        this.t = t;
        t.start();

    }

    /**
     * Fonction permettant de lancer le vote sur le référendum sélectionné. (lance la vue de vote)
     * Exécutée lors du clic sur le bouton "Valider".
     *
     * @throws IOException            erreur d'entrée/sortie lors de la communication avec le serveur
     * @throws ClassNotFoundException erreur de classe lors de la communication avec le serveur
     */
    public void valider() throws IOException, ClassNotFoundException {
        String vote;
        try {
             vote = voteEtTempsRestant.getSelectionModel().getSelectedItem().toString();
        } catch (NullPointerException e) {
            return;
        }

        int idVote = listeNomVote.indexOf(vote);

        try {
            output.writeInt(idVote);
            output.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        new VueVote(login, input, output, stage);
        t.interrupt();
        stage.close();
    }
}
