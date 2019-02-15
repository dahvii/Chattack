package Client.gui;


import Client.*;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import Client.ChatApp;
import Client.DataMessage;
import Client.Message;
import Client.NetworkClient;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.sql.Timestamp;
import java.util.Date;


public class Controller {

    public Button sendBtn;
    public TextField input;
    public TextArea messages;
    User user = new User();
    public String receiverName = "Jebidiah";

    public Timestamp time;

    public Controller(){
        promt();
        new Thread(this::messageListener).start();
        System.out.println(user.getName());
    }


    public void sendBtnClick(){
        Date date = new Date();
        Timestamp time = new Timestamp(date.getTime());
        Message message = new Message(input.getText(), time, user.getName(), receiverName);
        DataMessage dataMessage = new DataMessage(0, message);

        input.clear();
        System.out.println(message.getMessageData() + " "  + message.getTime() + " " + message.getSender() + " " + message.getReceiver());

        NetworkClient.getInstance().sendToServer(dataMessage);
//        messages.appendText(message.getMessageData() + "\n");
    }

    public void messageListener(){
        while (NetworkClient.getInstance().isActive()){
            Object o = NetworkClient.getInstance().getMessageQueue().poll();
            if (o != null) {
                printMessage((DataMessage) o);

            }
        }
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

    private void printMessage(DataMessage dataMessage){
        messages.appendText("\n" + dataMessage.getMessage().getMessageData() + " "  + dataMessage.getMessage().getTime() + " " + dataMessage.getMessage().getSender() + " " + dataMessage.getMessage().getReceiver());
    }

}
