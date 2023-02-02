package fr.umontpellier.server;// Java implementation of fr.umontpellier.server.Server side
// It contains two classes : fr.umontpellier.server.Server and fr.umontpellier.server.ClientHandler
// Save file as fr.umontpellier.server.Server.java

import Cryptage.MessageCrypte;
import Cryptage.PublicKey;
import fr.umontpellier.dependencies.ConnexionBD;
import fr.umontpellier.dependencies.Vote;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Scanner;

// fr.umontpellier.server.Server class
public class Server {

    final static String SCRUTATEUR_IP = "localhost";

    static Vote vote;

    public static void main(String[] args) throws IOException, SQLException, ClassNotFoundException {

        vote = setupVote();
        resetAVote();
        runServer(vote);

    }

    /**
     * Méthode principale du serveur, elle permet de lancer le serveur et de gérer les connexions entrantes pour le déroulement du vote
     *
     * @param vote les informations du vote pour lequel le serveur est lancé
     * @throws IOException
     */
    public static void runServer(Vote vote) throws IOException {

        System.out.println("Serveur démarré");
        // server is listening on port 5056
        ServerSocket serveurUrne = new ServerSocket(5056);

        Cryptage.MessageCrypte messageCrypte = new Cryptage.MessageCrypte(0, getPublicKey());

        HashMap<String, Cryptage.MessageCrypte> votes = new HashMap<>();

        int nombreDeVotes = 0;

        // running infinite loop for getting
        // client request
        Socket client = null;

        while (LocalDateTime.now().isBefore(vote.getDateDeFin())) {

            //TODO: Gérer les exceptions plus proprement
            try {
                // socket object to receive incoming client requests
                client = serveurUrne.accept();

                System.out.println("Nouveau client connecté : " + client);

                if (LocalDateTime.now().isAfter(vote.getDateDeFin())) {
                    break;
                }

                // obtaining input and out streams
                DataOutputStream outputData = new DataOutputStream(client.getOutputStream());
                ObjectInputStream inputObjets = new ObjectInputStream(client.getInputStream());

                // create a new thread object
                ClientHandler t = new ClientHandler(client, outputData, inputObjets, vote);

                // Invoking the start() method
                t.start();
                t.join();

                HashMap<String, MessageCrypte> voteRecu = t.getMessageCrypte();
                votes.putAll(voteRecu);

                //afficher les votes reçus pour test
                for (String key : votes.keySet()) {
                    int testGetVote = getMessageDecrypte(votes.get(key));
                    if (testGetVote == 1) {
                        System.out.println("login : " + key + ":" + vote.getChoixUn());
                    } else if (testGetVote == 0) {
                        System.out.println("login : " + key + ":" + vote.getChoixDeux());
                    }
                }

            } catch (IOException | InterruptedException e) {
                e.printStackTrace();

            } catch (Exception e) {
                assert client != null;
                client.close();
                e.printStackTrace();

            }
        }

        System.out.println("Vote terminé");
        System.out.println("Calcul des résultats...");

        nombreDeVotes = votes.size();

        for (String key : votes.keySet()) {
            messageCrypte = messageCrypte.agregate(votes.get(key), getPublicKey());
        }

        int message = getMessageDecrypte(messageCrypte);

        String resultats = resultatVotePourGUI(message, nombreDeVotes, vote);


        System.out.println(resultats);

        while (true) {

            //Socket client = null;
            try {
                // socket object to receive incoming client requests

                if (client == null) {
                    client = serveurUrne.accept();
                    System.out.println("Nouveau client connecté : " + client);
                }

                // obtaining input and out streams
                DataOutputStream outputData = new DataOutputStream(client.getOutputStream());

                outputData.writeUTF("resultats");
                outputData.flush();

                outputData.writeUTF(resultats);
                client.close();
                client = null;

            } catch (Exception e) {
                assert client != null;
                client.close();
                e.printStackTrace();
            }
        }

    }

