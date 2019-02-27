package Server;

import Data.DataMessage;

public class ServerSwitch {
    NetworkServer networkServer;


    public ServerSwitch(NetworkServer networkServer){
        this.networkServer = networkServer;
    }

    public void switchDataMessage(DataMessage data){
        switch (data.getCommando()) {
            case (0):
                networkServer.sendToAll(data);
                break;
            case(1):
                networkServer.roomSwitch(data.getMessage().getSender(), data.getMessage().getReceiver());
                break;
        }
    }

    public void addMessage(Object o){
        networkServer.addMessage(o);{
        }
    }

    public boolean switchLogin(DataMessage data){
            //REGISTER
        if (data.getCommando() == 2) {
            return PasswordCheck.getInstance()
                    .addUser(data.getMessage().getSender(), data.getMessage().getMessageData());
            // LOGIN
        } else if (data.getCommando() == 3) {
            return PasswordCheck.getInstance()
                    .checkUser(data.getMessage().getSender(), data.getMessage().getMessageData());
        }
        else return false;
    }
}
