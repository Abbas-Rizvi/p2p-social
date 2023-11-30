package backend.crypt;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class KeyGen implements Serializable {

    private static final long serialVersionUID = 123456789L;

    private static final String RSA = "RSA";

    private KeyPair kp;

    // Generating public & private keys
    // using RSA algorithm.
    public void generateRSAKkeyPair() {

        try {
            SecureRandom secureRandom = new SecureRandom();
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(RSA);

            keyPairGenerator.initialize(4096, secureRandom);

            kp = keyPairGenerator.generateKeyPair();

            storeKey(kp.getPrivate(), "./data/id_rsa");
            storeKey(kp.getPublic(), "./data/id_rsa.pub");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // store key for later use
    private void storeKey(Key key, String filePath) throws Exception {

        byte[] keyBytes = key.getEncoded();

        // encode key as base64
        // byte[] encodedKey = new String(Base64.getEncoder().encode(keyBytes));
        byte[] encodedKey = Base64.getEncoder().encode(keyBytes);

        try (FileOutputStream fos = new FileOutputStream(filePath)) {
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

    // ##########################
    // Public key functions
    // ##########################

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

    public PublicKey getPublicKey() {

        // read public key from file
        Path filePath = Paths.get("./data/id_rsa.pub").toAbsolutePath();

        String key;

        try {

            key = new String(Files.readAllBytes(filePath), StandardCharsets.UTF_8);
            PublicKey pubkey = convertPublicKey(key);
            return pubkey;

        } catch (IOException e) {

            e.printStackTrace();

        }

        return null;
    }

    public String getPublicKeyStr() {

        // read public key from file
        Path filePath = Paths.get("./data/id_rsa.pub").toAbsolutePath();

        String pubKeyStr;

        try {

            pubKeyStr = new String(Files.readAllBytes(filePath), StandardCharsets.UTF_8);
            return pubKeyStr;

        } catch (IOException e) {

            e.printStackTrace();

        }

        return null;
    }

    // ##########################
    // Private key functions
    // ##########################

    public String getPrivateKeyString() {

        // read public key from file
        Path filePath = Paths.get("./data/id_rsa").toAbsolutePath();

        String privKeyString;

        try {

            privKeyString = new String(Files.readAllBytes(filePath), StandardCharsets.UTF_8);
            return privKeyString;

        } catch (IOException e) {

            e.printStackTrace();

        }

        return null;
    }

    // connverts encoded public key to publicKey object
    public PrivateKey convertPrivateKey(String privKeyString) {

        // Decode the Base64-encoded public key
        byte[] decodedKey = Base64.getDecoder().decode(privKeyString);

        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(decodedKey);

        KeyFactory keyFactory;

        try {

            keyFactory = KeyFactory.getInstance("RSA");
            PrivateKey privKey = keyFactory.generatePrivate(keySpec);
            return privKey;

        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {

            e.printStackTrace();

        }

        return null;

    }

    public PrivateKey getPrivatKey() {

        // read public key from file
        Path filePath = Paths.get("./data/id_rsa").toAbsolutePath();

        String key;

        try {

            key = new String(Files.readAllBytes(filePath), StandardCharsets.UTF_8);
            PrivateKey privKey = convertPrivateKey(key);
            return privKey;

        } catch (IOException e) {

            e.printStackTrace();

        }

        return null;
    }

}