    /**
     * Méthode permettant de récupérer la clé publique du scrutateur
     *
     * @return la clé publique du scrutateur
     */
    public static PublicKey getPublicKey() {
        Socket connectionAuScrutateur;

        ObjectInputStream inputScrutateur;
        ObjectOutputStream outputScrutateur;

        PublicKey publicKey = null;

        try {
            connectionAuScrutateur = new Socket(SCRUTATEUR_IP, 5057);

            inputScrutateur = new ObjectInputStream(connectionAuScrutateur.getInputStream());
            outputScrutateur = new ObjectOutputStream(connectionAuScrutateur.getOutputStream());

            outputScrutateur.writeUTF("PK");
            outputScrutateur.flush();

            publicKey = (PublicKey) inputScrutateur.readObject();

            connectionAuScrutateur.close();
            outputScrutateur.close();
            inputScrutateur.close();

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erreur de connexion au scrutateur. Veuillez vérifier que le scrutateur est en ligne.");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }


        return publicKey;
    }

    /**
     * Envoie le message crypté au scrutateur qui lui renvoie le message déchiffré
     *
     * @param messageCrypte le message crypté
     * @return le message déchiffré (en int)
     */
    public static int getMessageDecrypte(MessageCrypte messageCrypte) {
        Socket connectionAuScrutateur;

        ObjectInputStream inputScrutateur;
        ObjectOutputStream outputScrutateur;

        int messageDecrypte = 0;

        try {
            connectionAuScrutateur = new Socket(SCRUTATEUR_IP, 5057);

            inputScrutateur = new ObjectInputStream(connectionAuScrutateur.getInputStream());
            outputScrutateur = new ObjectOutputStream(connectionAuScrutateur.getOutputStream());

            outputScrutateur.writeUTF("decrypt");
            outputScrutateur.flush();

            outputScrutateur.writeObject(messageCrypte);
            outputScrutateur.flush();

            messageDecrypte = (Integer) inputScrutateur.readObject();

            connectionAuScrutateur.close();
            outputScrutateur.close();
            inputScrutateur.close();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }


        return messageDecrypte;
    }

    /**
     * Fonction rendant un String contenant les résultats du vote pour un affichage console
     *
     * @param message       le nombre de votes pour le choix 1
     * @param nombreDeVotes le nombre total de votes
     * @param vote          le vote en cours
     * @return String contenant les résultats du vote
     */
    public static String resultatVote(int message, int nombreDeVotes, Vote vote) {
        StringBuilder resultatVote = new StringBuilder();
        resultatVote.append("Résulats : \n");
        resultatVote.append("Nombre de votes pour ").append(vote.getChoixUn()).append(" : ").append(message).append("\n");
        resultatVote.append("Nombre de votes pour ").append(vote.getChoixDeux()).append(" : ").append(nombreDeVotes - message).append("\n");
        resultatVote.append("Nombre total de votes : ").append(nombreDeVotes).append("\n");
        resultatVote.append("\n");
        resultatVote.append(vote.getChoixUn()).append(" : ").append(message * 100 / nombreDeVotes).append(" %\n");
        resultatVote.append(vote.getChoixDeux()).append(" : ").append(100 - message * 100 / nombreDeVotes).append(" %\n");
        resultatVote.append("\n");
        if (message * 100 / nombreDeVotes > 50) {
            resultatVote.append(vote.getChoixUn()).append(" a gagné");
        } else {
            resultatVote.append(vote.getChoixDeux()).append(" a gagné");
        }

        return resultatVote.toString();
    }

    /**
     * Fonction qui formate le résultat de manière à ce que l'interface utilisateur du client puisse l'utiliser
     *
     * @param message       le nombre de votes pour le choix 1
     * @param nombreDeVotes le nombre total de votes
     * @param vote          le vote en cours
     * @return le résultat, en String, du vote formaté de la forme "question:choix1:choix2:message:nombreDeVotes"
     */
    public static String resultatVotePourGUI(int message, int nombreDeVotes, Vote vote) {
        String resultatVote = vote.getQuestion() + ":" +
                vote.getChoixUn() + ":" +
                vote.getChoixDeux() + ":" +
                message + ":" +
                nombreDeVotes;
        return resultatVote;
    }

    /**
     * Fonction qui permet de paramétrer les caractéristiques du vote lors de la création de l'urne
     *
     * @return
     * @see Vote
     * @deprecated Cette fonction n'est plus utilisée, elle a été remplacée par l'interface utilisateur
     */
    public static Vote setupVote() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("****************************************");
        System.out.println("* Configuration de l'urne pour le vote *");
        System.out.println("****************************************");
        System.out.println("Veuillez entrer le sujet du vote (question) : ");
        String question = scanner.nextLine();
        System.out.println("Veuillez entrer le premier choix:");
        String choix1 = scanner.nextLine();
        System.out.println("Veuillez entrer le deuxième choix:");
        String choix2 = scanner.nextLine();
        System.out.println("Veuillez entrer la date  et l'heure de fin du vote (format : dd-MM-yyyy HH:mm:ss) : ");
        String dateFin = scanner.nextLine();
        while (!dateFin.matches("\\d{2}-\\d{2}-\\d{4} \\d{2}:\\d{2}:\\d{2}")) {
            System.out.println("Veuillez entrer la date de fin du vote (format : dd-MM-yyyy HH:mm:ss) : ");
            dateFin = scanner.nextLine();
        }

        return new Vote("ceci est un vote", question, choix1, choix2, LocalDateTime.parse(dateFin, DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")));
    }

    public static void resetAVote() throws SQLException, ClassNotFoundException {
        System.out.println("TEST CACA ZIZI");
        Connection co = ConnexionBD.getInstance().getConnection();
        String requete = "UPDATE vote SET vote = 0 where vote = 1";
        try (PreparedStatement ps = co.prepareStatement(requete)) {
            ps.executeUpdate();
        }
    }
}

