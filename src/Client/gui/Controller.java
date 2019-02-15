package Client.gui;

import Data.DataHandler;
import Data.Message;
import Client.NetworkClient;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;

public class Controller {

    public Button sendBtn;
    public TextField input;
    public TextArea messages;
    public String userName = "SENDERNAME";
    public String receiverName = "RECEIVERNAME";

    public Controller(){

    }

    @FXML
    public void initialize(){
        DataHandler.getInstance().getAllMessages().forEach(this::printMessage);
        new Thread(this::messageListener).start();
    }


    public void sendBtnClick(){
        Date date = new Date();
        Timestamp time = new Timestamp(date.getTime());
        Message message = new Message(input.getText(), time, userName, receiverName);

        input.clear();
        System.out.println(message.getMessageData() + " "  + message.getTime() + " " + message.getSender() + " " + message.getReceiver());
        NetworkClient.getInstance().sendToServer(message);
    }

    public void messageListener(){
        while (NetworkClient.getInstance().isActive()){
            Object o = NetworkClient.getInstance().getMessageQueue().poll();
            if (o instanceof Message) {
                DataHandler.getInstance().addMessage((Message) o);
                printMessage((Message) o);
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void printMessage(Message msg){
        messages.appendText("\n" + msg.getMessageData() + " "  + msg.getTime() + " " + msg.getSender() + " " + msg.getReceiver());
    }
}
