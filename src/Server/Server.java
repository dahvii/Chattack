package Server;

public class Server {
    public static void main(String[] args) {
        PasswordCheck.getInstance();
        NetworkServer ns = new NetworkServer();
        new Thread(ns).start();
        System.out.println("Server running");
    }

}
