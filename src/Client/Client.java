package Client;

public class Client {
    public NetworkClient networkClient;
    public String name;

    public Client(String name){
        this.name = name;
//        networkClient = new NetworkClient(this);
    }

    public void connectToServer(){
        networkClient.init();
    }

    public void sendToServer(Object o){
        networkClient.sendToServer(o);
    }

    public void disconnectServer(){
        networkClient.setActive(false);
    }

    public void print(Object o){
        System.out.println(name + ": " +((String[]) o)[2]);
    }
}
