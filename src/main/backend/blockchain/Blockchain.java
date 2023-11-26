package backend.blockchain;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

import backend.crypt.KeyGen;
import network.NTPTimeService;
import network.Node;
import network.PeersDatabase;

public class Blockchain {

    ArrayList<Block> blockchain;
    PeersDatabase db = new PeersDatabase();
    KeyGen key = new KeyGen();
    NTPTimeService timeService = new NTPTimeService();

    public Blockchain() {

        // file path for storage
        String filePath = "./data/blockchain";

        // check if chain exists
        File storeBlockchain = new File(filePath);

        // if nothing is stored create a new chain (with genesis block)
        if (!storeBlockchain.exists()) {

            blockchain = new ArrayList<Block>();

            // genesis header
            BlockHeader genHeader = new BlockHeader(timeService.getNTPDate().getTime(), "", "POST",
                    db.lookupNameByPublicKey(key.getPublicKeyStr()));
            // create genesis block
            appendBlock(new Post("", "Genesis Block", genHeader, key.getPrivatKey()));

        } else {
            readChain(storeBlockchain);
        }

    }

    // update chain with new block
    public void appendBlock(Block block) {

        if (validAddition(block))
            blockchain.add(block);
        else
            System.out.println("invalid addition!");

        // update chain
        storeChain();

    }

    // // read blockchain from byte arr
    // public void byteArrToBlockchain(byte[] chainBytes) {

    //     byte[] byteArray = chainBytes;
    //     File outputFile = new File("path/to/your/output/file.txt");

    //     try (FileOutputStream fos = new FileOutputStream(outputFile)) {
    //         fos.write(byteArray);
    //         System.out.println("Byte array to file conversion successful.");
    //     } catch (IOException e) {
    //         e.printStackTrace();
    //     }

    // }

    // // get the blockchain file stored
    // public byte[] blockchainToByteArr() {

    //     // file path for storage
    //     String filePath = "./data/blockchain";

    //     File file = new File(filePath);

    //     try (FileInputStream fis = new FileInputStream(file)) {
    //         byte[] byteArray = new byte[(int) file.length()];
    //         fis.read(byteArray);

    //         return byteArray;
    //     } catch (Exception e) {
    //         e.printStackTrace();
    //     }

    //     return null;
    // }

    // store blockchain
    public byte[] storeChain() {

        // file path for storage
        String filePath = "./data/blockchain";

        try {

            // create file and object output streams
            FileOutputStream file = new FileOutputStream(filePath);
            ObjectOutputStream oos = new ObjectOutputStream(file);

            // write chain to stream
            byte[] byteRes = oos.writeObject(this);

            System.out.println("Blockchain has been saved");

            return file.
            oos.close(); 
        } catch (Exception e) {

            e.printStackTrace();
        }

    }

    // read from file
    public Blockchain readChain(byte[] chainBytes) {


        try {
            ByteArrayInputStream file = new ByteArrayInputStream(chainBytes);

            // Creates an ObjectOutputStream
            ObjectInputStream ois = new ObjectInputStream(file);

            return ((Blockchain) ois.readObject());

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;

    }

    // read from file
    public Blockchain readChain(File chainFile) {

        // file path for storage
        String filePath = "./data/blockchain";

        try {
            FileInputStream file = new FileInputStream(filePath);

            // Creates an ObjectOutputStream
            ObjectInputStream ois = new ObjectInputStream(file);

            return ((Blockchain) ois.readObject());

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;

    }

    //
    private ArrayList<Block> getChain() {
        return blockchain;
    }

    // use to select branch of chain to use
    public Blockchain manageConflicts(Blockchain proposedBlockchain) {

        // file path for storage
        String filePath = "./data/blockchain";

        // check if chain exists
        File storeBlockchain = new File(filePath);

        // if nothing is stored
        if (!storeBlockchain.exists()) {

            return proposedBlockchain;

        } else if (proposedBlockchain.chainLength() > blockchain.size()) {

            return proposedBlockchain;
        } else {
            return this;
        }
    }

    // get length of chain
    public int chainLength() {

        return blockchain.size();
    }

    public boolean validAddition(Block newBlock) {

        Block previousBlock = blockchain.get(blockchain.size() - 1);

        boolean validPrev = false;
        boolean validCur = false;

        // validate current block signature from known peers
        for (Node node : db.readAllNodes()) {

            if (newBlock.validSignature(node.getPubKey())) {
                validCur = true;
                break;
            }
        }

        if (!validCur) {
            System.out.println("New block does not have a valid signature!");
            return false;
        }

        // validate previous block signature from known peers
        for (Node node : db.readAllNodes()) {

            if (previousBlock.validSignature(node.getPubKey())) {
                validPrev = true;
                break;
            }

        }

        if (!validPrev) {
            System.out.println("Previous block does not have a valid signature!");
            return true;
        }

        return true;
    }

    // gets last hash from block on blockchain
    public String lastHash() {
        Block currentBlock = blockchain.get(blockchain.size() - 1);

        return currentBlock.getHash();

    }

}
