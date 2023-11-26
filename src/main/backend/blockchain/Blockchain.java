package backend.blockchain;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import network.Node;
import network.PeersDatabase;

public class Blockchain {

    ArrayList<Block> blockchain;
    PeersDatabase db = new PeersDatabase();

    public Blockchain() {

        blockchain = new ArrayList<Block>();

    }

    // update chain with new block
    public void appendBlock(Block block) {

        blockchain.add(block);

    }

    // read blockchain from byte arr
    public void convertBlockChain(byte[] chainBytes) {

    }

    // store blockchain
    public void storeChain() {

        // file path for storage
        String filePath = "./data/blockchain";

        try {

            // create file and object output streams
            FileOutputStream file = new FileOutputStream(filePath);
            ObjectOutputStream oos = new ObjectOutputStream(file);

            // write chain to stream
            oos.writeObject(this);

            System.out.println("Blockchain has been saved");

        } catch (Exception e) {

            e.printStackTrace();
        }

    }

    // read from file
    public void readChain(File chainFile) {

        // file path for storage
        String filePath = "./data/blockchain";

        try {
            FileInputStream file = new FileInputStream(filePath);

            // Creates an ObjectOutputStream
            ObjectInputStream ois = new ObjectInputStream(file);

            this.blockchain = ((Blockchain) ois.readObject()).getChain();

        } catch (Exception e) {
            e.printStackTrace();
        }

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

    public boolean validAddition(){


            Block currentBlock = blockchain.get(blockchain.size() - 1);
            Block previousBlock = blockchain.get(blockchain.size() - 2);

            boolean validPrev = false;
            boolean validCur = false;


            // validate current block signature from known peers
            for (Node node : db.readAllNodes()){
            
                if (currentBlock.validSignature(node.getPubKey())){
                    validCur = true;
                    break;
                }
            }
            
            if (!validCur){
                System.out.println("New block does not have a valid signature!");
                return false;
            }


            // validate previous block signature from known peers
            for (Node node : db.readAllNodes()){
                
                if (previousBlock.validSignature(node.getPubKey())){
                    validPrev = true;
                    break;
                }

            }
            
            if (!validPrev){
                System.out.println("Previous block does not have a valid signature!");
                return true;
            }

            return true;
        }

}
