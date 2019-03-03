package Client;

import java.util.ArrayList;
import java.util.List;

import Data.Message;

public class ChatRoom {

    private String name;
    private List<String> users;
    private List<Message> messages;

    public ChatRoom(String name){
        this.name = name;
        users = new ArrayList<>();
        messages = new ArrayList<>();

    }

    public void addUser(String user) {
        users.add(user);
    }

    public void removeUser(String user) {
        users.remove(user);
    }

    public List getUsers() {
        return users;
    }

    public void addMessage(Message message) {
        messages.add(message);
    }

    public List<Message> getMessages() {
        return messages;
    }

}
