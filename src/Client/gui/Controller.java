package Client.gui;


import Client.ChatApp;
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
    public String userName = "Jebidiah";
    public String receiverName = "Jebidiah";
    public Timestamp time;

    public Controller(){
        new Thread(this::messageListener).start();
    }

    public String userName = "Johnny";
    public String receiverName = "Annabelle";

    public void sendBtnClick(){
        Date date = new Date();
        Timestamp time = new Timestamp(date.getTime());
        Message message = new Message(input.getText(), time, userName, receiverName);

        input.clear();

        NetworkClient.getInstance().sendToServer(message);
        messages.appendText(message.getMessageData() + "\n");
    }

    public void messageListener(){
        while (NetworkClient.getInstance().isActive()){
            Object o = NetworkClient.getInstance().getMessageQueue().poll();
            if (o != null) {
                messages.appendText("\n" + (String) o);
            }
        }
    }





}
