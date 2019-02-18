package Data;

import java.util.ArrayList;
import java.util.List;

public class DataHandler {
    private List<Message> allMessages;
//    private Map<String, List<Message>> allRooms;  //For later use

    private static DataHandler instance;

    public static DataHandler getInstance() {
        if (instance==null) instance = new DataHandler();
        return instance;
    }

    private DataHandler() {
    }

    public void loadMessages(String name){
        Object obj = FileHandler.getInstance().readFile(name.toLowerCase()+"-messages.dat");
        if (obj instanceof ArrayList) {
            allMessages = (ArrayList<Message>) obj;
            System.out.println(allMessages.size());
        } else allMessages = new ArrayList<>();
    }

    public void saveMessages(String name){
        FileHandler.getInstance().writeFile(name.toLowerCase()+"-messages.dat", allMessages);
    }

    public void addMessage(Message msg){
        allMessages.add(msg);
    }

    public List<Message> getAllMessages() {
        return allMessages;
    }

    //:TODO Refactor and add methods for handling multiple rooms
}
