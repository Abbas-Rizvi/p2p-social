package network;

import java.io.ByteArrayInputStream;
import java.io.EOFException;
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
    public Node(String ip) {
        this.ip = ip;
    }

    // get the public key
    public PublicKey getPublicKey() {

        // decode public key
        KeyGen KeyDecode = new KeyGen();
        pubKey = KeyDecode.convertPublicKey(pubKeyStr);

        return pubKey;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////
    // listen for connections, handle in non-blocking manner
    public void listener() {

        try {

            // create selector for connecting hosts
            Selector selector = Selector.open();

            ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.socket().bind(new InetSocketAddress(PORT));
            serverSocketChannel.configureBlocking(false);

            // OP_ACCEPT is for when the server accepts connection from the client
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

            // System.out.println("Server started on port " + PORT);

            // receive connections
            while (true) {

                // ignore empty selector
                if (selector.select() == 0) {
                    continue;
                }

                Iterator<SelectionKey> keyIterator = selector.selectedKeys().iterator();

                // queue for incoming requests
                while (keyIterator.hasNext()) {
                    // remove from the queue
                    SelectionKey key = keyIterator.next();
                    keyIterator.remove();

                    if (key.isAcceptable()) {
                        // handle connect request
                        handleAccept(key, selector);
                    } else if (key.isReadable()) {
                        // handle read request
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
        System.out.println("### Connection accepted from: " + socketChannel.getRemoteAddress());

        // // utilize file sender to send the blockchain and node list
        // FileSender fileSender = new FileSender();
        // fileSender.sendAllAssets(socketChannel);

    }

    // handle send requests from connection
    // parse message type
    private static void handleRead(SelectionKey key) throws IOException {
        SocketChannel socketChannel = (SocketChannel) key.channel();
        int bufferSize = 1024 * 1024 * 1024; // 1 KB, adjust as needed
        ByteBuffer buffer = ByteBuffer.allocate(bufferSize);

        // Track the total number of bytes read
        int totalBytesRead = 0;

        while (totalBytesRead < bufferSize) {
            int bytesRead = socketChannel.read(buffer);

            if (bytesRead == -1) {
                // Connection closed by the client
                System.out.println("Connection closed by: " + socketChannel.getRemoteAddress());
                socketChannel.close();
                key.cancel();
                return;
            }

            if (bytesRead > 0) {
                totalBytesRead += bytesRead;
            } else if (bytesRead == 0) {
                // No more data to read
                break;
            }
        }

        // Reset the position and limit to read the entire buffer
        buffer.flip();

        // Check if any data was received
        if (buffer.remaining() > 0) {
            byte[] data = new byte[buffer.remaining()];
            buffer.get(data);

            // System.out.println("Received message from " + socketChannel.getRemoteAddress());
            decodeMessage(data, socketChannel);
        }
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    public static String decodeMessage(byte[] data, SocketChannel socketChannel) {

        SockMessage msg = null;

        try (ByteArrayInputStream byteStream = new ByteArrayInputStream(data);
             ObjectInputStream objectStream = new ObjectInputStream(byteStream)) {
    
            Object obj = objectStream.readObject();
            if (obj instanceof SockMessage) {
                msg = (SockMessage) obj;
            }
    
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    
        if (msg != null) {
            try {
                System.out.println("Received " + msg.getType() + " From " + socketChannel.getRemoteAddress());
            } catch (IOException e) {
                e.printStackTrace();
            }

            // if the message passed was blockchain
            if (msg.getType().equalsIgnoreCase("BLOCKCHAIN")) {
                // send to blockchain to handle conflicts and merge
                Blockchain blockchain = new Blockchain();
                blockchain.manageConflicts(blockchain.deserialize(msg.getFile()));
                return "BLOCKCHAIN";

            } else if (msg.getType().equalsIgnoreCase("NODELIST")) {
                // send the node list to the database to merge
                PeersDatabase db = new PeersDatabase();
                db.mergeDatabase(msg.getFile());
                return "NODELIST";

            } else if (msg.getType().equalsIgnoreCase("HANDSHAKE")) {
                // indicates that the node is joining the network
                // send all assets back
                FileSender fileSender = new FileSender();
                fileSender.sendAllAssets(socketChannel);
                return "HANDSHAKE";

            } else if (msg.getType().equalsIgnoreCase("HANDSHAKE-RECV")) {
                // respond to a node joining the network sending own files
                FileSender fileSender = new FileSender();
                fileSender.sendBlockChain(socketChannel);
                fileSender.sendNodeList(socketChannel);
                return "HANDSHAKE-RECV";
            }

        }
        return null;
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
