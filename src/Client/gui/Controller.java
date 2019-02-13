package Client.gui;


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

    public void sendBtnClick(){
        Date date= new Date();
        time = new Timestamp(date.getTime());
        Message message = new Message(input.getText(), time, userName, receiverName);
        input.clear();

        NetworkClient.getInstance().sendToServer(message);
        messages.appendText(message.getMessageData() + "\n");
        System.out.println(message.getMessageData()+ time+ userName+ receiverName);
    }



}
