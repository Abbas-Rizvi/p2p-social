package network;

import java.security.PublicKey;
import java.security.interfaces.RSAKey;

import backend.crypt.RSAKeys;

public class Node {

    private String username;
    private PublicKey pubKey;
    private String ip;


    public Node(String username, String pubKeyStr, String ip) {


        RSAKeys KeyDecode = new RSAKeys();
            

        this.username = username;
        this.ip = ip;

        pubKey = KeyDecode.convertPublicKey(pubKeyStr);
    }


    public String getUsername() {
        return username;
    }


    public void setUsername(String username) {
        this.username = username;
    }


    public PublicKey getPubKey() {
        return pubKey;
    }


    public void setPubKey(PublicKey pubKey) {
        this.pubKey = pubKey;
    }


    public String getIp() {
        return ip;
    }


    public void setIp(String ip) {
        this.ip = ip;
    }


    @Override
    public String toString() {
        return "Node [username=" + username + ", pubKey=" + pubKey + ", ip=" + ip + "]";
    }


    
}
