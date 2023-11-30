package test.network;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import network.Node;
import network.SockMessage;

public class NodeTest {
    
    @Test
    public void testDecodeMessage() {

        SockMessage msg = new SockMessage("BLOCKCHAIN", 0);
        byte[] testMsg = msg.serialize();


        Node node = new Node("127.0.0.1");
        String decoded = node.decodeMessage(testMsg, null);


        assertEquals(decoded, "BLOCKCHAIN");


        

    }


}
