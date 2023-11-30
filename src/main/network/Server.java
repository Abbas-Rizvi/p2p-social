package network;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.List;

import backend.blockchain.Block;
import backend.blockchain.BlockHeader;
import backend.blockchain.Blockchain;
import backend.blockchain.Post;
import backend.crypt.KeyGen;

public class Server extends Thread {

    static PeersDatabase db = new PeersDatabase();

    // ArrayList<
    static KeyGen key = new KeyGen();
    static String pubKey = key.getPublicKeyStr();
    // static Node node = new Node("jim", key, "127.0.0.1");

    // static String ip = InetAddress.getLocalHost().toString();

    static Node localNode = new Node("Test", pubKey, "192.168.2.24");

    static Blockchain blockchain = new Blockchain();
    static NTPTimeService timeService = new NTPTimeService();

    public void setup() {

        // create list of all known peers

        // setup connections to each

        //

    }

    public static void main(String[] args) {

        String user = db.lookupNameByPublicKey(pubKey);

        if (db.insertRecord(localNode) == 1) {
            System.out.println("User " + localNode.getUsername() + " aleady exists!");
        }

        System.out.println("Execution Started");

        Server server = new Server();
        Thread nodeListen = new Thread(server);

        List<Node> peers = db.readAllNodes();

        nodeListen.start();

        System.out.println("Code is running ");

        ////////////////////////////////////////////////////////////////
        // connect to laptop
        Node node = new Node("192.168.2.59");

        try {
            // create socket channel
            SocketChannel socketChannel = SocketChannel.open();
            InetSocketAddress addr = new InetSocketAddress(node.getIp(), node.getPORT());
            // connect without blocking
            socketChannel.configureBlocking(false);

            if (socketChannel.connect(addr)) {

                System.out.println("Connected");
                // Send the file bytes
//                ByteBuffer buffer = ByteBuffer.wrap(msg2.getFile());
//                socketChannel.write(buffer);

            }

        } catch (Exception e) {
            System.err.println("unable to connect to " + node.getIp() + "; " + e);
        }

        ////////////////////////////////////////////////////////////////
        BlockHeader msg1h = new BlockHeader(timeService.getNTPDate().getTime(), "public", "POST", user);
        Block msg1 = new Post(blockchain.lastHash(), "Test Post", msg1h, key.getPrivatKey());
        blockchain.appendBlock(msg1);


        ////////////////////////////////////////////////////////////////
        SockMessage msg2 = new SockMessage("BLOCKCHAIN", timeService.getNTPDate().getTime());


//        for (Node node : peers) {
//
//            try {
//                // create socket channel
//                SocketChannel socketChannel = SocketChannel.open();
//                InetSocketAddress addr = new InetSocketAddress(node.getIp(), node.getPORT());
//                // connect without blocking
//                socketChannel.configureBlocking(false);
//
//                if (socketChannel.connect(addr)) {
//
//                    // Send the file bytes
//                    ByteBuffer buffer = ByteBuffer.wrap(msg2.getFile());
//                    socketChannel.write(buffer);
//                }
//
//            } catch (Exception e) {
//                System.err.println("unable to connect to " + node.getIp() + "; " + e);
//            }
//
//        }
//
    }

    // Handle the running of the server
    // run on thread to not interupt main program
    @Override
    public void run() {

        System.out.println("Server listening...");
        localNode.listener();

    }

}
