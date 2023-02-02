package fr.umontpellier.controller;

import fr.umontpellier.dependencies.ConnexionBD;
import fr.umontpellier.dependencies.Email;
import fr.umontpellier.dependencies.Vote;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;

import javax.mail.MessagingException;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

public class voteController {

    @FXML
    private TextField choixDeuxField;

    @FXML
    private TextField choixUnField;

    @FXML
    private AnchorPane configurationVote;

    @FXML
    private DatePicker dateFin;

    @FXML
    private TextField hourField;

    @FXML
    private TextField questionLabel;

    @FXML
    private Button startButton;

    @FXML
    private ToggleGroup date;

    @FXML
    private ToggleGroup hour;

    @FXML
    private ToggleButton customDateBtn;

    @FXML
    private ToggleButton customHourBtn;


    @FXML
    private ToggleButton demainBtn;

    @FXML
    private ToggleButton vingtHeureBtn;

    @FXML
    private ToggleButton septJoursBtn;

    @FXML
    private ToggleButton douzeHeureBtn;

    @FXML
    private TextField nomVote;

    @FXML
    private CheckBox notifyusers;

    @FXML
    void afficherChampDate(ActionEvent event) {
        dateFin.setDisable(false);
    }

    @FXML
    void afficherChampHeure(ActionEvent event) {
        hourField.setDisable(false);
        hourField.clear();
    }

    @FXML
    void reduireChampDate(ActionEvent event) {
        dateFin.setDisable(true);
        if (demainBtn.isSelected()) {
            dateFin.setValue(LocalDate.now().plusDays(1));
        } else if (septJoursBtn.isSelected()) {
            dateFin.setValue(LocalDate.now().plusDays(7));
        }

    }

    @FXML
    void reduireChampHeure(ActionEvent event) {
        hourField.setDisable(true);
        if (vingtHeureBtn.isSelected()) {
            hourField.setText("20:00:00");
        } else if (douzeHeureBtn.isSelected()) {
            hourField.setText("12:00:00");
        }

    }


    @FXML
    void startVote(ActionEvent event) {
        if (questionLabel.getText().isEmpty() || choixUnField.getText().isEmpty() || choixDeuxField.getText().isEmpty() || dateFin.getValue() == null || hourField.getText().isEmpty() || nomVote.getText().isEmpty()) {
            //gestion de l'erreur: champ vide
            msgError("Champ vide", "Veuillez remplir tous les champs");
        } else {

            //récupération des données
            LocalDate d = dateFin.getValue();
            String str = hourField.getText();
            List<String> hms = Arrays.asList(str.split(":"));
            LocalDateTime dateFin = d.atTime(Integer.parseInt(hms.get(0)), Integer.parseInt(hms.get(1)), Integer.parseInt(hms.get(2)));
            Vote vote = new Vote(nomVote.getText(), questionLabel.getText(), choixUnField.getText(), choixDeuxField.getText(), dateFin);
            try {

                try {
                    //remise des votes à false (permet aux utilisateurs de voter une seule fois)
                    Connection co = ConnexionBD.getInstance().getConnection();
                    String sql = "UPDATE utilisateur SET vote = 0";
                    try (PreparedStatement ps = co.prepareStatement(sql)) {
                        ps.executeUpdate();
                    }

                } catch (SQLException e) {
                    //écriture des erreurs liées à la base de données
                    e.printStackTrace();
                }

                //lancement du serveur dans un thread pour ne pas freeze l'interface
                Thread t = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Socket connectionSysteme = new Socket("localhost", 5056);
                            ObjectOutputStream out = new ObjectOutputStream(connectionSysteme.getOutputStream());

                            out.writeUTF("login");
                            out.flush();
                            out.writeUTF("admin:admin");
                            out.flush();
                            out.writeUTF("serv");
                            out.flush();

                            out.writeUTF("newvote");
                            out.flush();

                            out.writeUTF(vote.getVoteString());
                            out.flush();

                            out.writeObject(vote.getDateDeFin());
                            out.flush();
                            //runServer(vote);
                            //entete du mail et texte

                            if(notifyusers.isSelected()){
                                out.writeUTF("envoiemail");

                            }else {
                                out.writeUTF("noemail");
                            }
                            out.flush();

                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });

                t.start();

                    msgInfo("Vote envoyé","Le vote a été ajouté à la liste des votes disponibles");
                } catch(Exception e){
                    e.printStackTrace();
                }
            }
        }

        private void msgError (String header, String message){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Alerte d'erreur");
            alert.setHeaderText(header);
            alert.setContentText(message);
            alert.showAndWait();
        }

        private void msgInfo (String header, String message){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Information");
            alert.setHeaderText(header);
            alert.setContentText(message);
            alert.showAndWait();
        }

        private void msgConfirmation (String header, String message){
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation");
            alert.setHeaderText(header);
            alert.setContentText(message);
            alert.showAndWait();
        }


    }
