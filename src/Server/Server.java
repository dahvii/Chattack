package Server;

import java.io.FileNotFoundException;

public class Server {
    public NetworkServer ns;
    public Store store;

    public Server() throws FileNotFoundException {
        ns = new NetworkServer();
        new Thread(ns).start();
        store = new Store();
    }

    public static void main(String[] args) throws FileNotFoundException {
        new Server();
        System.out.println("Server running");
    }

}
