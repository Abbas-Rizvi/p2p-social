package network;

import java.util.ArrayList;

import backend.crypt.RSAKeys;

public class Server extends Thread {

    KeyDBHelper db = new KeyDBHelper();

    // ArrayList<
    static RSAKeys keys = new RSAKeys();

    static String key = keys.getPublicKeyStr();

    static Node node = new Node("jim", key, "127.0.0.1");

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

        System.out.println("Execution Started");


        Server server = new Server();
        Thread nodeListen = new Thread(server);

        nodeListen.start();


        System.out.println("Code is running ");


        while(true){

            System.out.println("run");

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
