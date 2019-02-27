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

    private ServerSocket serverSocket;
    private AtomicBoolean isActive = new AtomicBoolean();
    private Queue<Serializable> dataMessageQueue;
    private Queue<Socket> socketQueue;
    private List<Connection> connectionList;
    private Map<String, List<String>> roomUserMap;
    private ServerSwitch serverSwitch;
    private final String[] roomNames = new String[]{"main", "ninjas", "memes", "gaming", "horses"};

    public NetworkServer() {
        connectionList = Collections.synchronizedList(new ArrayList<>());
        roomUserMap = Collections.synchronizedMap(new HashMap<>());
        dataMessageQueue = new ConcurrentLinkedQueue<>();
        socketQueue = new ConcurrentLinkedQueue<>();
        serverSwitch = new ServerSwitch(this);

        for(String room:roomNames){
            roomUserMap.put(room, new ArrayList<>());
            DataHandler.getInstance().loadRoomMessages(room);
        }

        try {
            serverSocket = new ServerSocket(3000);
        } catch (IOException e) {
            e.printStackTrace();
        }

        setActive(true);

        Thread consumerMessagesThread = new Thread(this::consumeMessages);
        Thread consumeSocketsThread = new Thread(this::consumeSockets);
        consumerMessagesThread.setDaemon(true);
        consumerMessagesThread.start();
        consumeSocketsThread.setDaemon(true);
        consumeSocketsThread.start();
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

    public void roomSwitch(String user, String newRoom){
        connectionList.forEach(c -> {
            if(c.getName().equals(user)){
                c.setActiveRoom(newRoom);
            }
        });
    }

    private void addConnection(){
        Socket s = socketQueue.poll();
        if(s!=null) {
            Connection c = new Connection(serverSwitch, s);
            if(c.isActive()) {
                connectionList.add(c);
                roomSwitch(c.getName(), "main");
            }
        }
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

    private void consumeSockets(){
        while (isActive()){
            if(!socketQueue.isEmpty()) new Thread(this::addConnection).start();
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void consumeMessages(){
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
                    socketQueue.add(s);
                    Thread.sleep(1);
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
    }
}
