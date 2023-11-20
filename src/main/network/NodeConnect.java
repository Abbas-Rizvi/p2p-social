package network;

import java.io.IOException;
import java.net.Socket;

public class NodeConnect implements Runnable {
    private String peerAddress;
    private int peerPort;

    public NodeConnect (String peerAddress, int peerPort) {
        this.peerAddress = peerAddress;
        this.peerPort = peerPort;
    }

    @Override
    public void run() {
        try {
            // Attempt to connect to the peer
            Socket socket = new Socket(peerAddress, peerPort);
            
            // If the connection is successful, you can perform further actions here
            
            // Close the socket when done
            socket.close();
        } catch (IOException e) {
            // Handle connection errors
            System.err.println("Failed to connect to " + peerAddress + ":" + peerPort);
        }
    }


}
