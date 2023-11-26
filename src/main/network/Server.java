package network;

import java.util.ArrayList;
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
    

    static Blockchain blockchain = new Blockchain();
    static NTPTimeService timeService = new NTPTimeService();


    public void setup() {

        // create list of all known peers

        // setup connections to each

        //

    }

    public static void main(String[] args) {
        // // Create and start a thread for each peer
        // for (String peerAddress : peerAddresses) {
        // Thread thread = new Thread(new PeerToPeerConnector(peerAddress, peerPort));
        // thread.start();
        // }

        String user = db.lookupNameByPublicKey(pubKey);

        System.out.println("Execution Started");

        Server server = new Server();
        Thread nodeListen = new Thread(server);

        List<Node> peers = db.readAllNodes();

       



        nodeListen.start();


        System.out.println("Code is running ");


        BlockHeader msg1h = new BlockHeader(timeService.getNTPDate().getTime(), "public", "POST",user);
        Block msg1 = new Post(blockchain.lastHash(), "Test Post", msg1h, key.getPrivatKey());

        blockchain.appendBlock(msg1);

        SockMessage msg2 = new SockMessage("BLOCKCHAIN", timeService.getNTPDate().getTime(),bloc;
        for (Node node : peers){
        }
        

    }



    // Handle the running of the server
    // run on thread to not interupt main program
    @Override
    public void run() {

        
        System.out.println("Server listening...");
        node.listener();

    }


}
