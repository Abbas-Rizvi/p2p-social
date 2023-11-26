package backend.blockchain;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.util.Base64;

import network.NTPTimeService;

public class Post implements Block {

    private NTPTimeService timeService; // for contacting time server

    // header includes recipient, sender, time, msgtype
    private BlockHeader header;

    private String prevHash;
    private String hash;

    private String storedSignature; // sig encoded as string Base64

    private String data;

    // ######################
    // constructor
    // ######################

    public Post(String previousHash, String data, BlockHeader header, PrivateKey privKey) {

        this.header = header;
        this.prevHash = previousHash;
        this.data = data;
        this.hash = currentHash();
        this.storedSignature = signBlock(privKey);

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

    @Override
    public String getPrevHash() {
     
        return prevHash;
    }

}
