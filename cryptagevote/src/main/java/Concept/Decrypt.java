package Concept;

import java.math.BigInteger;
import java.util.HashMap;

public class Decrypt {

    private BigInteger u;
    private BigInteger v;
    private BigInteger x;
    private BigInteger p;
    private BigInteger g;


    public Decrypt(HashMap<String, BigInteger> messagecrypte, BigInteger sk, HashMap<String, BigInteger> pk) {
        this.u = messagecrypte.get("u");
        this.v = messagecrypte.get("v");
        this.x = sk;

        this.p = pk.get("p");
        this.g = pk.get("g");

    }

    public BigInteger getGrandM() {
        return this.v.multiply(this.u.modPow(this.x.negate(), this.p)).mod(this.p);
    }

    public BigInteger getMessageDecrypte() {
        BigInteger grandM = getGrandM();

        BigInteger m = BigInteger.ZERO;
        while (!grandM.equals(g.modPow(m, p))) {
            m = m.add(BigInteger.ONE);
        }
        return m;
    }
}
