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
import java.util.ArrayList;

/**
 * Classe qui gère la modification du mot de passe d'un utilisateur
 */
public class ChangePwdHandler extends Thread {

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

    private final ArrayList<Referendum> referendumsEnCours;

    /**
     * Constructeur de la classe ChangePwdHandler qui gère la modification du mot de passe d'un utilisateur
     *
     * @param s            Socket du client
     * @param dataOutput   Flux de sortie du client
     * @param objectInput  Flux d'entrée du client
     * @param referendumsEnCours Liste des référendums en cours
     */
    public ChangePwdHandler(Socket s, ObjectOutputStream dataOutput, ObjectInputStream objectInput, ArrayList<Referendum> referendumsEnCours) {
        this.s = s;
        this.dataOutput = dataOutput;
        this.objectInput = objectInput;
        this.referendumsEnCours = referendumsEnCours;
    }

    /**
     * Méthode run, qui est appelée lors de l'exécution du thread : elle gère la modification du mot de passe d'un utilisateur
     */
    @Override
    public void run() {
        try {
            while(true){
                System.out.println("Attente de changement de mot de passe");
                String message = objectInput.readUTF();
                System.out.println(message);
                if(message.equals("backMdpOublie")){
                    new LoginHandler(s, dataOutput, objectInput, referendumsEnCours).start();
                    break;
                } else if (message.equals("changePassword")) {
                    String login = objectInput.readUTF();
                    String code = getCode(login);
                    if (code==null){
                        dataOutput.writeUTF("pasdecode");
                        dataOutput.flush();
                    }
                    else{
                        dataOutput.writeUTF(code);
                        dataOutput.flush();
                    }
                    String etat = objectInput.readUTF();
                    if(etat.equals("changementMdp")){
                        System.out.println("ici?");
                        String newPwd = objectInput.readUTF();
                        changerMdp(login, newPwd);
                        dataOutput.writeUTF("ok");
                        dataOutput.flush();
                        new LoginHandler(s, dataOutput, objectInput, referendumsEnCours).start();
                        break;
                    }
                }
            }

        } catch (IOException | SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }


    private String getCode(String login) throws SQLException, ClassNotFoundException {
        Connection co = ConnexionBD.getInstance().getConnection();
        String requete = "SELECT code FROM code WHERE (utilisateur) = (?)";
        try(PreparedStatement statement = co.prepareStatement(requete)){
            statement.setString(1, login);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                return rs.getString("code");
            }
            else
                return null;
        }
    }


    private void changerMdp(String login, String mdp) throws SQLException, ClassNotFoundException {
        Connection co = ConnexionBD.getInstance().getConnection();
        String requete = "UPDATE utilisateur SET password = ? WHERE login = ?";
        try (PreparedStatement statement = co.prepareStatement(requete)) {
            statement.setString(1, mdp);
            statement.setString(2, login);
            statement.executeUpdate();
        }catch (SQLException  e){
            e.printStackTrace();
        }
    }
}
