package Concept;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Random;

public class Encrypt {
    private HashMap<String, BigInteger> pk;
    private BigInteger message;

    private BigInteger r;
    private BigInteger u;
    private BigInteger v;

    public Encrypt(HashMap<String, BigInteger> pk, BigInteger message) {
        this.pk = pk;
        this.message = message;

        this.r = tirerR();
        this.u = encryptU(this.r);
        this.v = encryptV(this.r);
    }


    public BigInteger tirerR() {
        BigInteger pPrime = (this.pk.get("p").subtract(BigInteger.ONE)).divide(BigInteger.valueOf(2));

        Random rand = new Random();
        BigInteger r = new BigInteger(pPrime.bitLength(), rand);

        //teste si r est dans [0;p'-1]
        while (r.compareTo(BigInteger.ZERO) < 0 || r.compareTo(pPrime.subtract(BigInteger.ONE)) > 0) {
            r = new BigInteger(pPrime.bitLength(), rand);
        }

        this.r = r;
        return r;
    }

    public BigInteger encryptU(BigInteger r) {
        BigInteger g = this.pk.get("g");
        BigInteger p = this.pk.get("p");

        BigInteger u = g.modPow(r, p);

        this.u = u;
        return u;
    }

    public BigInteger encryptV(BigInteger r) {

        BigInteger g = this.pk.get("g");
        BigInteger p = this.pk.get("p");
        BigInteger h = this.pk.get("h");

        BigInteger v = g.modPow(this.message, p).multiply(h.modPow(r, p)).mod(p);

        this.v = v;
        return v;
    }

    public HashMap<String, BigInteger> getEncrypt() {

        HashMap<String, BigInteger> encrypt = new HashMap<String, BigInteger>();
        encrypt.put("u", this.u);
        encrypt.put("v", this.v);
        return encrypt;
    }

}
