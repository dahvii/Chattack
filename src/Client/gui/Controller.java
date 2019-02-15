package Client.gui;


import Client.ChatApp;
import Client.DataMessage;
import Client.Message;
import Client.NetworkClient;
import Server.NetworkServer;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Shadow;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.sql.Timestamp;
import java.util.Date;


public class Controller {


    public Button sendBtn;
    public TextField input;
    public VBox messages;
    public String userName = "Pelle";
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
                Platform.runLater(() -> printMessage((Message) o));


            }
        }
    }


    private void printMessage(Message msg){

        HBox chatMessageContainer = new HBox();
        Label message = new Label(msg.getSender() + "\n" + msg.getMessageData() + "\n" + msg.getTime());
        DropShadow dropShadow = new DropShadow();
        dropShadow.setRadius(5.0);
        dropShadow.setOffsetX(3.0);
        dropShadow.setOffsetY(3.0);
        dropShadow.setColor(Color.color(0.4, 0.5, 0.5));

        message.setWrapText(true);
        message.setPadding(new Insets(5, 5, 5, 5));
        message.setStyle("-fx-background-color: #46A59F; -fx-background-radius: 5");
        chatMessageContainer.getChildren().add(message);
        chatMessageContainer.setMargin(message, new Insets(5,5,5,5));
        chatMessageContainer.setEffect(dropShadow);

        messages.getChildren().add(chatMessageContainer);



//        messages.appendText(msg.getSender() + ": " + msg.getTime() +"\n" + msg.getMessageData() + "\n");
    }





}
