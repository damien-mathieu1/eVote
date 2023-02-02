package Concept;

import Concept.KeyGen;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class KeyGenTest {

    int taille = 10;
    KeyGen keyGen = new KeyGen(taille);

    @Test
    public void testTirerPremierP() {
        BigInteger p = keyGen.tirerPremierP();

        //on teste si p est bien de la taille demandée
        assertEquals(taille, p.bitLength(), "La taille de p n'est pas correcte");

        //on teste si p est bien premier
        assertTrue(p.isProbablePrime(100), "p n'est pas premier");

        //on teste si p est bien de la forme p = 2p' + 1 avec p' premier
        BigInteger pPrime = (p.subtract(BigInteger.ONE)).divide(BigInteger.valueOf(2));
        assertTrue(pPrime.isProbablePrime(100), "p n'est pas de la forme p = 2p' + 1 avec p' premier");
    }

    @Test
    public void testTirerElementG() {
        BigInteger p = keyGen.tirerPremierP();

        BigInteger g = keyGen.tirerElementG(p);

        //on teste si g est bien compris entre 0 et p-1
        assertTrue(g.compareTo(BigInteger.ZERO) >= 0 && g.compareTo(p.subtract(BigInteger.ONE)) <= 0, "g n'est pas compris entre 0 et p-1");

        //on test si g^p' = 1 mod p
        BigInteger pPrime = (p.subtract(BigInteger.ONE)).divide(BigInteger.valueOf(2));
        assertEquals(g.modPow(pPrime, p), BigInteger.ONE, "g^p' != 1 mod p");
    }

    @Test
    public void testTirerEntierX() {
        BigInteger p = keyGen.tirerPremierP();
        BigInteger x = keyGen.tirerEntierX(p);

        //on teste si x est bien compris entre 0 et p'-1
        BigInteger pPrime = (p.subtract(BigInteger.ONE)).divide(BigInteger.valueOf(2));
        assertTrue(x.compareTo(BigInteger.ZERO) >= 0 && x.compareTo(pPrime.subtract(BigInteger.ONE)) <= 0, "x n'est pas compris entre 0 et p'-1");
    }

    @Test
    public void testCalculerH() {
        BigInteger p = keyGen.tirerPremierP();
        BigInteger g = keyGen.tirerElementG(p);
        BigInteger x = keyGen.tirerEntierX(p);

        BigInteger h = keyGen.calculerH(g, x, p);

        //on teste si h = g^x mod p
        assertEquals(h, g.modPow(x, p), "h != g^x mod p");
    }

    @Test
    public void testClesFinal() {
        BigInteger p = keyGen.getPublicKey().get("p");
        BigInteger g = keyGen.getPublicKey().get("g");
        BigInteger h = keyGen.getPublicKey().get("h");

        BigInteger x = keyGen.getPrivateKey();

        BigInteger pPrime = (p.subtract(BigInteger.ONE)).divide(BigInteger.valueOf(2));
        //on teste si p est bien de la taille demandée
        assertEquals(taille, p.bitLength(), "La taille de p n'est pas correcte");

        //on teste si p est bien premier
        assertTrue(p.isProbablePrime(100), "p n'est pas premier");

        //on teste si p est bien de la forme p = 2p' + 1 avec p' premier
        assertTrue(pPrime.isProbablePrime(100), "p n'est pas de la forme p = 2p' + 1 avec p' premier");

        //on teste si g est bien compris entre 0 et p-1
        assertTrue(g.compareTo(BigInteger.ZERO) >= 0 && g.compareTo(p.subtract(BigInteger.ONE)) <= 0, "g n'est pas compris entre 0 et p-1");

        //on test si g^p' = 1 mod p
        assertEquals(g.modPow(pPrime, p), BigInteger.ONE, "g^p' != 1 mod p");

        //on teste si x est bien compris entre 0 et p'-1
        assertTrue(x.compareTo(BigInteger.ZERO) >= 0 && x.compareTo(pPrime.subtract(BigInteger.ONE)) <= 0, "x n'est pas compris entre 0 et p'-1");

        //on teste si h = g^x mod p
        assertEquals(h, g.modPow(x, p), "h != g^x mod p");

    }
}
