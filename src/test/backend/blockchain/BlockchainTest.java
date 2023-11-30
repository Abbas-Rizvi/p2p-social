package test.backend.blockchain;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import backend.blockchain.Block;
import backend.blockchain.Blockchain;
import backend.blockchain.Post;

public class BlockchainTest {
    @Test
    public void testReadChain() {

        Blockchain blockchain = new Blockchain();
        int count = 0;

        for (Block block : blockchain.getBlockchain()){

            if (block instanceof Post){
                System.out.println(block.getData() + block.getHash());
                
                count ++;

            }
        }

        System.out.println(count);
        assertEquals(count, blockchain.chainLength());

    }
}
