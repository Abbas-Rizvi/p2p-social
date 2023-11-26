package backend.blockchain;

import java.io.Serializable;
import java.security.PrivateKey;
import java.security.PublicKey;

public interface Block<T> extends Serializable {

    public void genHeader(String recipientString, String msgType, String sender);
    public BlockHeader readHeader();


    public String currentHash();
    public String getPrevHash();

    public String signBlock(PrivateKey privKey);
    public boolean validSignature(PublicKey pubKey);
    public String getHash();

    public T getData();

}
