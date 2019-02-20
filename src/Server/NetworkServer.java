package Server;

import Data.DataMessage;

import java.io.IOException;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class NetworkServer implements Runnable {

    private Queue<Serializable> dataMessageQueue;
    private ServerSocket serverSocket;
    private AtomicBoolean isActive = new AtomicBoolean();
    private List<Connection> connectionList;
    private ServerSwitch serverSwitch;
    //private List<ChatRoom> chatRooms;

    public NetworkServer() {
        connectionList = Collections.synchronizedList(new ArrayList<Connection>());
        dataMessageQueue = new ConcurrentLinkedQueue<>();
        serverSwitch = new ServerSwitch(this);
        try {
            serverSocket = new ServerSocket(3000);
        } catch (IOException e) {
            e.printStackTrace();
        }
        setActive(true);
        Thread consumerThread = new Thread(this::consume);
        consumerThread.setDaemon(true);
        consumerThread.start();
    }

    public synchronized void sendToAll(Object o) {
        Iterator<Connection> connections = connectionList.iterator();
        while(connections.hasNext()){
            Connection c = connections.next();
            if (c.isActive()) {
                c.sendToClient(o);
            } else {
                connections.remove();
            }
        }
    }

    private void addConnection(Socket socket){
        connectionList.add(new Connection(this, socket));
    }

    public void addMessage(Object o){
        dataMessageQueue.add((Serializable) o);
    }

    public boolean isActive() {
        return isActive.get();
    }

    public void setActive(boolean active){
        isActive.set(active);
    }

    private void consume(){
        while (isActive()){
                Object o = dataMessageQueue.poll();
                if(o instanceof DataMessage) serverSwitch.switchDataMessage((DataMessage) o);
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void run() {
            while (isActive()) {
                try {
                    Socket s = serverSocket.accept();
                    addConnection(s);
                    Thread.sleep(1);
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
    }
}
