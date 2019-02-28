package Server;

import Data.DataMessage;
import Data.Message;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ServerSwitch {
    NetworkServer networkServer;

    public ServerSwitch(NetworkServer networkServer) {
        this.networkServer = networkServer;
    }

    public void switchDataMessage(DataMessage data) {
        switch (data.getCommando()) {
            case (0):
                DataHandler.getInstance().addMessage(data.getMessage());
                networkServer.sendToAll(data);
                break;
            case (1):
                networkServer.roomSwitch(data.getMessage().getSender(), data.getMessage().getMessageData());
                break;
            case (5):
                networkServer.sendToAll(data);
        }
    }

    public void addMessage(Object o) {
        networkServer.addMessage(o);
        {
        }
    }

    public boolean switchLogin(DataMessage data) {
        //REGISTER
        if (data.getCommando() == 2) {
            return PasswordCheck.getInstance()
                    .addUser(data.getMessage().getSender(), data.getMessage().getMessageData());
            // LOGIN
        } else if (data.getCommando() == 3) {
            if (networkServer.userOnline(data.getMessage().getSender())) return false;
            else return PasswordCheck.getInstance()
                    .checkUser(data.getMessage().getSender(), data.getMessage().getMessageData());
        } else return false;
    }

    public DataMessage getOnlineUsers(String roomName) {
        String usersString = "";
        for (Connection c : networkServer.getConnectionList()) {
            if (c.getActiveRoom().equals(roomName)) usersString += c.getName() + ",";
        }
        if (usersString.length() > 0) usersString = usersString.substring(0, usersString.length() - 1);
        return new DataMessage(4, new Message(usersString, LocalDateTime.now(), null, roomName));
    }

    public ArrayList<DataMessage> getLatestMessages() {
        ArrayList<DataMessage> messageList = new ArrayList<>();
        LocalDateTime localDateTime = LocalDateTime.now();
        for (List<Message> roomMessages : DataHandler.getInstance().getMessageMap().values()) {
            roomMessages.forEach(message -> {
                if (message.getTime().isAfter(localDateTime.minusHours(24))) {
                    messageList.add(new DataMessage(0, message));
                }
            });
        }
        return messageList;
    }

    public void removeConnection(Connection c){
        networkServer.removeConnection(c);
    }
}
