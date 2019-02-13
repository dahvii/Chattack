package Server;

public class Server {
    public NetworkServer ns;

    public Server(){
        ns = new NetworkServer();
        new Thread(ns).start();
    }

    public static void main(String[] args){
        new Server();
        System.out.println("Server running");
    }

}
