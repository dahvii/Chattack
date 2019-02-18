package Client.gui;

import Client.ClientSwitch;
import Client.User;
import Data.DataMessage;
import javafx.application.Platform;
import javafx.geometry.Insets;
import Data.DataHandler;
import Data.Message;
import Client.NetworkClient;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.sql.Timestamp;
import java.util.Date;

public class Controller {

    private ClientSwitch clientSwitch;
    public Button sendBtn;
    public TextField input;
    public VBox messages;
    public ScrollPane allMessagesWindow;
    private User user = new User();
    private String receiverName = "Jebidiah";

    public Controller(){
        clientSwitch = new ClientSwitch(this);
    }

    @FXML
    public void initialize(){
        promt();
        DataHandler.getInstance().loadMessages(user.getName());
        DataHandler.getInstance().getAllMessages().forEach(this::printMessage);
        new Thread(clientSwitch::messageListener).start();
    }


    public void sendBtnClick(){
        DataMessage dataMessage = new DataMessage(0, new Message(input.getText(), new Date().getTime(), user.getName(), receiverName));
        input.clear();
        NetworkClient.getInstance().sendToServer(dataMessage);
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
        closeButton.setDefaultButton(true);
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
    
    public void printMessage(Message msg) {
        HBox chatMessageContainer = new HBox();
        Label message = new Label(msg.getSender() + "\n" + msg.getMessageData() + "\n" + new Timestamp(msg.getTime()));
        message.setMinHeight(Control.USE_PREF_SIZE);
        DropShadow dropShadow = new DropShadow();
        dropShadow.setRadius(5.0);
        dropShadow.setOffsetX(3.0);
        dropShadow.setOffsetY(3.0);
        dropShadow.setColor(Color.color(0.4, 0.5, 0.5));

        message.setWrapText(true);
        message.setPadding(new Insets(5, 5, 5, 5));
        message.setStyle("-fx-background-color: #46A59F; -fx-background-radius: 5");
        chatMessageContainer.getChildren().add(message);
        chatMessageContainer.setMargin(message, new Insets(5, 5, 5, 5));
        chatMessageContainer.setEffect(dropShadow);


        messages.getChildren().add(chatMessageContainer);
        //allMessagesWindow.setVvalue(1);
        scroll();
    }
    
    public User getUser() {
        return user;
    }

    private void scroll(){
        allMessagesWindow.setVvalue(1);
    }
}
