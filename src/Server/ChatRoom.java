package Server;

import java.util.ArrayList;
import java.util.List;
import Client.User;

public class ChatRoom {

    public String roomName;
    //ArrayList of User objects
    private List roomMembers;

    public ChatRoom() {
        createRooms();
    }

    private void createRooms() {

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
