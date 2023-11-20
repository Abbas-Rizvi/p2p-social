package backend.crypt;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class RSAKeys {

    private static final String RSA = "RSA";

    private KeyPair kp;

    // Generating public & private keys
    // using RSA algorithm.
    public void generateRSAKkeyPair() throws Exception {

        SecureRandom secureRandom = new SecureRandom();
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(RSA);

        keyPairGenerator.initialize(4096, secureRandom);

        kp = keyPairGenerator.generateKeyPair();

        storeKey(kp.getPrivate(), "./data/id_rsa");
        storeKey(kp.getPublic(), "./data/id_rsa.pub");
    }

    // store key for later use
    private void storeKey(Key key, String filePath) throws Exception {

        byte[] keyBytes = key.getEncoded();

        // encode key as base64
        // byte[] encodedKey = new String(Base64.getEncoder().encode(keyBytes));
        byte[] encodedKey = Base64.getEncoder().encode(keyBytes);

        try (FileOutputStream fos = new FileOutputStream(filePath)){
            fos.write(encodedKey);
        }

        // try (PrintWriter out = new PrintWriter(filePath)) {
            // out.println(encodedKey);
        // }

    }

    // check keys
    public boolean checkKeysExist() {

        String file1Path = "./data/id_rsa";
        String file2Path = "./data/id_rsa.pub";

        // Create an instance of File with the specified path
        File file1 = new File(file1Path);
        File file2 = new File(file2Path);

        if (file1.exists() && file2.exists())
            return true;
        else
            return false;

    }

    // connverts encoded public key to publicKey object
    public PublicKey convertPublicKey(String pubKeyStr) {

        // Decode the Base64-encoded public key
        byte[] decodedKey = Base64.getDecoder().decode(pubKeyStr);

        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(decodedKey);
        
        KeyFactory keyFactory;

        try {

            keyFactory = KeyFactory.getInstance("RSA");
            PublicKey pubKey = keyFactory.generatePublic(keySpec);
            return pubKey;

        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {

            e.printStackTrace();

        }

            

        return null;

    }

}