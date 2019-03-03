package Server;

import Data.DataMessage;
import Data.Message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Arrays;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class Connection {
    private AtomicReference<String> name = new AtomicReference<>("");
    private AtomicReference<String> activeRoom = new AtomicReference<>("");
    private AtomicBoolean isActive = new AtomicBoolean(false);
    private AtomicBoolean loggedIn = new AtomicBoolean(false);
    private Queue<Object> sendQueue = new ConcurrentLinkedQueue<>();
    private ObjectOutputStream objectOutputStream;
    private ObjectInputStream objectInputStream;
    private Socket socket;
    private ServerSwitch serverSwitch;

    public Connection(ServerSwitch serverSwitch, Socket socket){
        this.serverSwitch =  serverSwitch;
        this.socket = socket;
        init();
    }

    private void init(){
        try {
            objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            objectInputStream = new ObjectInputStream(socket.getInputStream());
            setActive(true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(loginCheck()){
            Thread listenerThread = new Thread(this::inputThread);
            listenerThread.setDaemon(true);
            listenerThread.start();

            Thread sendThread = new Thread(this::outputThread);
            sendThread.setDaemon(true);
            sendThread.start();

            Arrays.stream(NetworkServer.roomNames).parallel().forEach(roomName -> {
                addToSendQueue(serverSwitch.getOnlineUsers(roomName));
                DataHandler.getInstance()
                        .getLatestMessages(roomName)
                        .map(message -> new DataMessage(0, message))
                        .forEach(this::addToSendQueue);
            });

            serverSwitch.switchDataMessage(
                    new DataMessage(5, new Message(getActiveRoom(), null, getName(), getActiveRoom())));
        }
    }

    private boolean loginCheck(){
        sendObject(new DataMessage(1, null));
        while (!isLoggedIn() && isActive()){
            DataMessage response = null;
            while (response == null && isActive()){
                response = (DataMessage) receiveObject();
            }
            if(isActive()){
                boolean passwordRegisterCheck = serverSwitch.switchLogin(response);

                if(!passwordRegisterCheck) {
                    sendObject(new DataMessage(2, null));

                } else if(passwordRegisterCheck && response.getCommando()==2) {
                    sendObject(new DataMessage(3, null));

                } else if(passwordRegisterCheck && response.getCommando()==3) {
                    setName(response.getMessage().getSender());
                    setActiveRoom("main");
                    sendObject(new DataMessage(3, null));
                    setLoggedIn(true);
                }
            }
        }
        return isLoggedIn();
    }

    private Object receiveObject(){
        try {
            return objectInputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            closeConnection(e.toString());
        }
        return null;
    }


    public void sendObject(Object o){
        try {
           objectOutputStream.writeObject(o);
        } catch (IOException e) {
            closeConnection(e.toString());
        }
    }

    private void outputThread() {
        while (isActive()){
            try {
                Object o = getSendQueue().poll();
                if(o != null) sendObject(o);
                Thread.sleep(1);
            } catch (Exception e) {
                closeConnection(e.toString());
            }
        }
    }

    private void inputThread() {
        while (isActive()){
            try {
                Object o = receiveObject();
                if(o != null) serverSwitch.addMessage(o);
                Thread.sleep(1);
            } catch (Exception e) {
                closeConnection(e.toString());
            }
        }
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
            System.out.println("Connection closed by client: " + exception);
        }
    }
    private synchronized AtomicBoolean getAtomicLoggedIn(){
        return loggedIn;
    }

    public boolean isLoggedIn(){
        return getAtomicLoggedIn().get();
    }

    private void setLoggedIn(boolean loggedIn) {
        getAtomicLoggedIn().set(loggedIn);
    }

    private synchronized AtomicBoolean getAtomicActive() {
        return isActive;
    }

    public boolean isActive() {
        return getAtomicActive().get();
    }

    public void setActive(boolean isActive) {
        getAtomicActive().set(isActive);
    }

    private synchronized AtomicReference<String> getAtomicName() {
        return name;
    }

    public String getName() {
        return getAtomicName().get();
    }

    public String getAndSetName(String name){
        return getAtomicName().getAndSet(name);
    }

    public void setName(String name) {
        getAtomicName().set(name);
    }

    private synchronized AtomicReference<String> getAtomicRoom() {
        return activeRoom;
    }

    public String getActiveRoom(){
        return getAtomicRoom().get();
    }

    public void setActiveRoom(String activeRoom) {
        getAtomicRoom().set(activeRoom);
    }

    private synchronized Queue<Object> getSendQueue() {
        return sendQueue;
    }

    public void addToSendQueue(Object o){
        getSendQueue().add(o);
    }
}
