package backend.blocks;

import java.security.Signature;

// header for data block
// outline message intent
public class BlockHeader {

    private long time;
    private String recipient;
    private String msgType;
    private String sender;
    private Signature sig;


    public BlockHeader(long time, String recipient, String msgType, String sender) {
        this.time = time;
        this.recipient = recipient;
        this.msgType = msgType;
        this.sender = sender;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }


    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public String getMsgType() {
        return msgType;
    }

    public void setMsgType(String msgType) {
        this.msgType = msgType;
    }

}
