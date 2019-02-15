package Client.gui;

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
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;

public class Controller {

    public Button sendBtn;
    public TextField input;
    public TextArea messages;
    User user = new User();
    public String receiverName = "Jebidiah";

    public Timestamp time;

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
        DataMessage dataMessage = new DataMessage(0, message);

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
    public void promt(){
        Stage window = new Stage();

        //blockar byte av fönster
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("Välj användarnamn");
        window.setMinWidth(250);
        window.setMinHeight(300);


        Label label = new Label();
        label.setText("Välj ett användarnamn");
        TextField input = new TextField();
        Button closeButton = new Button("Ok");
        closeButton.setOnAction(e -> {
            user.setName(input.getText());
            window.close();
        });

        VBox layout = new VBox(10);
        layout.getChildren().addAll(label,input,  closeButton);
        layout.setAlignment(Pos.CENTER);

        Scene scene = new Scene(layout);
        window.setScene(scene);
        window.showAndWait();

    }
}
