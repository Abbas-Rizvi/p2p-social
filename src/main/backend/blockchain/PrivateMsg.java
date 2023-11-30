package backend.blockchain;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.util.Base64;

import javax.crypto.Cipher;

import backend.crypt.KeyGen;
import network.NTPTimeService;
import network.PeersDatabase;

public class PrivateMsg implements Block {
    private static final long serialVersionUID = 123456789L;

    private NTPTimeService timeService; // for contacting time server

    // header includes recipient, sender, time, msgtype
    public BlockHeader header;

    private String prevHash;
    private String hash;

    private String storedSignature; // sig encoded as string Base64

    private String data;

    private String sender;
    private String recipient;

    // ######################
    // constructor
    // ######################

    public PrivateMsg(String previousHash, String data, BlockHeader header, PrivateKey privKey) {

        this.header = header;
        this.prevHash = previousHash;
        this.hash = currentHash();
        this.storedSignature = signBlock(privKey);

        this.data = encrypt(data, privKey, header.getRecipient());

    }

    // ######################
    // header functions
    // ######################
    @Override
    public void genHeader(String recipientString, String msgType, String sender) {

        header = new BlockHeader(
                timeService.getNTPDate().getTime(),
                recipientString,
                msgType,
                sender);

    }

    @Override
    public BlockHeader readHeader() {
        return header;
    }

    // ######################
    // Block functions
    // ######################

    // encrypt message to sender
    private String encrypt(String data, PrivateKey privKey, String recipient) {

        // find user public key in string format
        PeersDatabase db = new PeersDatabase();
        String recipientPubKeyStr = db.lookupPublicKeyByName(recipient);

        // convert string to a public key object
        KeyGen keys = new KeyGen();
        PublicKey recipientPubKey = keys.convertPublicKey(recipientPubKeyStr);

        if (recipientPubKey == null) {
            System.out.println("User not found!");
        } else {

            try {
                // initialize cipher
                Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
                cipher.init(Cipher.ENCRYPT_MODE, recipientPubKey);

                // encrypt message
                byte[] encryptedBytes = cipher.doFinal(data.getBytes());

                // convert to string
                String encryptedMessage = Base64.getEncoder().encodeToString(encryptedBytes);

                return encryptedMessage;

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        return null;

    }

    public String decrypt(PrivateKey privateKey) {

        try {
            // initialize ciphers
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.DECRYPT_MODE, privateKey);

            // decode Base64 string
            byte[] encryptedBytes = Base64.getDecoder().decode(data);

            // decrypt message
            byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
            String decryptedMessage = new String(decryptedBytes);

            return decryptedMessage;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    

    // generate hash using time for randomness
    @Override
    public String currentHash() {

        String hashData = prevHash + Long.toString(header.getTime());

        MessageDigest digest = null;
        byte[] bytes = null;

        try {
            digest = MessageDigest.getInstance("SHA-256");
            bytes = digest.digest(hashData.getBytes("UTF-8"));

        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            e.getStackTrace();
        }

        StringBuffer buffer = new StringBuffer();

        for (byte b : bytes) {
            buffer.append(String.format("%02x", b));
        }

        return buffer.toString();

    }

    // ######################
    // Signature Management
    // ######################

    // create a signature to be used in a block
    @Override
    public String signBlock(PrivateKey privateKey) {

        try {
            // create signature
            Signature signature = Signature.getInstance("SHA256withRSA");

            // sign and store as byte
            signature.initSign(privateKey);
            signature.update(hash.getBytes("UTF-8"));
            byte[] signatureBytes = signature.sign();

            // encode signature
            String encodedSig = Base64.getEncoder().encodeToString(signatureBytes);

            return encodedSig;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // check if a signature is valid
    @Override
    public boolean validSignature(PublicKey publicKey) {

        try {
            // create signature
            Signature signature = Signature.getInstance("SHA256withRSA");

            // verify signature
            signature.initVerify(publicKey);
            signature.update(hash.getBytes("UTF-8"));

            // decode stored signature
            byte[] signatureByte = Base64.getDecoder().decode(storedSignature);

            // verify stored signature
            return signature.verify(signatureByte);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // get previous hash
    @Override
    public String getPrevHash() {

        return prevHash;
    }

    // get current hash
    public String getHash() {
        return hash;
    }

    public String getData() {
        return data;
    }

    public String getSender() {
        return sender;
    }

    public String getRecipient() {
        return recipient;
    }

}
