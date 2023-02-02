package fr.umontpellier.controller;

import fr.umontpellier.dependencies.ConnexionBD;
import fr.umontpellier.dependencies.Email;
import fr.umontpellier.dependencies.Hachage;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import org.apache.commons.lang3.RandomStringUtils;

import javax.mail.MessagingException;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Optional;

public class utilisateurController {

    @FXML
    private Button addButton;

    @FXML
    private AnchorPane ajoutUtilisateur;

    @FXML
    private Button editButton;

    @FXML
    private TextField loginText;

    @FXML
    private TextField mailText;

    @FXML
    private TextField passwordText;

    @FXML
    private Button supprButton;

    @FXML
    private Button delete;

    @FXML
    private ListView<String> listUsers;

    @FXML
    private Button refresh;

    @FXML
    private TextField search;

    @FXML
    public void delete(ActionEvent actionEvent) throws SQLException, ClassNotFoundException {
        //TODO: delete user quand il a un code dans modifier mdp
        String loginToDelete = listUsers.getSelectionModel().getSelectedItem();

        boolean isConfirmed = messageConfirmation("Suppression","Suppression d'un utilisateur", "Voulez-vous vraiment supprimer l'utilisateur " + loginToDelete + " ?");
        if(isConfirmed) {
            try {
                Connection co = ConnexionBD.getInstance().getConnection();
                String requeteSql = "DELETE FROM utilisateur WHERE (login) = (?)";
                try (PreparedStatement ps = co.prepareStatement(requeteSql)) {
                    ps.setString(1, loginToDelete);
                    ps.executeUpdate();
                }
                msgInfo("Suppression", "L'utilisateur " + loginToDelete + " a bien été supprimé");
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        afficherUsers();
    }

    @FXML
    public void ajouterUtilisateur(ActionEvent event) throws IOException, SQLException, MessagingException, NoSuchAlgorithmException, ClassNotFoundException {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!?-_";

        String login = loginText.getText();
        String vote = "0";
        String mail = mailText.getText();
        String password = RandomStringUtils.random(15, characters);
        String hashpwd = Hachage.hachage(password);

        Connection co = ConnexionBD.getInstance().getConnection();
        String requeteSql = "INSERT INTO utilisateur(login,password,vote,mail) VALUES((?),(?),(?),(?))";
        try (PreparedStatement ps = co.prepareStatement(requeteSql)) {
            ps.setString(1, login);
            ps.setString(2, hashpwd);
            ps.setString(3, vote);
            ps.setString(4, mail);
            ps.executeUpdate();
        }
        clearChamps();
        /*Envoi du mdp par mail à l'utilisateur*/
        Email.envoiMailPasswd("MDP créé automatiquement", "Bonjour, votre nouveau mot de passe pour le système d'urne e-vote est : \nlogin -> " + login + " \nmdp -> " + password, mail);
        msgInfo("Ajout", "L'utilisateur a bien été ajouté");
    }

    private ArrayList<String> getUsers() throws SQLException, ClassNotFoundException {
        //récupération des utilisateurs
        Connection co = ConnexionBD.getInstance().getConnection();
        try (PreparedStatement ps = co.prepareStatement("SELECT login FROM utilisateur")) {
            ResultSet rs = ps.executeQuery();
            ArrayList<String> users = new ArrayList<>();
            while (rs.next()) {
                users.add(rs.getString("login"));
            }
            return users;
        }
    }

    @FXML
    private void refresh(ActionEvent event) throws SQLException, ClassNotFoundException {
        afficherUsers();
    }

    @FXML
    public void search(KeyEvent actionEvent) throws SQLException, ClassNotFoundException {
        String searchStr = this.search.getText();
        //get users from this.listUsers
        ArrayList<String> users = getUsers();
        ArrayList<String> usersSearch = new ArrayList<>();
        for (String user : users) {
            if (user.contains(searchStr)) {
                usersSearch.add(user);
            }
        }
        listUsers.setItems(FXCollections.observableArrayList(usersSearch));
    }

    private void afficherUsers() throws SQLException, ClassNotFoundException {
        //affichage des utilisateurs
        ArrayList<String> users = getUsers();
        listUsers.setItems(FXCollections.observableArrayList(users));
    }

    @FXML
    public void modifierUtilisateur(ActionEvent event) throws SQLException, ClassNotFoundException {
        //TODO: modifier user en requete préparée
       /* String login = loginText.getText();
        String password = passwordText.getText();
        String vote = aVote.getText();

        Connection co = ConnexionBD.getInstance().getConnection();
        String requeteSql = "UPDATE utilisateur SET login = (?), (password) = (?), (vote) = (?) WHERE (login) = (?)";
        ConnexionBD connexionBD = new ConnexionBD();
        Statement statement = connexionBD.ConnexionBD().createStatement();

        String requeteSql = "UPDATE utilisateur SET login = \"" + login + "\", password = \"" + password + "\", vote = \"" + vote + "\" WHERE login = \"" + login + "\";";
        statement.execute(requeteSql);
        statement.close();
        clearChamps();*/
        msgInfo("Modification", "L'utilisateur a bien été modifié");
    }

    @FXML
    public void supprimerUtilisateur(ActionEvent event) throws SQLException, ClassNotFoundException {

        String login = loginText.getText();


        Connection co = ConnexionBD.getInstance().getConnection();
        String requeteSql = "DELETE FROM utilisateur WHERE (login) = (?)";
        try (PreparedStatement ps = co.prepareStatement(requeteSql)) {
            ps.setString(1, login);
            ps.executeUpdate();
        }
        clearChamps();

        msgInfo("Suppression", "L'utilisateur a bien été supprimé");
    }

    private void msgInfo(String header, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(header);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private boolean messageConfirmation(String title, String header, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(message);
        Optional<ButtonType> result = alert.showAndWait();
        return result.get() == ButtonType.OK;
    }

    public void clearChamps() {
        loginText.clear();
        mailText.clear();
    }

}

