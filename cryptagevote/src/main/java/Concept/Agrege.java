package Concept;

import java.math.BigInteger;
import java.util.HashMap;

public class Agrege {
    HashMap<String, BigInteger> pk;

    public Agrege(HashMap<String, BigInteger> pk) {
        this.pk = pk;
    }

    public HashMap<String, BigInteger> agregate(HashMap<String, BigInteger> message1, HashMap<String, BigInteger> message2) {
        HashMap<String, BigInteger> messageAgreg = new HashMap<String, BigInteger>();

        BigInteger u = message1.get("u").multiply(message2.get("u")).mod(this.pk.get("p"));
        BigInteger v = message1.get("v").multiply(message2.get("v")).mod(this.pk.get("p"));

        messageAgreg.put("u", u);
        messageAgreg.put("v", v);

        return messageAgreg;
    }
}
