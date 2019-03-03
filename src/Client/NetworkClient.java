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
            Object o = objectInputStream.readObject();
            if(o != null) return o;
        } catch (Exception e) {
            e.printStackTrace();
            closeConnection(e.getMessage());
        }
        return null;
    }

    public void sendToServer(Object o){
        try {
            objectOutputStream.writeObject(o);
        } catch (Exception e) {
            e.printStackTrace();
            closeConnection(e.getMessage());
        }
    }

    public void run() {
        while (isActive()){
            try {
                Object o =  receiveObject();
                if (o !=null) {
                    addMessage(o);
                }
                Thread.sleep(1);
            } catch (Exception e) {
                e.printStackTrace();
                closeConnection(e.getMessage());
            }
        }
        closeConnection(null);
    }

    private void closeConnection(String exception){
        if(isActive()){
            try {
                setActive(false);
                objectInputStream.close();
                objectOutputStream.close();
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("Connection closed " + exception);
        }
    }

    private void addMessage(Object o){
        getMessageQueue().add(o);
    }

    public synchronized Queue<Object> getMessageQueue(){
        return messageQueue;
    }

    private synchronized AtomicBoolean getAtomicActive(){
        return isActive;
    }

    public boolean isActive() {
        return getAtomicActive().get();
    }

    public void setActive(boolean isActive) {
        getAtomicActive().set(isActive);
    }
}
