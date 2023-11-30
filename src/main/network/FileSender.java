package network;

import java.io.ObjectOutputStream;
import java.nio.channels.SocketChannel;

import backend.blockchain.Blockchain;

/// used for getting files stored locally and sending over sockets
public class FileSender {

    private Blockchain blockchain = new Blockchain();
    private PeersDatabase db = new PeersDatabase();
    private NTPTimeService timeService = new NTPTimeService();

    // send all assets stored locally
    public void sendAllAssets(SocketChannel sock) {

        // get time
        long time = timeService.getNTPDate().getTime();

        String reqIP = sock.socket().getInetAddress().getHostAddress();
        Node targNode = new Node(reqIP);

        // create objects for both chain and database
        SockMessage msgChain = new SockMessage("BLOCKCHAIN", time, blockchain.serialize());
        SockMessage msgNodes = new SockMessage("NODELIST", time, db.serialize());

        // request files back from the node
        SockMessage handshake = new SockMessage("HANDSHAKE-RECV", time);

        try {

            // // send both files over the socket
            // ObjectOutputStream objectOutputStream = new
            // ObjectOutputStream(sock.socket().getOutputStream());

            InterNetworkCom interNet = new InterNetworkCom(targNode, msgChain);
            interNet.start();

            InterNetworkCom interNet2 = new InterNetworkCom(targNode, msgNodes);
            interNet2.start();

            InterNetworkCom interNet3 = new InterNetworkCom(targNode, handshake);
            interNet3.start();

            System.out.println("HANDSHAKE INITIATED: " + sock.getRemoteAddress());

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    // send nodeList stored on node
    public void sendNodeList(SocketChannel sock) {

        // get time
        long time = timeService.getNTPDate().getTime();

        String reqIP = sock.socket().getInetAddress().getHostAddress();
        Node targNode = new Node(reqIP);

        // create objects nodelist msg
        SockMessage msgNodes = new SockMessage("NODELIST", time, db.serialize());

        try {

            InterNetworkCom interNet = new InterNetworkCom(targNode, msgNodes);
            interNet.start();

            System.out.println("node list sent! : " + sock.getRemoteAddress());

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    // send blockchain stored locally
    public void sendBlockChain(SocketChannel sock) {

        // get time
        long time = timeService.getNTPDate().getTime();

        String reqIP = sock.socket().getInetAddress().getHostAddress();
        Node targNode = new Node(reqIP);
        // create objects for chain
        SockMessage msgChain = new SockMessage("BLOCKCHAIN", time, blockchain.serialize());

        try {

            InterNetworkCom interNet = new InterNetworkCom(targNode, msgChain);
            interNet.start();

            System.out.println("chain sent! : " + sock.getRemoteAddress());

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
