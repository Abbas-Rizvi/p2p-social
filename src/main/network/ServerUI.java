package network;

import java.util.Scanner;

public class ServerUI {

    public static void main(String[] args) {
        Server server = new Server();
        server.setup(); // Call the setup method

        Thread nodeListen = new Thread(server);
        nodeListen.start();

        Scanner scanner = new Scanner(System.in);

        while (true) {
            displayMenu();
            int choice = scanner.nextInt();
            scanner.nextLine(); // consume the newline

            switch (choice) {
                case 1:
                    System.out.println("Enter topic:");
                    String topic = scanner.nextLine();
                    System.out.println("Enter message:");
                    String message = scanner.nextLine();
                    server.createPost(topic, message);
                    break;
                case 2:
                    System.out.println("Enter recipient:");
                    String recipient = scanner.nextLine();
                    System.out.println("Enter message:");
                    String privateMessage = scanner.nextLine();
                    server.createMessage(recipient, privateMessage);
                    break;
                case 3:
                    server.readMessages();
                    break;
                case 4:
                    server.readPosts();
                    break;
                case 5:
                    server.listPeers();
                    break;
                case 6:
                    System.out.println("Enter recipient:");
                    String ip = scanner.nextLine();
                    server.connectNode(ip);
                    break;

                case 7:
                    System.out.println("Exiting...");
                    System.exit(0);
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private static void displayMenu() {
        System.out.println("\n----- Menu -----");
        System.out.println("1. Create Post");
        System.out.println("2. Create Private Message");
        System.out.println("3. Read Messages");
        System.out.println("4. Read Posts");
        System.out.println("5. List Peers");
        System.out.println("6. Connect IP");
        System.out.println("7. Exit");
        System.out.print("Enter your choice: ");
    }
}
