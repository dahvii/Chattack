package Client;

import Client.gui.Controller;
import Server.DataHandler;
import Data.DataMessage;
import javafx.application.Platform;

public class ClientSwitch {
    private Controller controller;

    public ClientSwitch(Controller controller){
        this.controller = controller;
    }

    private void switchDataMessage(DataMessage data){
        switch (data.getCommando()) {
//          Receive message
            case (0):
                DataHandler.getInstance().addMessage(data.getMessage());
                if(data.getMessage().getReceiver().equals(controller.getActiveRoom())){
                    Platform.runLater(() -> controller.printMessage(data.getMessage()));
                }
                break;
//          Server asks for username and password
            case(1):
                controller.setServerWaiting(false);
                break;
//          Server delivers register/password check response FAIL
            case(2):
                controller.setServerResponse(false);
                controller.setServerWaiting(false);
                break;
//          Server delivers register/password check response SUCCESS
            case(3):
                controller.setServerResponse(true);
                controller.setServerWaiting(false);
                break;
//          Server delivers active user list
            case(4):
                System.out.println("CASE 1-" + data.getMessage().getMessageData());
                break;
        }
    }

    public void messageListener(){
        while (NetworkClient.getInstance().isActive()){
            Object o = NetworkClient.getInstance().getMessageQueue().poll();
            try {
                if (o instanceof DataMessage) {
                    switchDataMessage((DataMessage) o);
                }
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
