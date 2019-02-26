package Client;

import java.util.ArrayList;
import java.util.List;

import Data.Message;
import Data.User;

public class ChatRoom {

    private String name;
    private List<String> users;

    public ChatRoom(String name){
        this.name = name;
        users = new ArrayList<>();
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

}
