package Client.gui;


import Client.ChatApp;
import Client.DataMessage;
import Client.Message;
import Client.NetworkClient;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.sql.Timestamp;
import java.util.Date;


public class Controller {

    public Button sendBtn;
    public TextField input;
    public TextArea messages;
    public String userName = "SENDERNAME";
    public String receiverName = "RECEIVERNAME";

    public Controller(){
        new Thread(this::messageListener).start();
    }


    public void sendBtnClick(){
        Date date = new Date();
        Timestamp time = new Timestamp(date.getTime());
        Message message = new Message(input.getText(), time, userName, receiverName);

        input.clear();
        System.out.println(message.getMessageData() + " "  + message.getTime() + " " + message.getSender() + " " + message.getReceiver());

        NetworkClient.getInstance().sendToServer(message);
//        messages.appendText(message.getMessageData() + "\n");
    }

    public void messageListener(){
        while (NetworkClient.getInstance().isActive()){
            Object o = NetworkClient.getInstance().getMessageQueue().poll();
            if (o != null) {
                printMessage((Message) o);

            }
        }
    }


    private void printMessage(Message msg){
        messages.appendText("\n" + msg.getMessageData() + " "  + msg.getTime() + " " + msg.getSender() + " " + msg.getReceiver());
    }





}
