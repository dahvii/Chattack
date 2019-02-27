package Server;

public class Server {
    public static void main(String[] args) {
        NetworkServer ns = new NetworkServer();
        new Thread(ns).start();
        System.out.println("Server running");
    }

}
