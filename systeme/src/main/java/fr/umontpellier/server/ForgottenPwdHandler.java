package fr.umontpellier.server;

import fr.umontpellier.dependencies.ConnexionBD;
import fr.umontpellier.dependencies.Email;
import fr.umontpellier.dependencies.Hachage;
import fr.umontpellier.dependencies.Referendum;
import org.apache.commons.lang3.RandomStringUtils;

import javax.mail.MessagingException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Classe qui gère la modification du mot de passe d'un utilisateur si celui-ci l'a oublié
 */
public class ForgottenPwdHandler extends Thread {

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
     * Constructeur de la classe ForgottenPwdHandler qui gère la modification du mot de passe d'un utilisateur si celui-ci l'a oublié
     *
     * @param s            Socket du client
     * @param dataOutput   Flux de sortie du client
     * @param objectInput  Flux d'entrée du client
     * @param referendumsEnCours Liste des référendums en cours
     */
    public ForgottenPwdHandler(Socket s, ObjectOutputStream dataOutput, ObjectInputStream objectInput, ArrayList<Referendum> referendumsEnCours) {
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
                System.out.println("En attente d'un message");
                String message = objectInput.readUTF();
                System.out.println(message);
                switch (message) {
                    case "backLogin" -> {
                        new LoginHandler(s, dataOutput, objectInput, referendumsEnCours).start();
                        return;
                    }
                    case "envoiCode" -> {
                        String login = objectInput.readUTF();
                        if (checkLoginBd(login) == 1) {
                            envoieCode(login);
                            dataOutput.writeUTF("codeEnvoye");
                            dataOutput.flush();
                        } else {
                            dataOutput.writeUTF("loginInconnu");
                            dataOutput.flush();
                        }
                    }
                    case "rentrerCode" -> {
                        String login = objectInput.readUTF();
                        if (checkLoginBd(login) == 1) {
                            dataOutput.writeUTF("loginOk");
                            dataOutput.flush();
                            new LoginHandler(s, dataOutput, objectInput, referendumsEnCours).start();
                            break label;
                        } else {
                            dataOutput.writeUTF("loginInconnu");
                            dataOutput.flush();
                        }
                    }
                }
            }
        }catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException | MessagingException | NoSuchAlgorithmException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private void envoieCode(String login) throws SQLException, MessagingException, IOException, NoSuchAlgorithmException, ClassNotFoundException {
        String code = RandomStringUtils.randomAlphanumeric(6);
        Connection co = ConnexionBD.getInstance().getConnection();
        ResultSet rs;
        String mail = null;
        boolean mailTrouve;
        String requeteSql = "select mail from utilisateur where (login) = (?)";
        try(PreparedStatement statement = co.prepareStatement(requeteSql)){
            statement.setString(1, login);
            rs = statement.executeQuery();
            mailTrouve = rs.next();
            if(mailTrouve){
                mail = rs.getString("mail");
            }
            else{
                System.out.println("Erreur");
            }
        }

        if(mailTrouve){
            requeteSql = "delete from code where (utilisateur) = (?)";
            try(PreparedStatement statement = co.prepareStatement(requeteSql)){
                statement.setString(1, login);
                statement.executeUpdate();
            }
            String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new java.util.Date());
            requeteSql = "insert into code(code,utilisateur,currentTimeStamp) values ((?),(?),(?))";
            try(PreparedStatement statement = co.prepareStatement(requeteSql)){
                statement.setString(1, Hachage.hachage(code));
                statement.setString(2, login);
                statement.setString(3, timeStamp);
                statement.executeUpdate();
            }
            Email.envoiMailPasswd("Code de réinitialisation eVote", "Bonjour, votre code pour réinitialiser votre mot de passe est : " + code+" (Ce code expire à "+this.heureMinute()+")", mail);
        }
        else{
            System.out.println("Erreur");
        }
    }

    private int checkLoginBd(String login) throws SQLException, ClassNotFoundException {
        Connection co = ConnexionBD.getInstance().getConnection();
        ResultSet rs;
        int res;
        String requeteSql = "select count(*) from utilisateur where (login) = (?)";
        try(PreparedStatement statement = co.prepareStatement(requeteSql)){
            statement.setString(1, login);
            rs = statement.executeQuery();
            rs.next();
            res= rs.getInt(1);
        }
        return res;
    }

    public String heureMinute(){
        String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new java.util.Date());
        String heure = timeStamp.substring(11, 13);
        String minute = timeStamp.substring(14, 16);
        int minuteInt = Integer.parseInt(minute);
        if (minuteInt+15 >= 60) {
            int heureInt = Integer.parseInt(heure);
            heureInt++;
            heure = Integer.toString(heureInt);
            minuteInt = minuteInt+15-60;
            if (minuteInt < 10) {
                minute = "0" + Integer.toString(minuteInt);
            } else {
                minute = Integer.toString(minuteInt);
            }
        } else {
            minuteInt = minuteInt+15;
            minute = Integer.toString(minuteInt);
        }
        return heure+":"+minute;
    }
}
