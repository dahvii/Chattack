package Client;

import Client.gui.Controller;
import Data.DataHandler;
import Data.DataMessage;
import javafx.application.Platform;

public class ClientSwitch {
    private Controller controller;

    public ClientSwitch(Controller controller){
        this.controller = controller;
    }

    private void switchDataMessage(DataMessage data){
        switch (data.getCommando()) {
            case (0):
                DataHandler.getInstance().addMessage(data.getMessage());
                if(data.getMessage().getReceiver().equals(controller.getActiveRoom())){
                    Platform.runLater(() -> controller.printMessage(data.getMessage()));
                }
                break;
            case(1):
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
