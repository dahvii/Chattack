package Client.gui;

import Client.User;
import javafx.application.Platform;
import javafx.geometry.Insets;
import Data.DataHandler;
import Data.Message;
import Client.NetworkClient;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;

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
        DataHandler.getInstance().loadMessages(user.getName());
        DataHandler.getInstance().getAllMessages().forEach(this::printMessage);
        new Thread(this::messageListener).start();
    }


    public void sendBtnClick(){
        Message message = new Message(input.getText(), new Date().getTime(), user.getName(), receiverName);
//        DataMessage dataMessage = new DataMessage(0, message);
        input.clear();
        NetworkClient.getInstance().sendToServer(message);
    }

    private void messageListener(){
        while (NetworkClient.getInstance().isActive()){
            Object o = NetworkClient.getInstance().getMessageQueue().poll();
            //TODO: INSERT SWITCHCALL HERE
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

    private void promt(){
        Stage window = new Stage();
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
    
    private void printMessage(Message msg){

        HBox chatMessageContainer = new HBox();
        Label message = new Label(msg.getSender() + "\n" + msg.getMessageData() + "\n" + new Timestamp(msg.getTime()));
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

    public User getUser() {
        return user;
    }
}
