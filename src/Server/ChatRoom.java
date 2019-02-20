package Server;

import java.util.ArrayList;
import java.util.List;

public class ChatRoom {

    public int roomNumber;
    private List roomMembers;

    public ChatRoom(int roomNumber) {
        this.roomNumber = roomNumber;
        this.roomMembers = new ArrayList();
    }

    private void addToRoom(Object User) {
        this.roomMembers.add(User);
    }

    private void removeFromRoom(Object User) {
        this.roomMembers.remove(User);
    }

    private List getRoomMembers() {
        return this.roomMembers;
    }
}
