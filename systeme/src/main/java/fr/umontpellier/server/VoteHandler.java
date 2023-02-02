package fr.umontpellier.server;

import Cryptage.MessageCrypte;
import fr.umontpellier.dependencies.Referendum;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Classe qui gère le vote d'un utilisateur sur le serveur pour un referendum donné
 */
class VoteHandler extends Thread {

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
     * Référendum sur lequel l'utilisateur vote
     */
    private Referendum referendum;

    /**
     * Liste des référendums en cours
     */
    private ArrayList<Referendum> referendumsEnCours;

    /**
     * Constructeur de la classe VoteHandler qui gère le vote d'un utilisateur sur le serveur pour un referendum donné
     *
     * @param s                  Socket du client
     * @param dataOutput         Flux de sortie du client
     * @param objectInput        Flux d'entrée du client
     * @param referendum         Référendum sur lequel l'utilisateur vote
     * @param referendumsEnCours Liste des référendums en cours
     */
    public VoteHandler(Socket s, ObjectOutputStream dataOutput, ObjectInputStream objectInput, Referendum referendum, ArrayList<Referendum> referendumsEnCours) {
        this.s = s;
        this.dataOutput = dataOutput;
        this.objectInput = objectInput;
        this.referendum = referendum;
        this.referendumsEnCours = referendumsEnCours;
    }

    @Override
    public void run() {
        try {
            methodeVote(dataOutput, objectInput);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        new ClientHandler(s, dataOutput, objectInput, referendumsEnCours).start();
    }


    private void methodeVote(ObjectOutputStream dataOutput, ObjectInputStream objectInput) throws IOException, ClassNotFoundException {
        if (!referendum.getDateDeFin().isAfter(LocalDateTime.now())) {
            dataOutput.writeUTF("resultats");
            dataOutput.flush();
            if (referendum.getResultat().equals("")) {
                referendum.calculerResultats();
            }
            dataOutput.writeUTF(referendum.getResultat());
            dataOutput.flush();
        } else {
            dataOutput.writeUTF("vote");
            dataOutput.flush();

            dataOutput.writeUTF(referendum.toString());
            dataOutput.flush();

            dataOutput.writeObject(referendum.getPublicKey());
            dataOutput.flush();

            HashMap<String, MessageCrypte> voteRecu;

            voteRecu = (HashMap<String, MessageCrypte>) objectInput.readObject();

            referendum.addVote(voteRecu);
            System.out.println("Vote reçu de: " + s);
        }


    }


}
