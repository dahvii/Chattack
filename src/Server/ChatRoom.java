package Server;

import java.util.ArrayList;
import java.util.List;
import Data.User;

public class ChatRoom {

    public String roomName;
    //ArrayList of User objects
    private List roomMembers;




    private ArrayList<Message> messages = new ArrayList<>();


    public ChatRoom() {
        createRooms();
    }

    public ChatRoom(String roomName){
        this.roomName = roomName;

        //just for testing
        addMessages();
        System.out.println("skapat chatroom "+roomName+" och "+messages.size()+" meddelanden");
    }

    private void addMessages(){

        Long time = (long) 13.37;

        for (int i =0; i < 10; i++) {
            messages.add(new Message("Testmeddelande från " + roomName, time, "", ""));
        }
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

    public ArrayList<Message> getMessages() {
        return messages;
    }
}
