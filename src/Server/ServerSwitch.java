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
        return networkServer.getOnlineUsers(roomName);
    }
}
