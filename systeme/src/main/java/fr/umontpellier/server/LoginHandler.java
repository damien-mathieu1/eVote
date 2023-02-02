package fr.umontpellier.server;

import fr.umontpellier.dependencies.ConnexionBD;
import fr.umontpellier.dependencies.Referendum;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Classe qui gère le login d'un utilisateur sur le serveur
 */
public class LoginHandler extends Thread {

    /**
     * Socket du client
     */
    final Socket s;

    /**
     * Flux de sortie
     */
    final ObjectOutputStream dataOutput;

    /**
     * Flux d'entrée
     */
    final ObjectInputStream objectInput;

    /**
     * Liste des référendums en cours
     */
    private ArrayList<Referendum> referendumsEnCours;

    /**
     * Constructeur de la classe LoginHandler qui gère le login d'un utilisateur sur le serveur
     *
     * @param s            Socket du client
     * @param dataOutput   Flux de sortie du client
     * @param objectInput  Flux d'entrée du client
     * @param referendumsEnCours Liste des référendums en cours
     */
    public LoginHandler(Socket s, ObjectOutputStream dataOutput, ObjectInputStream objectInput, ArrayList<Referendum> referendumsEnCours) {
        this.s = s;
        this.dataOutput = dataOutput;
        this.objectInput = objectInput;
        this.referendumsEnCours = referendumsEnCours;
    }

    @Override
    public void run() {

        try {
            label:
            while (true) {
                String message = objectInput.readUTF();

                //si le client veut se connecter
                switch (message) {
                    case "login":
                        message = objectInput.readUTF();
                        String[] loginpassword = message.split(":");

                        //vériier si le login et le mot de passe sont corrects
                        if (verifieLogin(loginpassword[0])) {
                            if (resteEssais(loginpassword[0])) {
                                if (verifiePassword(loginpassword[0], loginpassword[1])) {
                                    dataOutput.writeUTF("connexionauthorise");
                                    dataOutput.flush();
                                    new ClientHandler(s, dataOutput, objectInput, referendumsEnCours).start();
                                    break label;
                                } else {
                                    falsePassword(loginpassword[0]);
                                    dataOutput.writeUTF("badpassword");
                                    System.out.println("Mauvais mot de passe");
                                    dataOutput.flush();
                                    dataOutput.writeUTF(""+LoginHandler.getNbEssais(loginpassword[0])+"");
                                    dataOutput.flush();
                                }
                            } else {
                                int essais = LoginHandler.getNbEssais(loginpassword[0]);
                                dataOutput.writeUTF("bloque");
                                dataOutput.flush();
                            }
                        } else {
                            dataOutput.writeUTF("badlogin");
                            dataOutput.flush();
                        }
                        break;
                    //si le client veut changer son mot de passe
                    case "mdpoublie":
                        new ForgottenPwdHandler(s, dataOutput, objectInput, referendumsEnCours).start();
                        break label;
                    case "changermdp":
                        new ChangePwdHandler(s, dataOutput, objectInput, referendumsEnCours).start();
                        break label;
                }

            }
        } catch (SQLException | ClassNotFoundException ex) {
            throw new RuntimeException(ex);
        } catch (IOException ex) {
            System.err.println("Le client s'est déconnecté");
        }
    }

    private boolean verifieLogin(String login) throws SQLException, ClassNotFoundException {
        Connection co = ConnexionBD.getInstance().getConnection();
        ResultSet resultSet;
        int res;
        String requeteSql = "select count(*) from utilisateur where (login) = (?)";

        //check si le login existe
        try (PreparedStatement statement = co.prepareStatement(requeteSql)) {
            statement.setString(1, login);
            resultSet = statement.executeQuery();
            resultSet.next();
            res = resultSet.getInt(1);
        }
        return res == 1;
    }


    private boolean verifiePassword(String loginEnter, String passwordEnter) throws SQLException, ClassNotFoundException {
        Connection co = ConnexionBD.getInstance().getConnection();
        ResultSet resultSet;
        String password;
        String requeteSql = "select password from utilisateur where (login) = (?)";

        try (PreparedStatement statement = co.prepareStatement(requeteSql)) {
            statement.setString(1, loginEnter);
            resultSet = statement.executeQuery();
            resultSet.next();
            password = resultSet.getString("password");
        }
        return password.equals(passwordEnter);
    }

    private boolean resteEssais(String login) throws SQLException, ClassNotFoundException {
        Connection co = ConnexionBD.getInstance().getConnection();
        ResultSet resultSet;
        String requeteSql = "select essaiRestant from utilisateur where (login) = (?)";

        try (PreparedStatement statement = co.prepareStatement(requeteSql)) {
            statement.setString(1, login);
            resultSet = statement.executeQuery();
            resultSet.next();
            return resultSet.getInt("essaiRestant") > 0;
        }
        //TODO: SI IL RESTE DES ESSAIS, ON RENVOIE TRUE, sinon false
    }

    private int falsePassword(String login) throws SQLException, ClassNotFoundException {
        Connection co = ConnexionBD.getInstance().getConnection();
        ResultSet resultSet;
        int essaiRestant = LoginHandler.getNbEssais(login);
        if (essaiRestant > 0) {
            essaiRestant--;
            String requeteSql = "update utilisateur set essaiRestant = (?) where (login) = (?)";
            try (PreparedStatement statement = co.prepareStatement(requeteSql)) {
                statement.setInt(1, essaiRestant);
                statement.setString(2, login);
                statement.executeUpdate();
            }
            if (essaiRestant == 0) {
                String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new java.util.Date());
                requeteSql = "update utilisateur set essaiRestant = (?), currentTimeStamp= (?) where (login) = (?)";
                try (PreparedStatement statement = co.prepareStatement(requeteSql)) {
                    statement.setInt(1, essaiRestant);
                    statement.setString(2, timeStamp);
                    statement.setString(3, login);
                    statement.executeUpdate();
                }
            }
            return essaiRestant;
        }
        return 0;

        //TODO: si nbEssais = 0, alors on bloque le compte pour x minutes
        //TODO si nbEssais > 0, alors on décrémente le nombre d'essais
    }

    private static int getNbEssais(String login) throws SQLException, ClassNotFoundException {
        Connection co = ConnexionBD.getInstance().getConnection();
        ResultSet resultSet;
        String requeteSql = "select essaiRestant from utilisateur where (login) = (?)";

        try (PreparedStatement statement = co.prepareStatement(requeteSql)) {
            statement.setString(1, login);
            resultSet = statement.executeQuery();
            resultSet.next();
            return resultSet.getInt("essaiRestant");
        }
    }
}




