package fr.umontpellier.server;

import fr.umontpellier.dependencies.ConnexionScrutateur;
import fr.umontpellier.dependencies.Email;
import fr.umontpellier.dependencies.Referendum;

import javax.mail.MessagingException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;


/**
 * Classe qui gère les connexions des clients, admin ou non, et qui les redirige vers le bon handler en fonction de leur choix
 */
class ClientHandler extends Thread {

    /**
     * Socket du client
     */
    final Socket s;

    /**
     * Flux de sortie du client
     */
    final ObjectOutputStream objectOutput;

    /**
     * Flux d'entrée du client
     */
    final ObjectInputStream objectInput;

    private ArrayList<Referendum> referendumsEnCours;

    /**
     * Constructeur de la classe ClientHandler qui gère les connexions des clients, admin ou non, et qui les redirige vers le bon handler en fonction de leur choix
     *
     * @param s                  Socket du client
     * @param dataOutput         Flux de sortie du client
     * @param objectInput        Flux d'entrée du client
     * @param referendumsEnCours Liste des référendums en cours
     */
    public ClientHandler(Socket s, ObjectOutputStream dataOutput, ObjectInputStream objectInput, ArrayList<Referendum> referendumsEnCours) {
        this.s = s;
        this.objectOutput = dataOutput;
        this.objectInput = objectInput;
        this.referendumsEnCours = referendumsEnCours;
    }

    @Override
    public void run() {

        boolean estAdmin = false;

        //Test de connexion, on vérifie si l'utilisateur est un admin ou non
        try {
            String message = objectInput.readUTF();
            if (message.equals("serv")) {
                estAdmin = true;
            }

        } catch (IOException e) {
            System.err.println("Erreur lors de la lecture du message de :"+s.getInetAddress());
        }

        //Si l'utilisateur est un admin
        if (estAdmin) {
            String line;
            try {
                line = objectInput.readUTF();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }


            switch (line) {
                case "newvote" -> {
                    String vote;
                    try {
                        vote = objectInput.readUTF();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    String[] voteSplit = vote.split(":");
                    LocalDateTime dateFinVoteRecu;
                    try {
                        dateFinVoteRecu = (LocalDateTime) objectInput.readObject();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    Referendum voterecu = new Referendum(voteSplit[0], voteSplit[1], voteSplit[2], voteSplit[3], dateFinVoteRecu);
                    this.addToReferendumsEnCours(voterecu);
                    String message;
                    try {
                        message = objectInput.readUTF();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    if (message.equals("envoiemail")) {
                        System.out.println("envoiemail");
                        Thread envoieMail = new Thread(() -> {
                            String sujet = "NOTIFICATION DE VOTE";
                            String texte = "Bonjour, un nouveau vote est disponible dans votre application de vote.";
                            //envoi mail à tous les mails de la bd
                            try {
                                Email.envoiMailGroupe(sujet, texte);
                            } catch (IOException | SQLException | MessagingException | ClassNotFoundException e) {
                                throw new RuntimeException(e);
                            }
                        });
                        envoieMail.start();
                    }
                }
                case "listVotes" -> {
                    StringBuilder nomDesVotes = new StringBuilder();
                    for (Referendum referendum : referendumsEnCours) {
                        nomDesVotes.append(referendum.getNom()).append(":");
                    }
                    if (nomDesVotes.length() > 0) {
                        nomDesVotes.deleteCharAt(nomDesVotes.length() - 1);
                    }
                    try {
                        objectOutput.writeUTF(nomDesVotes.toString());
                        objectOutput.flush();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
                case "deletevote" -> {
                    String voteAsupprimer;
                    try {
                        voteAsupprimer = objectInput.readUTF();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    deleteReferendumEnCours(voteAsupprimer);
                }
            }
        }

        //si le client est un client normal
        else {
            try {
                if (referendumsEnCours.size() == 0) {
                    objectOutput.writeUTF("aucunvote");
                    objectOutput.flush();

                    //fermeture des ressources
                    try {
                        this.objectInput.close();
                        this.objectInput.close();
                        this.s.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    //envoi des votes en cours
                    objectOutput.writeUTF("plusieursvotes");
                    objectOutput.flush();

                    ArrayList<String> listeVotes = new ArrayList<>();

                    for (Referendum referendum : referendumsEnCours) {
                        listeVotes.add(referendum.getNom() + ";" + referendum.getDateDeFin().toString());
                    }

                    objectOutput.writeObject(listeVotes);
                    objectOutput.flush();

                    //reception du vote choisi
                    int reponse;
                    try {
                        reponse = objectInput.readInt();
                    } catch (IOException e) {
                        System.err.println("client " + s + " déconnecté");
                        return;
                    }

                    //creation du voteHandler pour le vote choisi
                    VoteHandler voteHandler = new VoteHandler(s, objectOutput, objectInput, referendumsEnCours.get(reponse), referendumsEnCours);
                    voteHandler.start();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private synchronized void addToReferendumsEnCours(Referendum referendum) {
        referendum.setPublicKey(ConnexionScrutateur.getPublicKey(referendum.getNom()));
        referendumsEnCours.add(referendum);
    }

    private synchronized void deleteReferendumEnCours(String nomReferendum) {
        for (Referendum v : referendumsEnCours) {
            if (v.getNom().equals(nomReferendum)) {
                referendumsEnCours.remove(v);
                break;
            }
        }
    }


}