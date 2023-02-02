package Concept;

import Concept.Decrypt;
import Concept.Encrypt;
import Concept.KeyGen;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DecryptTest {

    KeyGen keyGen = new KeyGen(50);
    BigInteger x = keyGen.getPrivateKey();

    Encrypt encrypt = new Encrypt(keyGen.getPublicKey(), BigInteger.ONE);
    HashMap<String, BigInteger> messagecrypte = encrypt.getEncrypt();

    Decrypt decrypt = new Decrypt(messagecrypte, x, keyGen.getPublicKey());

    @Test
    public void getDecryptM(){
        BigInteger m = decrypt.getMessageDecrypte();
        assertEquals(BigInteger.ONE, m, "Le message déchiffré n'est pas correct");
    }
}
