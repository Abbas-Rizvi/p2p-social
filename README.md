# P2P Social

P2P Social is a distributed social networking system built using peer-to-peer networks and blockchain.

## Description

Peer-to-Peer (P2P) Social is a social network designed to be distributed across different devices. 
Through the use of blockchain technology, the social network is able to effectively distribute content 
across a network in a scalable, decentralized manner ensuring that all content posted by users is secure, 
immutable and free of any governing body. The network offers many advantages to users including the 
use of asymmetric encryption technology to validate the integrity of all posts, a secure encrypted 
private messaging system which facilitates true end-to-end encryption and methods to ensure the 
availability of the network should any singular node become unavailable.

Administrators of the network are also able to extend the network with new functionalities 
encompassed as blocks for users using the underlying blockchain, allowing new versions of the social 
network software to be released and placed in service without requiring the creation of a separate 
network. The network provides strong decoupling from user-interfaces, allowing developers and users 
to create and implement novel user-interfaces on platforms such as mobile, desktop and command line.


### Core Features

- Data Privacy & Security
- Decentralized
- Extensible
- Scalable

## Architecture & Design

### Blockchain

P2P Social utlizes a versatile blockchain architecture for managing the creation of posts and messages within the application.
An interface is provided for the creation of blocks which can be implemented within the network.

![image](https://github.com/Abbas-Rizvi/p2p-social/assets/73917749/4a61b78f-7135-47e3-a7d5-54f6e5aebb59)

---

New blocks being published to the blockchain have a unique hash generated which is then signed using the key of the node.
This signature and hash is then verified and evaluated by other nodes for acceptance into the blockchain.


![image](https://github.com/Abbas-Rizvi/p2p-social/assets/73917749/fb3186e4-e6dc-406c-81a4-2222aa58ab60)

---

### Networking 

Networking within P2P Social is done using Java NIO TCP connections.
Upon starting the daemon, each node is assigned a listening socket for handling incoming connections.
This socket is then used by other nodes for sending updates regarding new nodes, blockchain changes, etc.

![image](https://github.com/Abbas-Rizvi/p2p-social/assets/73917749/7a579445-c6c0-49f6-8ccd-1af952dbcce1)


## Running Application

Clone the gihub repo

`$ git clone https://github.com/Abbas-Rizvi/p2p-social.git`

Create dataset folder

`mkdir data`

Compile java classes

`javac -cp ".\lib\*" .\src\main\backend\blockchain\*.java .\src\main\backend\crypt\*.java .\src\main\network\*.java`

Run Server UI java class

`java -cp ".\lib\*;.\src\main" network.ServerUI`

---

## Demo
[Watch the demo](demo/p2pMerged.mp4)








