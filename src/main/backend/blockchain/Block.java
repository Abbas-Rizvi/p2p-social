package backend.blockchain;

import java.security.PrivateKey;
import java.security.PublicKey;

public interface Block {

    public void genHeader(String recipientString, String msgType, String sender);
    public BlockHeader readHeader();


    public String currentHash();
    public String getPrevHash();

    public String signBlock(PrivateKey privKey);
    public boolean validSignature(PublicKey pubKey);
    public String getHash();

}
