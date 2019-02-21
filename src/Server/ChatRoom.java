package Server;

import java.util.ArrayList;
import java.util.List;
import Data.User;

public class ChatRoom {

    public String roomName;
    //ArrayList of User objects
    private List roomMembers;

    public ChatRoom(String roomNumber) {
        this.roomName = roomName;
        this.roomMembers = new ArrayList();
    }

    private void addToRoom(User user) {
        this.roomMembers.add(user);
    }

    private void removeFromRoom(User user) {
        this.roomMembers.remove(user);
    }

    private List getRoomMembers() {
        return this.roomMembers;
    }
}
