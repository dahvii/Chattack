package Server;

import Data.DataMessage;
import Data.Message;

import java.io.IOException;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

public class NetworkServer implements Runnable {

    private ServerSocket serverSocket;
    private AtomicBoolean isActive = new AtomicBoolean();
    private Queue<Serializable> dataMessageQueue;
    private Queue<Socket> socketQueue;
    private List<Connection> connectionList;
    private ServerSwitch serverSwitch;
    public final static String[] roomNames = new String[]{"main", "ninjas", "memes", "gaming", "horses"};
    private Queue<Connection> removeQueue;

    public NetworkServer() {
        connectionList = Collections.synchronizedList(new ArrayList<>());
        dataMessageQueue = new ConcurrentLinkedQueue<>();
        removeQueue = new ConcurrentLinkedQueue<>();
        socketQueue = new ConcurrentLinkedQueue<>();
        serverSwitch = new ServerSwitch(this);

        for(String room:roomNames){
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
        Thread destroyConnectionsThread = new Thread(this::destroyConnections);
        consumerMessagesThread.setDaemon(true);
        consumerMessagesThread.start();
        consumeSocketsThread.setDaemon(true);
        consumeSocketsThread.start();
        destroyConnectionsThread.setDaemon(true);
        destroyConnectionsThread.start();
    }

    public synchronized void sendToAll(Object o) {
            new Thread(()-> getConnectionStream().forEach(connection -> {
                if (connection.isActive()) {
                    if(!connection.getName().contains("TEST")) connection.addToSendQueue(o);
                } else {
                    addToRemoveQueue(connection);
                }
            })).start();
    }

    private void addConnection(){
        Socket s = socketQueue.poll();
        if(s!=null) {
            Connection c = new Connection(serverSwitch, s);
            if(c.isLoggedIn()) {
                getConnectionList().add(c);
                System.out.println("Connection added: " + c.getName() + " NEW SIZE: " + getConnectionList().size());
            }
        }
    }

    public void roomSwitch(String user, String newRoom){
        getConnectionStream().parallel().forEach(connection -> {
            if (connection.isActive()) {
                if(connection.getName().equals(user)){
                    String oldRoom = connection.getActiveRoom();
                    connection.setActiveRoom(newRoom);
                    sendToAll(new DataMessage(5, new Message(newRoom, null, user, oldRoom)));
                }
            } else addToRemoveQueue(connection);
        });
    }

    public boolean userOnline(String name){
        AtomicBoolean found = new AtomicBoolean(false);
        getConnectionStream().parallel().forEach(connection -> {
            if (connection.isActive()) {
                if(connection.getName().equals(name)) found.set(true);
            } else {
                addToRemoveQueue(connection);
            }
        });
        return found.get();
    }

    public DataMessage getOnlineUsers(String roomName) {
        AtomicReference<String> userString = new AtomicReference<>("");;
        getConnectionStream().parallel().forEach(connection -> {
            if (connection.isActive()) {
                if (connection.getActiveRoom().equals(roomName))
                    userString.set(userString.get() + connection.getName() + ",");
            } else {
                addToRemoveQueue(connection);
            }
        });
        if (userString.get().length() > 0)
            userString.set(userString.get().substring(0, userString.get().length() - 1));
        return new DataMessage(4, new Message(userString.get(), LocalDateTime.now(), null, roomName));
    }


    private synchronized void addToRemoveQueue(Connection connection){
        String name;
        if(!(name = connection.getAndSetName("")).equals("")){
            sendToAll(new DataMessage(5, new Message(null, null, name, connection.getActiveRoom())));
            System.out.println("Connection queued for destruction: " + name);
            removeQueue.add(connection);
        }
    }

    private void destroyConnections(){
        while(isActive()){
            try {
                Connection c = removeQueue.poll();
                if(c != null){
                    if(getConnectionList().remove(c)){
                        System.out.println("Connection destroyed - NEW SIZE:"+ getConnectionList().size());
                    }
                }
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } {

            }
        }
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

    private Stream<Connection> getConnectionStream(){
        return new ArrayList<>(getConnectionList()).stream();
    }

    public synchronized List<Connection> getConnectionList() {
        return connectionList;
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
}
