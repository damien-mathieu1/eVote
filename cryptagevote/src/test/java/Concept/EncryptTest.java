package Concept;

import Concept.Encrypt;
import Concept.KeyGen;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.util.HashMap;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class EncryptTest {

    KeyGen keyGen = new KeyGen(50);

    HashMap<String, BigInteger> pk = keyGen.getPublicKey();

    BigInteger message = new BigInteger("1");

    Encrypt encrypt = new Encrypt(pk, message);

    @Test
    public void testTirerR() {
        BigInteger r = encrypt.tirerR();

        //on teste si r est bien compris entre 0 et p'-1
        BigInteger p = pk.get("p");
        BigInteger pPrime = (p.subtract(BigInteger.ONE)).divide(BigInteger.valueOf(2));
        assertTrue(r.compareTo(BigInteger.ZERO) >= 0 && r.compareTo(pPrime.subtract(BigInteger.ONE)) <= 0, "r n'est pas compris entre 0 et p'-1");
    }

    @Test
    public void testEncryptU() {
        BigInteger r = encrypt.tirerR();

        BigInteger u = encrypt.encryptU(r);

        //on teste si u = g^r mod p
        BigInteger g = pk.get("g");
        BigInteger p = pk.get("p");
        assertEquals(u, g.modPow(r, p), "u != g^r mod p");
    }

    @Test
    public void testEncryptV() {
        BigInteger r = encrypt.tirerR();

        BigInteger v = encrypt.encryptV(r);

        //on teste si v = g^m * h^r mod p
        BigInteger h = pk.get("h");
        BigInteger p = pk.get("p");
        BigInteger g = pk.get("g");
        assertEquals(v, g.modPow(message, p).multiply(h.modPow(r, p)).mod(p), "v != g^m * h^r mod p");
    }


}
