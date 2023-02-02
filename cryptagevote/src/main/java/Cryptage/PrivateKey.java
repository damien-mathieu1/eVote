package Cryptage;

import java.math.BigInteger;

public class PrivateKey {
    private BigInteger x;

    public PrivateKey(BigInteger x) {
        this.x = x;
    }

    public BigInteger getX() {
        return this.x;
    }

}
