package Server;

public class Server {
    public NetworkServer ns;
    public Store store;

    public Server(){
        ns = new NetworkServer();
        new Thread(ns).start();
        store = new Store();
    }

    public static void main(String[] args){
        new Server();
        System.out.println("Server running");
    }

}
