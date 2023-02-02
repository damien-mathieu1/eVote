package fr.umontpellier.controller;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

public class voteEnCoursController {

    @FXML
    private ListView<String> listVotes;

    @FXML
    private Button delete;


    @FXML
    public void delete(ActionEvent actionEvent) throws IOException {
        String vote = listVotes.getSelectionModel().getSelectedItem();
        if (vote != null) {
            boolean isConfirmed = messageConfirmation("Suppression","Suppression d'un vote", "Voulez-vous vraiment supprimer le vote : " + vote + " ?");

            if(isConfirmed) {
                Socket connectionSysteme = new Socket("localhost", 5056);
                ObjectOutputStream out = new ObjectOutputStream(connectionSysteme.getOutputStream());
                ObjectInputStream in = new ObjectInputStream(connectionSysteme.getInputStream());
                out.writeUTF("login");
                out.flush();
                out.writeUTF("admin:admin");
                out.flush();
                out.writeUTF("serv");
                out.flush();
                out.writeUTF("deletevote");
                out.flush();
                in.readUTF();

                out.writeUTF(vote);
                out.flush();
                connectionSysteme.close();
                this.refresh();
            }
        }
    }

    public void refresh() throws IOException {
        Socket connectionSysteme = new Socket("localhost", 5056);
        ObjectOutputStream out = new ObjectOutputStream(connectionSysteme.getOutputStream());
        ObjectInputStream in = new ObjectInputStream(connectionSysteme.getInputStream());

        out.writeUTF("login");
        out.flush();
        out.writeUTF("admin:admin");
        out.flush();
        out.writeUTF("serv");
        out.flush();

        out.writeUTF("listVotes");
        out.flush();

        String NomVotes;

        in.readUTF();


        NomVotes = in.readUTF();

        String[] votes = NomVotes.split(":");
        ArrayList<String> listNomVotes = new ArrayList<>(Arrays.asList(votes));

        listVotes.setItems(FXCollections.observableArrayList(listNomVotes));
        connectionSysteme.close();
    }

    private boolean messageConfirmation(String title, String header, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(message);
        Optional<ButtonType> result = alert.showAndWait();
        return result.get() == ButtonType.OK;
    }
}

