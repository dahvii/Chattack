package Server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;

public class Connection implements Runnable{
    private String name;
    private NetworkServer networkServer;
    private Socket socket;
    private AtomicBoolean isActive;
    private ObjectOutputStream objectOutputStream;


    public Connection(NetworkServer networkServer, Socket socket){
        this.networkServer =  networkServer;
        this.socket = socket;
        this.isActive = new AtomicBoolean();
        init();
    }

    private void init(){
        try {
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

    public void sendToClient(Object o){
        try {
            if(isActive()) objectOutputStream.writeObject(o);
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
                networkServer.addMessage(o);
            } catch (Exception e) {
                closeConnection();
                e.printStackTrace();
            }
        }
    }

    private void closeConnection(){
        try {
            setActive(false);
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
