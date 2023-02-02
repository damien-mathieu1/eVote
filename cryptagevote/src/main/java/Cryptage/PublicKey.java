package Cryptage;

import java.io.Serializable;
import java.math.BigInteger;

public class PublicKey implements Serializable {
    private BigInteger p;
    private BigInteger g;
    private BigInteger h;

    public PublicKey(BigInteger p, BigInteger g, BigInteger h) {
        this.p = p;
        this.g = g;
        this.h = h;
    }

    public BigInteger getP() {
        return this.p;
    }

    public BigInteger getG() {
        return this.g;
    }

    public BigInteger getH() {
        return this.h;
    }

}
