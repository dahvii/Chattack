import Client.Client;
import Server.Server;

public class Main {

    public static void main(String[] args) {
        String[] msg = new String[]{"0","HEJ","FRÅN PETER"};
        String[] msg2 = new String[]{"0","HEJ","FRÅN KATE"};
        String[] msg3 = new String[]{"0","HEJ","FRÅN ERIC"};
        String[] msg4 = new String[]{"0","HEJ","FRÅN ELLIOT"};
        String[] msg5 = new String[]{"0","HEJ","FRÅN JOHAN"};

        Server server  = new Server();

        Client c = new Client("Peter");
        c.connectToServer();
        Client c2 = new Client("Kate");
        c2.connectToServer();
        Client c3 = new Client("Eric");
        c3.connectToServer();
        Client c4 = new Client("Elliot");
        c4.connectToServer();
        Client c5 = new Client("Johan");
        c5.connectToServer();

        c.sendToServer(msg);
        c2.sendToServer(msg2);
        c3.sendToServer(msg3);
        c4.sendToServer(msg4);
        c5.sendToServer(msg5);
    }
}
