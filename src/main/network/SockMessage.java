package network;

import java.io.Serializable;

public class SockMessage implements Serializable {

    private String type;
    private Long time;
    private byte[] file;


    // define message for sending packets over network
    public SockMessage(String type, Long time, byte[] file) {
        this.type = type;
        this.time = time;
        this.file = file;


    }


    public String getType() {
        return type;
    }


    public Long getTime() {
        return time;
    }


    public byte[] getFile() {
        return file;
    }



    // types of messages:
    // Blockchain
    // Database


    
    
    
}
