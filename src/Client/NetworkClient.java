package Client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;

public class NetworkClient implements Runnable {
    private static NetworkClient instance;
    private String name;
    private Socket socket;
    private AtomicBoolean isActive;
    private ObjectOutputStream objectOutputStream;
//    private Client client;
//
//    public NetworkClient(Client client) {
//        isActive = new AtomicBoolean();
//        this.client = client;
//        init();
//    }

    private NetworkClient(){
        isActive = new AtomicBoolean();
        init();
    }

    public static NetworkClient getInstance(){
        if (instance == null) instance = new NetworkClient();
        return instance;
    }


    public void init(){
        try {
            socket = new Socket("localhost", 3000);
            objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            socket.setTcpNoDelay(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        setActive(true);
        Thread listenerThread = new Thread(this);
        listenerThread.setDaemon(true);
        listenerThread.start();
    }

    public void sendToServer(Object o){
        try {
            objectOutputStream.writeObject(o);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void run() {
        ObjectInputStream objectInputStream  = null;
        try {
            objectInputStream = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        while (isActive()){
            try {
                Object o =  objectInputStream.readObject();
                if (o !=null) System.out.println(o);
            } catch (Exception e) {
                e.printStackTrace();
                setActive(false);
            }
        }

        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public boolean isActive() {
        return isActive.get();
    }

    public void setActive(boolean isActive) {
        this.isActive.set(isActive);
    }

}
