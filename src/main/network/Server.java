package network;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;

import javax.management.loading.PrivateClassLoader;

import backend.blockchain.Block;
import backend.blockchain.BlockHeader;
import backend.blockchain.Blockchain;
import backend.blockchain.Post;
import backend.blockchain.PrivateMsg;
import backend.crypt.KeyGen;

public class Server extends Thread {

    static PeersDatabase db = new PeersDatabase();

    static KeyGen key;

    static String username;
    static Node localNode;
    static Blockchain blockchain = new Blockchain();
    static NTPTimeService timeService = new NTPTimeService();

    public static InetAddress hostIp() {

        try {
            return InetAddress.getLocalHost();
        } catch (UnknownHostException e) {

            e.printStackTrace();
        }

        return null;
    }

    public static void setup() {

        // welcome prompt
        System.out.println("########################");
        System.out.println(" Welcome to P2P Social! ");
        System.out.println("########################");

        // get ip of host
        String localIp = hostIp().getHostAddress();

        // check if keys exist, if not prompt username
        if (checkKeysExist()) {
            // ------------------- todo ----------------------------
            // PROMPT USER FOR INPUT FOR A USERNAME
            username = "Abbas";

            key = new KeyGen();
            // create the local node
            localNode = new Node(username, key.getPublicKeyStr(), localIp);

            // check if name not already taken
            if (db.insertRecord(localNode) == 1) {
                System.out.println("User " + localNode.getUsername() + " aleady exists!");
            }

        } else {
            key = new KeyGen();
            username = db.lookupNameByPublicKey(db.lookupNameByPublicKey(key.getPublicKeyStr()));

            // create the local node
            localNode = new Node(username, key.getPublicKeyStr(), localIp);
        }

    }

    public static boolean checkKeysExist() {

        String file1Path = "./data/id_rsa";
        String file2Path = "./data/id_rsa.pub";

        // Create an instance of File with the specified path
        File file1 = new File(file1Path);
        File file2 = new File(file2Path);

        if (file1.exists() && file2.exists())
            return true;

        // if no keys exist
        return false;

    }

    // connect a new node
    public static void connectNode(String ip) {

        long time = timeService.getNTPDate().getTime();

        // create newNode object
        Node newNode = new Node(ip);

        // create message for new node addition
        SockMessage newNodeMsg = new SockMessage("HANDSHAKE", time);

        // Start a thread for the connection
        InterNetworkCom interNet = new InterNetworkCom(newNode, newNodeMsg);
        interNet.start();
    }

    public static void createPost(String topic, String message) {

        long time = timeService.getNTPDate().getTime();

        // create a new block and append to chain
        BlockHeader msg1h = new BlockHeader(time, topic, "POST", username);
        Block post = new Post(blockchain.lastHash(), message, msg1h, key.getPrivatKey());
        blockchain.appendBlock(post);

        // inform all other nodes in known peers
        for (Node node : db.readAllNodes()) {

            // create message for new node addition
            SockMessage updateChainMsg = new SockMessage("BLOCKCHAIN", time);

            // Start a thread for each connection to inform
            InterNetworkCom interNet = new InterNetworkCom(node, updateChainMsg);
            interNet.start();
        }

    }


    public static void createMessage(String recipient, String message) {

        long time = timeService.getNTPDate().getTime();

        // create a new block and append to chain
        BlockHeader msg1h = new BlockHeader(time, recipient, "POST", username);
        Block msg1 = new PrivateMsg(blockchain.lastHash(), message, msg1h, key.getPrivatKey());
        blockchain.appendBlock(msg1);

        // inform all other nodes in known peers
        for (Node node : db.readAllNodes()) {

            // create message for new node addition
            SockMessage updateChainMsg = new SockMessage("BLOCKCHAIN", time);

            // Start a thread for each connection to inform
            InterNetworkCom interNet = new InterNetworkCom(node, updateChainMsg);
            interNet.start();
        }

    }


    public static void readMessages() {

        ArrayList<Block> allBlocks = blockchain.getBlockchain();

        System.out.println("----- Messages ------");

        for (Block block : allBlocks) {

            // check if block is a message
            if (block instanceof PrivateMsg) {

                PrivateMsg message = (PrivateMsg) block;

                // check if post is to user
                // if so try to decrypt it 
                if (true || message.header.getRecipient().equalsIgnoreCase(username)) {

                    // output post
                    System.out.println(
                            message.header.getSender() + " - " +
                                    timeService.formatedTime(message.header.getTime()) + " : " +
                                    message.decrypt(key.getPrivatKey()));
                }

            }

        }

    }

    public static void readPosts() {

        ArrayList<Block> allBlocks = blockchain.getBlockchain();

        System.out.println("----- Posts ------");

        for (Block block : allBlocks) {

            // check if block is a post
            if (block instanceof Post) {

                Post post = (Post) block;

                // output post
                System.out.println(
                        post.header.getSender() + " - " +
                                timeService.formatedTime(post.header.getTime()) + " : " +
                                post.getData());

            }

        }

    }

    public static void listPeers(){

        for (Node node : db.readAllNodes()){

            System.out.println(
                node.getUsername() + "\t\t" + 
                node.getIp());

        }
    }

    public static void main(String[] args) {

        // setup user node
        // YOU NEED TO ENTER USERNAME IN SETUP
        setup();

        // Start a listening server thread
        Server server = new Server();
        Thread nodeListen = new Thread(server);
        nodeListen.start();

        // connect laptop
        // connectNode("192.168.194.181");

        // Create a new post for the block chain
        // createPost("public", "Hello post");

        // readPosts();
        // listPeers();
        createMessage("test", "Hello Abbas!");
        // readMessages();
        // readPosts();

    }

    // Handle the running of the server
    // run on thread to not interupt main program
    @Override
    public void run() {

        // System.out.println("Server listening on " + localNode.getIp() + ":" + localNode.getPORT() + "...");
        localNode.listener();

    }

}
