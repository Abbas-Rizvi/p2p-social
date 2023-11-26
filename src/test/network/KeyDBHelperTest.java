package test.network;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import network.PeersDatabase;
import network.Node;

public class KeyDBHelperTest {

    PeersDatabase db = new PeersDatabase();

    @Test
    public void testInsertRecord() {

        db.deleteRowsByName("Joe");

        int result = db.insertRecord("Joe", "21", "192.125.12.1");
        assertEquals(0, result);

        assertEquals("21", db.lookupPublicKeyByName("Joe"));


        db.deleteRowsByName("Joe");
    }

    @Test
    public void testReadAllNodes() {

        db.insertRecord("Joe", "21", "192.125.12.1");
        db.insertRecord("Tim", "21", "192.125.12.1");

        List<Node> allRecords = new ArrayList<>();

        allRecords = db.readAllNodes();

        for(Node node: allRecords){
            System.out.println(node.toString());
            // return "Node [username=" + username + ", pubKey=" + pubKey + ", ip=" + ip + "]";
        }
    }

}
