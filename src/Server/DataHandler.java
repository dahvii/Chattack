package Server;

import Data.DataMessage;
import Data.Message;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Stream;

public class DataHandler {
    private Map<String, List<Message>> allMessages;

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
            System.out.println("Loading " + messages.size() +" messages in room " +roomName);
            if(!messages.isEmpty()) {
                messages.forEach(this::addMessage);
            }
        }
    }

    public synchronized void saveRoomMessages(String roomName){
        FileHandler.getInstance().writeFile(roomName.toLowerCase()+"-messages.dat", getMessageMap().get(roomName));
    }

    public void addRoom(String roomName){
        getMessageMap().putIfAbsent(roomName, new ArrayList<>());
    }

    public void addMessage(Message msg){
        getMessageMap().get(msg.getReceiver()).add(msg);
        saveRoomMessages(msg.getReceiver());
    }

    public Stream<DataMessage> getLatestMessages(){
        return getMessageMap()
                .values()
                .stream()
                .flatMap(Collection::stream)
                .filter(message -> message.getTime().isAfter(LocalDateTime.now().minusHours(8)))
                .sorted(Comparator.comparing(Message::getTime))
                .map(message -> new DataMessage(0, message))
                ;
    }

    private synchronized Map<String, List<Message>> getMessageMap() {
        return allMessages;
    }
}
