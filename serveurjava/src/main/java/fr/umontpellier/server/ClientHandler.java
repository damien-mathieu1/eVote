package fr.umontpellier.server;

import Cryptage.MessageCrypte;
import fr.umontpellier.dependencies.Vote;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.HashMap;

// fr.umontpellier.server.ClientHandler class
class ClientHandler extends Thread {


    final DataOutputStream dataOutput;
    final Socket s;
    final ObjectInputStream objectInput;
    private HashMap<String, MessageCrypte> messageRecu = new HashMap<>();
    private final Vote vote;

    // Constructor
    public ClientHandler(Socket s, DataOutputStream dataOutput, ObjectInputStream objectInput, Vote vote) {
        this.s = s;
        this.dataOutput = dataOutput;
        this.objectInput = objectInput;
        this.vote = vote;
    }

    @Override
    public void run() {
        while (true) {
            try {
                dataOutput.writeUTF("vote");
                dataOutput.flush();

                methodeVote(dataOutput, objectInput);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
            break;
        }

        try {// closing resources
            this.objectInput.close();
            this.dataOutput.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void methodeVote(DataOutputStream dataOutput, ObjectInputStream objectInput) throws IOException, ClassNotFoundException {
        dataOutput.writeUTF(vote.toString());
        dataOutput.flush();
        dataOutput.writeUTF("OK");
        dataOutput.flush();

        System.out.println("En attente d'un vote...");

        this.messageRecu = (HashMap<String, MessageCrypte>) objectInput.readObject();

        System.out.println("Vote reçu");
    }

    public HashMap<String, MessageCrypte> getMessageCrypte() {
        return messageRecu;
    }

}
