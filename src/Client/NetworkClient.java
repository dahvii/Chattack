package Client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class NetworkClient {
    private static NetworkClient instance;
    private Queue<Object> messageQueue;
    private Socket socket;
    private AtomicBoolean isActive;
    private ObjectOutputStream objectOutputStream;
    private ObjectInputStream objectInputStream;

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
            messageQueue = new ConcurrentLinkedQueue<>();
            socket = new Socket("localhost", 3000);
            objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            objectInputStream = new ObjectInputStream(socket.getInputStream());
            socket.setTcpNoDelay(true);

        } catch (IOException e) {
            e.printStackTrace();
        }
        setActive(true);
        Thread listenerThread = new Thread(this::run);
        listenerThread.setDaemon(true);
        listenerThread.start();
    }

    public Object receiveObject(){
        try {
            return objectInputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void sendToServer(Object o){
        try {
            objectOutputStream.writeObject(o);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private synchronized void addMessage(Object o){
        messageQueue.add(o);
    }

    public synchronized Queue<Object> getMessageQueue(){
        return messageQueue;
    }

    public void run() {
        while (isActive()){
            try {
                Object o =  receiveObject();
                if (o !=null) {
                    addMessage(o);
                    System.out.println("From server: " + o);
                }
                Thread.sleep(1);
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
