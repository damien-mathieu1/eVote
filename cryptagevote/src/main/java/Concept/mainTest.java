package Concept;

import java.math.BigInteger;
import java.util.HashMap;

public class mainTest{

    public static void main (String[] args){
        KeyGen keyGen = new KeyGen(50);
        System.out.println("La clé privée est : " + keyGen.getPrivateKey());
        System.out.println("La clé publique est : " + keyGen.getPublicKey());

        BigInteger vote1 = BigInteger.valueOf(1);
        BigInteger vote2 = BigInteger.ONE;
        BigInteger vote3 = BigInteger.ZERO;
        BigInteger vote4 = BigInteger.ZERO;
        BigInteger vote5 = BigInteger.ONE;
        BigInteger vote6 = BigInteger.ONE;
        BigInteger vote7 = BigInteger.ONE;

        Encrypt encrypt1 = new Encrypt(keyGen.getPublicKey(), vote1);
        Encrypt encrypt2 = new Encrypt(keyGen.getPublicKey(), vote2);
        Encrypt encrypt3 = new Encrypt(keyGen.getPublicKey(), vote3);
        Encrypt encrypt4 = new Encrypt(keyGen.getPublicKey(), vote4);
        Encrypt encrypt5 = new Encrypt(keyGen.getPublicKey(), vote5);
        Encrypt encrypt6 = new Encrypt(keyGen.getPublicKey(), vote6);
        Encrypt encrypt7 = new Encrypt(keyGen.getPublicKey(), vote7);

        HashMap<String, BigInteger> messagecrypte1 = encrypt1.getEncrypt();
        HashMap<String, BigInteger> messagecrypte2 = encrypt2.getEncrypt();
        HashMap<String, BigInteger> messagecrypte3 = encrypt3.getEncrypt();
        HashMap<String, BigInteger> messagecrypte4 = encrypt4.getEncrypt();
        HashMap<String, BigInteger> messagecrypte5 = encrypt5.getEncrypt();
        HashMap<String, BigInteger> messagecrypte6 = encrypt6.getEncrypt();
        HashMap<String, BigInteger> messagecrypte7 = encrypt7.getEncrypt();

        HashMap<String, BigInteger> messagecrypteagrege = new HashMap<String, BigInteger>();
        Agrege agrege1 = new Agrege(keyGen.getPublicKey());

        messagecrypteagrege = agrege1.agregate(messagecrypte1, messagecrypte2);
        messagecrypteagrege = agrege1.agregate(messagecrypteagrege, messagecrypte3);
        messagecrypteagrege = agrege1.agregate(messagecrypteagrege, messagecrypte4);
        messagecrypteagrege = agrege1.agregate(messagecrypteagrege, messagecrypte5);
        messagecrypteagrege = agrege1.agregate(messagecrypteagrege, messagecrypte6);
        messagecrypteagrege = agrege1.agregate(messagecrypteagrege, messagecrypte7);

        Decrypt decrypt = new Decrypt(messagecrypteagrege, keyGen.getPrivateKey(), keyGen.getPublicKey());

        System.out.println("Nombre de votants = 7");
        System.out.println("Le message déchiffré est : " + decrypt.getMessageDecrypte());
        System.out.println("il y a " + decrypt.getMessageDecrypte() + " votants pour la proposition 1");

    }
}
