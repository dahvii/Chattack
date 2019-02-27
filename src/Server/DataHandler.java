package Server;

import Data.Message;

import java.util.*;

public class DataHandler {
    private Map<String, List<Message>> allMessages;  //For later use

    private static DataHandler instance;

    public static DataHandler getInstance() {
        if (instance==null) instance = new DataHandler();
        return instance;
    }

    private DataHandler() {
        allMessages = Collections.synchronizedMap(new HashMap<>());
    }

    public void loadRoomMessages(String roomName){
        Object obj = FileHandler.getInstance().readFile(roomName.toLowerCase()+"-messages.dat");
        addRoom(roomName);
        if (obj instanceof ArrayList) {
            ArrayList<Message> messages = (ArrayList<Message>) obj;
            System.out.println(messages.size());
            if(!messages.isEmpty()) {
                messages.forEach(this::addMessage);
            }
        }
    }

    public void saveRoomMessages(String roomName){
        FileHandler.getInstance().writeFile(roomName.toLowerCase()+"-messages.dat", getMessageMap().get(roomName));
    }

    public void addRoom(String roomName){
        getMessageMap().putIfAbsent(roomName, new ArrayList<>());
    }

    public void addMessage(Message msg){
        getMessageMap().get(msg.getReceiver()).add(msg);
        saveRoomMessages(msg.getReceiver());
    }

    public List<Message> getRoomMessages(String roomName){
        return getMessageMap().get(roomName);
    }

    public synchronized Map<String, List<Message>> getMessageMap() {
        return allMessages;
    }

    //:TODO Refactor and add methods for handling multiple rooms
}