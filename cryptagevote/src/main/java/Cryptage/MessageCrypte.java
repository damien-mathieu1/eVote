package Cryptage;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.HashMap;

import Concept.Encrypt;
import Concept.Decrypt;
import Concept.Agrege;

public class MessageCrypte implements Serializable {
    private BigInteger u;
    private BigInteger v;

    /**
     * MessageCrypte génère un message crypté à partir d'un message clair et d'une clé publique générée à l'aide de la classe KeyGenCryptage
     * @param message message clair à crypter de type int
     * @param pk clé publique de type PublicKey
     */
    public MessageCrypte(int message, PublicKey pk) {
        HashMap<String, BigInteger> hashMapPK = new HashMap<String, BigInteger>();
        hashMapPK.put("p", pk.getP());
        hashMapPK.put("g", pk.getG());
        hashMapPK.put("h", pk.getH());

        Encrypt encrypt = new Encrypt(hashMapPK, BigInteger.valueOf(message));

        HashMap<String, BigInteger> messageCrypte = encrypt.getEncrypt();

        this.u = messageCrypte.get("u");
        this.v = messageCrypte.get("v");
    }

    private MessageCrypte(BigInteger u, BigInteger v) {
        this.u = u;
        this.v = v;
    }

    private BigInteger getU() {
        return this.u;
    }

    private BigInteger getV() {
        return this.v;
    }

    /**
     * Permet l'addition de deux messages cryptés
     * @param messageCrypte premier message crypté de type MessageCrypte
     * @param pk clé publique de type PublicKey
     * @return l'agrégation (la somme) des deux messages cryptés de type MessageCrypte
     */
    public MessageCrypte agregate(MessageCrypte messageCrypte, PublicKey pk) {
        HashMap<String, BigInteger> hashMapPK = new HashMap<String, BigInteger>();
        hashMapPK.put("p", pk.getP());
        hashMapPK.put("g", pk.getG());
        hashMapPK.put("h", pk.getH());

        Agrege agrege = new Agrege(hashMapPK);

        HashMap<String, BigInteger> message1 = new HashMap<String, BigInteger>();
        message1.put("u", this.u);
        message1.put("v", this.v);

        HashMap<String, BigInteger> message2 = new HashMap<String, BigInteger>();
        message2.put("u", messageCrypte.getU());
        message2.put("v", messageCrypte.getV());

        HashMap<String, BigInteger> messageAgreg = agrege.agregate(message1, message2);

        return new MessageCrypte(messageAgreg.get("u"), messageAgreg.get("v"));
    }

    /**
     * Permet de décrypter un message crypté à l'aide d'une clé privée
     * @param pk clé privée de type PrivateKey
     * @param sk clé publique de type PublicKey
     * @return le message clair de type int
     */
    public int decrypt(PrivateKey sk, PublicKey pk) {

        HashMap<String, BigInteger> messageCrypte = new HashMap<String, BigInteger>();
        messageCrypte.put("u", this.u);
        messageCrypte.put("v", this.v);

        HashMap<String, BigInteger> hashMapPK = new HashMap<String, BigInteger>();
        hashMapPK.put("p", pk.getP());
        hashMapPK.put("g", pk.getG());
        hashMapPK.put("h", pk.getH());

        BigInteger BigIntSK = sk.getX();

        Decrypt decrypt = new Decrypt(messageCrypte, BigIntSK, hashMapPK);

        return decrypt.getMessageDecrypte().intValue();
    }

    public String toString() {
        return "u = " + this.u + " v = " + this.v;
    }



}
