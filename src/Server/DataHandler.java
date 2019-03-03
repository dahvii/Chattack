package Server;

import Data.Message;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

public class DataHandler {
    private Map<String, List<Message>> allMessages;

    private static DataHandler instance;

    public static DataHandler getInstance() {
        if (instance==null) instance = new DataHandler();
        return instance;
    }

    private DataHandler() {
        allMessages = Collections.synchronizedMap(new ConcurrentHashMap<>());
    }

    public void loadRoomMessages(String roomName){
        Object obj = FileHandler.getInstance().readFile(roomName.toLowerCase()+"-messages.dat");
        if (obj instanceof ArrayList) {
            addRoom(roomName, (ArrayList<Message>) obj);
            System.out.println("Loading " + getMessageMap().get(roomName).size() +" messages in room " +roomName);
        } else {
            addRoom(roomName, null);
            System.out.println("Creating empty messageList in room " +roomName);
        }
    }

    public void saveRoomMessages(String roomName){
        FileHandler.getInstance().writeFile(roomName.toLowerCase()+"-messages.dat", getMessageMap().get(roomName));
    }

    public void addRoom(String roomName, List<Message> messageList){
        if(messageList != null) getMessageMap().putIfAbsent(roomName, messageList);
        else  getMessageMap().putIfAbsent(roomName, Collections.synchronizedList(new ArrayList<>()));
    }

    public void addMessage(Message msg){
        getMessageMap().get(msg.getReceiver()).add(msg);
        saveRoomMessages(msg.getReceiver());
    }

    public Stream<Message> getLatestMessages(String roomName){
        ArrayList<Message> tempMessageList = new ArrayList<>(getMessageMap().get(roomName));
        return tempMessageList
                .stream()
                .filter(message -> message.getTime().isAfter(LocalDateTime.now().minusHours(8)))
                .sorted(Comparator.comparing(Message::getTime));
    }

    private synchronized Map<String, List<Message>> getMessageMap() {
        return allMessages;
    }
}
