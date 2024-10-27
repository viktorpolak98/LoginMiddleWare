package Controller;

import java.util.Base64;
import java.util.Random;

public class APIKeyGenerator {
    public String generateKey(){
        byte[] key = new byte[32];
        Random rand = new Random();
        rand.nextBytes(key);
        return Base64.getEncoder().encodeToString(key);
    }
}
