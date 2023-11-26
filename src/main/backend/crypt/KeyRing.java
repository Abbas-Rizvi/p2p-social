package backend.crypt;

import java.security.PublicKey;
import java.util.List;

import network.PeersDatabase;
import network.Node;

public class KeyRing {

    PeersDatabase db = new PeersDatabase();
    List<Node> storedKeys = db.readAllNodes();

    public KeyRing() {

        }



    }

}
