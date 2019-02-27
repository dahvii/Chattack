package Server;

import Data.DataMessage;
import Data.Message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;

public class Connection {
    private String name;
    private String activeRoom;
    private Socket socket;
    private AtomicBoolean isActive;
    private ObjectOutputStream objectOutputStream;
    private ObjectInputStream objectInputStream;
    private ServerSwitch serverSwitch;


    public Connection(ServerSwitch serverSwitch, Socket socket){
        this.serverSwitch =  serverSwitch;
        this.socket = socket;
        isActive = new AtomicBoolean();
        activeRoom = "main";
        init();
    }

    private void init(){
        try {
            objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            objectInputStream = new ObjectInputStream(socket.getInputStream());
            socket.setTcpNoDelay(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        setActive(true);
        login();

        Thread listenerThread = new Thread(this::run);
        listenerThread.setDaemon(true);
        listenerThread.start();

        for(String roomName: NetworkServer.roomNames){
            sendToClient(serverSwitch.getOnlineUsers(roomName));
        }

        serverSwitch.getLatestMessages().forEach(this::sendToClient);
    }

    private void login(){
        sendToClient(new DataMessage(1, null));
        boolean loggedIn = false;
        while (!loggedIn){
            DataMessage response = null;
            while (response == null){
                response = (DataMessage) receiveObject();
            }

            boolean passwordRegisterCheck = serverSwitch.switchLogin(response);

            if(!passwordRegisterCheck) {
                sendToClient(new DataMessage(2, null));

            } else if(passwordRegisterCheck && response.getCommando()==2) {
                sendToClient(new DataMessage(3, null));

            } else if(passwordRegisterCheck && response.getCommando()==3) {
                setName(response.getMessage().getSender());
                loggedIn = true;
                sendToClient(new DataMessage(3, null));
            }
        }
    }

    private Object receiveObject(){
        try {
            return objectInputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }


    public void sendToClient(Object o){
        try {
            if(isActive()) objectOutputStream.writeObject(o);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void run() {
        while (isActive()){
            try {
                Object o =  receiveObject();
                if(o != null){
                    serverSwitch.addMessage(o);
                    Thread.sleep(1);
                } else closeConnection();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void closeConnection(){
        try {
            setActive(false);
            objectInputStream.close();
            objectOutputStream.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Connection closed by client");
    }

    public boolean isActive() {
        return isActive.get();
    }

    public void setActive(boolean isActive) {
        this.isActive.set(isActive);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public synchronized String getActiveRoom() {
        return activeRoom;
    }

    public synchronized void setActiveRoom(String activeRoom) {
        this.activeRoom = activeRoom;
    }
}
