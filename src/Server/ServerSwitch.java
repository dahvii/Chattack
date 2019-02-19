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
                System.out.println("CASE 1-" + data.getMessage().getMessageData());
                break;
        }
    }
}
