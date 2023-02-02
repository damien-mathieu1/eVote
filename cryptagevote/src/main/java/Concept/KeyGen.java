package Concept;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Random;

public class KeyGen {

    private final int taille;
    private  BigInteger p;
    private  BigInteger g;
    private  BigInteger x;
    private  BigInteger h;


    public KeyGen(int taille) {
        this.taille = taille;

        BigInteger p = tirerPremierP();
        BigInteger g = tirerElementG(p);
        BigInteger x = tirerEntierX(p);
        BigInteger h = calculerH(g, x, p);

    }

    public BigInteger tirerPremierP() {
        BigInteger p = BigInteger.probablePrime(taille, new Random());
        BigInteger pPrime = (p.subtract(BigInteger.ONE)).divide(BigInteger.valueOf(2));
        while (!pPrime.isProbablePrime(100)) {
            p = BigInteger.probablePrime(taille, new Random());
            pPrime = (p.subtract(BigInteger.ONE)).divide(BigInteger.valueOf(2));
        }

        this.p = p;
        return p;
    }

    public BigInteger tirerElementG(BigInteger p) {
        BigInteger elementG = new BigInteger(p.bitLength(), new Random());
        boolean gBonneForme = false;

        while (!gBonneForme) {
            elementG = new BigInteger(p.bitLength(), new Random());

            //test si elementG est dans l'intervalle [0, p-1]
            if ((elementG.compareTo(BigInteger.ZERO) >= 0 && elementG.compareTo(p.subtract(BigInteger.ONE)) <= 0)) {

                BigInteger pPrime = (p.subtract(BigInteger.ONE)).divide(BigInteger.valueOf(2));
                //test si elementG^p' = 1 mod p
                if (elementG.modPow(pPrime, p).equals(BigInteger.ONE)) {

                    gBonneForme = true;
                }
            }
        }

        this.g = elementG;
        return g;
    }

    public BigInteger tirerEntierX(BigInteger p) {
        BigInteger pPrime = (p.subtract(BigInteger.ONE)).divide(BigInteger.valueOf(2));
        BigInteger x = new BigInteger(pPrime.bitLength(), new Random());

        //test si x est dans l'intervalle [0, p'-1]
        while (x.compareTo(BigInteger.ZERO) < 0 || x.compareTo(pPrime.subtract(BigInteger.ONE)) > 0) {
            x = new BigInteger(pPrime.bitLength(), new Random());
        }
        this.x = x;
        return x;
    }

    public BigInteger calculerH(BigInteger g, BigInteger x, BigInteger p) {
        BigInteger h =  g.modPow(x, p);
        this.h = h;
        return h;
    }

    public HashMap<String, BigInteger> getPublicKey() {
        HashMap<String, BigInteger> publicKey = new HashMap<>();
        publicKey.put("p", p);
        publicKey.put("g", g);
        publicKey.put("h", h);

        return publicKey;
    }

    public BigInteger getPrivateKey() {
        return x;
    }
}
