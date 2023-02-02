package Cryptage;

import java.math.BigInteger;
import java.util.HashMap;

import Concept.KeyGen;

public class KeyGenCryptage {
    private BigInteger p;
    private BigInteger g;
    private BigInteger h;
    private BigInteger x;


    /**
     * KeyGenCryptage génère une clé publique (PublicKey) et une clé privée (PrivateKey) sur le modèle de Elgamal
     * afin de générer des messages cryptés avec la classe MessageCrypte
     * @param taille taille de la clé en bits
     */
    public KeyGenCryptage(int taille) {
        KeyGen keyGen = new KeyGen(taille);
        HashMap<String, BigInteger> hashMap = keyGen.getPublicKey();

        this.p = hashMap.get("p");
        this.g = hashMap.get("g");
        this.h = hashMap.get("h");
        this.x = keyGen.getPrivateKey();
    }

    /**
     * Retourne la clé publique générée
     * @return la clé publique, de type PublicKey
     */
    public PublicKey getPublicKey() {
        return new PublicKey(this.p, this.g, this.h);
    }

    /**
     * Retourne la clé privée générée
     * @return la clé privée, de type PrivateKey
     */
    public PrivateKey getPrivateKey() {
        return new PrivateKey(this.x);
    }
}
