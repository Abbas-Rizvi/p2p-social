package backend.blockchain;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.util.ArrayList;

import backend.crypt.KeyGen;
import network.NTPTimeService;
import network.Node;
import network.PeersDatabase;

public class Blockchain implements Serializable {

    private static final long serialVersionUID = 123456789L;

    ArrayList<Block> blockchain;
    PeersDatabase db = new PeersDatabase();
    KeyGen key = new KeyGen();
    NTPTimeService timeService = new NTPTimeService();

    // ##################
    // Constructor
    // ##################
    public Blockchain() {

        // file path for storage
        String filePath = "./data/blockchain";

        // check if chain exists
        File storeBlockchain = new File(filePath);

        // if nothing is stored create a new chain (with genesis block)
        if (!storeBlockchain.exists()) {

            blockchain = new ArrayList<Block>();

            // genesis header
            BlockHeader genHeader = new BlockHeader(timeService.getNTPDate().getTime(), "", "GEN",
                    db.lookupNameByPublicKey(key.getPublicKeyStr()));
            // create genesis block
            appendBlock(new Post("", "Genesis Block", genHeader, key.getPrivatKey()));

        } else {

            this.blockchain = deserialize(readChain(storeBlockchain)).getBlockchain();
        }

    }

    // ##################
    // Blockchain Functions
    // ##################

    private ArrayList<Block> getBlockchain() {
        return blockchain;
    }

    // update chain with new block
    public void appendBlock(Block block) {

        if (validAddition(block))
            blockchain.add(block);
        else
            System.out.println("invalid addition!");

        // update chain
        storeChain(serialize());

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

    // check if a block addition is valid
    public boolean validAddition(Block newBlock) {

        // base case for genesis block
        if (newBlock instanceof Post && newBlock.getData().equals("Genesis Block"))
            return true;

        Block previousBlock = blockchain.get(blockchain.size() - 1);

        boolean validPrev = false;
        boolean validCur = false;

        // validate current block signature from known peers
        for (Node node : db.readAllNodes()) {

            if (newBlock.validSignature(key.convertPublicKey(node.getPubKeyStr()))) {
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

            if (previousBlock.validSignature(key.convertPublicKey(node.getPubKeyStr()))) {
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

    // ##################
    // IO Functions
    // ##################

    // read blockchain from byte[]
    public Blockchain deserialize(byte[] chainBytes) {

        try {
            ByteArrayInputStream byteArrayIn = new ByteArrayInputStream(chainBytes);
            ObjectInputStream ois = new ObjectInputStream(byteArrayIn);

            // cast input to object
            Object obj = ois.readObject();

            if (obj instanceof Blockchain) {
                return (Blockchain) obj;
            } else {
                return null;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    // convert blockchain to a byte[]
    public byte[] serialize() {

        try {

            // create output streams
            ByteArrayOutputStream byteArrayOut = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(byteArrayOut);

            // Write the object to the byte array stream
            oos.writeObject(this);

            // get byte array from the output stream
            byte[] byteArray = byteArrayOut.toByteArray();

            return byteArray;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    // store blockchain
    public void storeChain(byte[] byteArray) {

        // file path for storage
        String filePath = "./data/blockchain";

        // write byte array to file
        try {
            FileOutputStream fos = new FileOutputStream(filePath);
            fos.write(byteArray);
            System.out.println("blockchain saved");
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    // read from stored chain
    public byte[] readChain(File inputFile) {

        try {

            byte[] fileContent = Files.readAllBytes(inputFile.toPath());
            return fileContent;

        } catch (IOException e) {

            e.printStackTrace();
        }

        return null;
    }



}
