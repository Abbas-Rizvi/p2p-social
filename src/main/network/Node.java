package network;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.security.PublicKey;
import java.util.Iterator;

import backend.blockchain.Blockchain;
import backend.crypt.KeyGen;

public class Node implements Serializable {

    private static final long serialVersionUID = 123456789L;

    // constant for port
    private final int PORT = 5687;

    private String username;
    private PublicKey pubKey;
    private String pubKeyStr;
    private String ip;

    Selector selector;

    public Node(String username, String pubKeyStr, String ip) {

        // store variables
        this.username = username;
        this.ip = ip;
        this.pubKeyStr = pubKeyStr;

    }

    // create node with ip
    public Node(String ip){
        this.ip = ip;
    }


    // get the public key
    public PublicKey getPublicKey() {

        // decode public key
        KeyGen KeyDecode = new KeyGen();
        pubKey = KeyDecode.convertPublicKey(pubKeyStr);

        return pubKey;
    }

    // listen for connections, handle in non blocking manner
    public void listener() {

        try {

            // create selector for connecting hosts
            Selector selector = Selector.open();

            ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.socket().bind(new InetSocketAddress(PORT));
            serverSocketChannel.configureBlocking(false);

            // OP_ACCEPT is for when server accepts connection from client
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

            System.out.println("Server started on port " + PORT);

            // receive cconnections
            while (true) {

                // ignore empty selector
                if (selector.select() == 0) {
                    continue;
                }

                Iterator<SelectionKey> keyIterator = selector.selectedKeys().iterator();

                // queue for incoming requests
                while (keyIterator.hasNext()) {
                    // remove from queue
                    SelectionKey key = keyIterator.next();
                    keyIterator.remove();

                    if (key.isAcceptable()) {
                        // handle connect req
                        handleAccept(key, selector);
                    } else if (key.isReadable()) {
                        // handle read req
                        handleRead(key);
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    // handle accept of new connections
    // pass the currently stored blockchain
    private static void handleAccept(SelectionKey key, Selector selector) throws IOException {

        ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
        SocketChannel socketChannel = serverSocketChannel.accept();
        socketChannel.configureBlocking(false);
        socketChannel.register(selector, SelectionKey.OP_READ);
        System.out.println("Connection accepted from: " + socketChannel.getRemoteAddress());

        // utilize file sender to send block chain and node list
        FileSender fileSender = new FileSender();
        fileSender.sendAllAssets(socketChannel);

    }

    // handle send requests from connection
    // parse message type
    private static void handleRead(SelectionKey key) throws IOException {
        SocketChannel socketChannel = (SocketChannel) key.channel();
        ByteBuffer buffer = ByteBuffer.allocate(1024);

        int bytesRead = socketChannel.read(buffer);

        if (bytesRead == -1) {
            // Connection closed by client
            System.out.println("Connection closed by: " + socketChannel.getRemoteAddress());
            socketChannel.close();
            key.cancel();
            return;
        }

        if (bytesRead > 0) {

            // clear buffer
            buffer.flip();
            byte[] data = new byte[buffer.limit()];
            buffer.get(data);
            System.out.println("Received message from " + socketChannel.getRemoteAddress());
            decodeMessage(data, socketChannel);
        }
    }

    private static void decodeMessage(byte[] data, SocketChannel socketChannel) {

        SockMessage msg = null;
        // Convert input read from socket to object
        try {

            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(data);
            ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);

            Object inputMessage = objectInputStream.readObject();

            if (inputMessage instanceof SockMessage) {
                msg = (SockMessage) inputMessage;
            }

        } catch (IOException | ClassNotFoundException e) {

            e.printStackTrace();

        }

        // if message passed was blockchain
        if (msg.getType().equalsIgnoreCase("BLOCKCHAIN")) {

            // send to blockchain to handle conflicts and merge
            Blockchain blockchain = new Blockchain();
            blockchain.manageConflicts(blockchain.deserialize(msg.getFile()));

        } else if (msg.getType().equalsIgnoreCase("NODELIST")){

            // send node list to database to merge
            PeersDatabase db = new PeersDatabase();
            db.mergeDatabase(msg.getFile());





        } else if (msg.getType().equalsIgnoreCase("HANDSHAKE")){


            // indicates that the node is joining the network
            // send all assets back

            FileSender fileSender = new FileSender();

            fileSender.sendBlockChain(socketChannel);
            fileSender.sendNodeList(socketChannel);


        }

    }

    public PublicKey getPubKey() {
        return pubKey;
    }

    public String getPubKeyStr() {
        return pubKeyStr;
    }

    public int getPORT() {
        return PORT;
    }

    public String getIp() {
        return ip;
    }

    public String getUsername() {
        return username;
    }

}
