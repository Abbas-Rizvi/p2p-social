package test.backend.crypt;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.PrivateKey;
import java.security.PublicKey;

import org.junit.Test;

import backend.crypt.KeyGen;

public class RSAKeysTest {

    KeyGen genKey = new KeyGen();

    @Test
    public void testGenerateKeys() {

        try {
            genKey.generateRSAKkeyPair();
        } catch (Exception e) {
            e.printStackTrace();
        }

        String file1Path = "./data/id_rsa";
        String file2Path = "./data/id_rsa.pub";

        // Create an instance of File with the specified path
        File file1 = new File(file1Path);
        File file2 = new File(file2Path);

        // Use assertTrue to check if the file exists
        assertTrue("File was not created", file1.exists());
        assertTrue("File was not created", file2.exists());

    }

    @Test
    public void testConvertPublicKey() throws IOException {

        //setup key if not already exist
        try {
            genKey.generateRSAKkeyPair();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // read public key from file

        Path filePath = Paths.get("./data/id_rsa.pub").toAbsolutePath();

        // String key = Files.readString(filePath);
        String key = new String(Files.readAllBytes(filePath), StandardCharsets.UTF_8);


        PublicKey pubkey = genKey.convertPublicKey(key);

        assertNotNull(pubkey);
        

    }


    @Test
    public void testConvertPrivateKey() throws IOException {

        //setup key if not already exist
        try {
            genKey.generateRSAKkeyPair();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // read public key from file
        Path filePath = Paths.get("./data/id_rsa").toAbsolutePath();

        // String key = Files.readString(filePath);
        String key = new String(Files.readAllBytes(filePath), StandardCharsets.UTF_8);


        PrivateKey privKey = genKey.convertPrivateKey(key);

        assertNotNull(privKey);
        

    }


}
