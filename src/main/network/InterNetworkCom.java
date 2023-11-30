package network;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class InterNetworkCom extends Thread {

    static NTPTimeService timeService = new NTPTimeService();

    SockMessage sockMessage;
    Node node;

    // constructor
    public InterNetworkCom(Node node, SockMessage sockMessage){
        this.node = node;
        this.sockMessage = sockMessage;
    }


    // send message to node
    public void run() {

        // System.out.println("Thread started for sending to " + node.getIp());
        
        try {
            // create socket channel
            SocketChannel socketChannel = SocketChannel.open();
            InetSocketAddress addr = new InetSocketAddress(node.getIp(), node.getPORT());
            // connect without blocking
            socketChannel.configureBlocking(false);

            socketChannel.connect(addr);

            // check if the connection is complete
            while (!socketChannel.finishConnect()) {

                Thread.sleep(100); // sleep for a short duration
            }

            // System.out.println("# Connected to " + node.getIp());

            // Send the file bytes            
            ByteBuffer buffer = ByteBuffer.wrap(sockMessage.serialize());
            socketChannel.write(buffer);

            if (socketChannel != null && socketChannel.isConnectionPending()) 
                    socketChannel.close();

        } catch (

        Exception e) {
            System.err.println("# Unable to connect to " + node.getIp() + "; " + e);
        }

    }
}
