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

        // create objects for both chain and database
        SockMessage msgChain = new SockMessage("BLOCKCHAIN", time,blockchain.serialize());
        SockMessage msgNodes = new SockMessage("NODELIST", time, db.serialize());
        SockMessage handshake = new SockMessage("HANDSHAKE", time);

        try {

            // send both files over the socket
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(sock.socket().getOutputStream());
            objectOutputStream.writeObject(msgChain);
            objectOutputStream.writeObject(msgNodes);
            objectOutputStream.writeObject(handshake);
            System.out.println("chain and node list sent! : " + sock.getRemoteAddress());

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    // send nodeList stored on node
    public void sendNodeList(SocketChannel sock) {

        // get time
        long time = timeService.getNTPDate().getTime();

        // create objects nodelist msg
        SockMessage msgNodes = new SockMessage("NODELIST", time, db.serialize());

        try {

            // send file over the socket
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(sock.socket().getOutputStream());
            objectOutputStream.writeObject(msgNodes);
            System.out.println("node list sent! : " + sock.getRemoteAddress());

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    
    // send blockchain stored locally
    public void sendBlockChain(SocketChannel sock) {

        // get time
        long time = timeService.getNTPDate().getTime();

        // create objects for chain 
        SockMessage msgChain = new SockMessage("BLOCKCHAIN", time,blockchain.serialize());

        try {

            // send both files over the socket
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(sock.socket().getOutputStream());
            objectOutputStream.writeObject(msgChain);
            System.out.println("chain sent! : " + sock.getRemoteAddress());

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
