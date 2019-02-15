package Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DataHandler {
    private List<Message> allMessages;
//    private Map<String, List<Message>> allRooms;  //For later use

    private static DataHandler instance;

    public static DataHandler getInstance() {
        if (instance==null) instance = new DataHandler();
        return instance;
    }

    private DataHandler() {
        loadMessages();
    }

    private void loadMessages(){
        Object obj = FileHandler.getInstance().readFile("messages.dat");
        if (obj instanceof ArrayList) {
            allMessages = (ArrayList<Message>) obj;
            System.out.println(allMessages.size());
        } else allMessages = new ArrayList<>();
    }

    public void saveMessages(){
        FileHandler.getInstance().writeFile("messages.dat", allMessages);
    }

    public void addMessage(Message msg){
        allMessages.add(msg);
    }

    public List<Message> getAllMessages() {
        return allMessages;
    }

    //:TODO Refactor and add methods for handling multiple rooms
}
