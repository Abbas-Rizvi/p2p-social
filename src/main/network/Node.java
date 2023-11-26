package network;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.security.PublicKey;
import java.security.interfaces.RSAKey;
import java.util.Iterator;

import backend.crypt.KeyGen;

public class Node {

    // constant for port
    private final int PORT = 5687;

    private String username;
    private PublicKey pubKey;
    private String ip;
    private Socket socket;

    Selector selector;

    public Node(String username, String pubKeyStr, String ip) {

        // store variables
        this.username = username;
        this.ip = ip;

        // decode public key
        KeyGen KeyDecode = new KeyGen();
        pubKey = KeyDecode.convertPublicKey(pubKeyStr);

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
            // TODO Auto-generated catch block
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
            buffer.flip();
            byte[] data = new byte[buffer.limit()];
            buffer.get(data);
            System.out.println("Received message from " + socketChannel.getRemoteAddress());
            decodeMessage(data);
        }
    }

    private static void decodeMessage(byte[] data) {

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

            // TODO Auto-generated catch block
            e.printStackTrace();

        }


        // handle message using appropriate method
        switch (msg.getType()) {

            case "NODELIST":

                break;
            case "BLOCKCHAIN":
                
                break;

            default:
                break;
        }

        // if message passed was blockchain
        if (msg.getType().equalsIgnoreCase("BLOCKCHAIN")){

            //TODO
        }



        
    }

    public PublicKey getPubKey() {
        return pubKey;
    }

    public void setPubKey(PublicKey pubKey) {
        this.pubKey = pubKey;
    }

}
