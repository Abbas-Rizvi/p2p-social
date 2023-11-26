package network;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import backend.blockchain.Blockchain;

public class SockMessage implements Serializable {

    private static final long serialVersionUID = 123456789L;


    private String type;
    private Long time;
    private byte[] file;

    private Blockchain blockchain = new Blockchain();
    private PeersDatabase db = new PeersDatabase();

    // define message for sending packets over network
    public SockMessage(String type, Long time, byte[] file) {
        this.type = type;
        this.time = time;
        this.file = file;

    }

    // types of messages:
    // Blockchain
    // Database

    public SockMessage(String type, long time) {
        this.type = type;
        this.time = time;

        if (type == "BLOCKCHAIN") {
            this.file = blockchain.serialize();
        } else if (type == "DATABASE") {
            this.file = db.databaseToByteArr();
        }

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

    // read message from byte[]
    public SockMessage deserialize(byte[] msgBytes) {

        try {
            ByteArrayInputStream byteArrayIn = new ByteArrayInputStream(msgBytes);
            ObjectInputStream ois = new ObjectInputStream(byteArrayIn);

            // cast input to object
            Object obj = ois.readObject();

            if (obj instanceof SockMessage) {
                return (SockMessage) obj;
            } else {
                return null;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    // convert message to a byte[]
    public byte[] serialize() {

        try {

            // create output streams
            ByteArrayOutputStream byteArrayOut = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(byteArrayOut);

            // Write the object to the byte array stream
            oos.writeObject(this);

            // get byte array from the output stream
            byte[] byteArray = byteArrayOut.toByteArray();

            return byteArray;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

}
