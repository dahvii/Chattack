package Client.gui;

import Client.User;
import Data.DataMessage;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import Data.DataHandler;
import Data.Message;
import Client.NetworkClient;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.sql.Timestamp;
import java.util.Date;

public class Controller {


    public Button sendBtn;
    public TextField input;
    public VBox messages;
    private User user = new User();
    private String receiverName = "Jebidiah";

    public Controller(){

    }

    @FXML
    public void initialize(){
        promt();
        DataHandler.getInstance().getAllMessages().forEach(this::printMessage);
        new Thread(this::messageListener).start();
    }


    public void sendBtnClick(){
        Date date = new Date();
        Timestamp time = new Timestamp(date.getTime());
        Message message = new Message(input.getText(), time, user.getName(), receiverName);
//        DataMessage dataMessage = new DataMessage(0, message);

        input.clear();
//        System.out.println(message.getMessageData() + " "  + message.getTime() + " " + message.getSender() + " " + message.getReceiver());
        NetworkClient.getInstance().sendToServer(message);
    }

    public void messageListener(){
        while (NetworkClient.getInstance().isActive()){
            Object o = NetworkClient.getInstance().getMessageQueue().poll();
            if (o instanceof Message) {
                DataHandler.getInstance().addMessage((Message) o);
                Platform.runLater(() -> printMessage((Message) o));
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void promt(){
        Stage window = new Stage();
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


    }
}
