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

public class NetworkServer implements Runnable {

    private ServerSocket serverSocket;
    private AtomicBoolean isActive = new AtomicBoolean();
    private Queue<Serializable> dataMessageQueue;
    private Queue<Socket> socketQueue;
    private List<Connection> connectionList;
    private ServerSwitch serverSwitch;
    public final static String[] roomNames = new String[]{"main", "ninjas", "memes", "gaming", "horses"};

    public NetworkServer() {
        connectionList = Collections.synchronizedList(new ArrayList<>());
        dataMessageQueue = new ConcurrentLinkedQueue<>();
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
        consumerMessagesThread.setDaemon(true);
        consumerMessagesThread.start();
        consumeSocketsThread.setDaemon(true);
        consumeSocketsThread.start();
    }

    public synchronized void sendToAll(Object o) {
        Spliterator<Connection> connections = getConnectionList().spliterator();
        while(connections.tryAdvance(c ->{
            if (c.isActive()) {
                c.sendToClient(o);
            }
        }));
    }

    public void roomSwitch(String user, String newRoom){
        Iterator<Connection> connections = getConnectionList().iterator();
        while (connections.hasNext()){
            Connection c = connections.next();
            if (c.isActive()) {
                if(c.getName().equals(user)){
                    String oldRoom = c.getActiveRoom();
                    c.setActiveRoom(newRoom);
                    sendToAll(new DataMessage(5, new Message(newRoom, null, user, oldRoom)));
                }
            } else {
                removeConnection(connections, c);
            }

        }
    }

    private void addConnection(){
        Socket s = socketQueue.poll();
        if(s!=null) {
            Connection c = new Connection(serverSwitch, s);
            if(c.isActive()) {
                getConnectionList().add(c);
            }
        }
    }

    public boolean userOnline(String name){
        Iterator<Connection> connections = getConnectionList().iterator();
        while (connections.hasNext()){
            Connection c = connections.next();
            if (c.isActive()) {
                if(c.getName().equals(name)) return true;
            } else {
                removeConnection(connections, c);
            }
        }
        return false;
    }

    public DataMessage getOnlineUsers(String roomName) {
        String usersString = "";
        Iterator<Connection> connections = getConnectionList().iterator();
        while (connections.hasNext()) {
            Connection c = connections.next();
            if (c.isActive()) {
                if (c.getActiveRoom().equals(roomName)) usersString += c.getName() + ",";
            } else {
                removeConnection(connections, c);
            }
        }
        if (usersString.length() > 0) usersString = usersString.substring(0, usersString.length() - 1);

        return new DataMessage(4, new Message(usersString, LocalDateTime.now(), null, roomName));
    }

    public synchronized void removeConnection(Iterator<Connection> connections, Connection c){
            try {
                if (getConnectionList().contains(c)){
                    if(!c.getName().equals("")){
                        if(connections != null) connections.remove();
                        else getConnectionList().remove(c);

                        System.out.println("Connection removed: " + c.getName());
                        sendToAll(new DataMessage(5, new Message(null, null, c.getName(), c.getActiveRoom())));
                        c.setName("");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
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
