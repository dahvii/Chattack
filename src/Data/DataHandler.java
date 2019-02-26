package Data;

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
        if (obj instanceof ArrayList) {
            ((ArrayList<Message>) obj).forEach(this::addMessage);
        }
    }

    public void saveMessages(){
        getMessageMap().entrySet().forEach(entry ->
                FileHandler.getInstance().writeFile(
                        entry.getKey().toLowerCase()+"-messages.dat", entry.getValue()));
    }

    public void addRoom(String roomName){
        getMessageMap().putIfAbsent(roomName, new ArrayList<>());
    }

    public void addMessage(Message msg){
        getMessageMap().get(msg.getReceiver()).add(msg);
    }

    public List<Message> getRoomMessages(String roomName){
        return getMessageMap().get(roomName);
    }

    public synchronized Map<String, List<Message>> getMessageMap() {
        return allMessages;
    }

    //:TODO Refactor and add methods for handling multiple rooms
}
