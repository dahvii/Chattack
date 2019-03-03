package Client.gui;

import Data.Message;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.time.format.DateTimeFormatter;

public class GenerateGraphics {
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");

    Controller controller;

    public GenerateGraphics(Controller controller){
        this.controller = controller;
    }

    public VBox generateMessageBox(Message msg){
        VBox chatMessageContainer = new VBox();
        Label messageSender = new Label(msg.getSender());
        messageSender.setFont(Font.font("Arial"));
        Label messageData = new Label(msg.getMessageData());
        messageData.setFont(Font.font("Arial"));
        if(msg.getSender().equals(controller.getUser().getName())){
            messageData.setTextFill(Color.WHITE);
        }
        messageData.setWrapText(true);
        Label messageTime = new Label(msg.getTime().format(formatter));
        messageTime.setFont(Font.font("Arial"));
        VBox messageTextContainer = new VBox();
        messageTextContainer.getChildren().add(messageData);
        styleMessage(messageSender, messageTextContainer, messageTime, chatMessageContainer, msg);
        return chatMessageContainer;
    }

    private void styleMessage(
            Label messageSender, VBox messageData, Label messageTime, VBox chatMessageContainer, Message msg) {
        DropShadow dropShadow = new DropShadow();
        dropShadow.setRadius(5.0);
        dropShadow.setOffsetX(3.0);
        dropShadow.setOffsetY(3.0);
        dropShadow.setColor(Color.color(0.4, 0.5, 0.5));
        messageData.setMinHeight(Control.USE_PREF_SIZE);
        chatMessageContainer.setPadding(new Insets(5, 5, 5, 5));
        chatMessageContainer.setStyle("-fx-background-color: linear-gradient(from 25% 25% to 100% 100%," +
                " #67b75d, #6fc165); -fx-background-radius: 5");
        if(msg.getSender().equals(controller.getUser().getName())){
            chatMessageContainer.setStyle("-fx-background-color: linear-gradient(from 25% 25% to 100% 100%," +
                    " #5d7799, #6986a9); -fx-background-radius: 5");
            messageSender.setTextFill(Color.WHITE);
            messageTime.setTextFill(Color.WHITE);
        }
        messageSender.setStyle("-fx-font-weight: bold");
        chatMessageContainer.getChildren().addAll(messageSender, messageData, messageTime);
        VBox.setMargin(chatMessageContainer,new Insets(5,5,5,5));
        chatMessageContainer.setEffect(dropShadow);
    }

    public void styleButton(Boolean isActive, Button button){
        if(isActive){
            button.setStyle("-fx-background-color:  #5274b4, linear-gradient(from 25% 25% to 100% 100%," +
                    " #37517a, #4e74af), radial-gradient(center 50% -40%, radius 200%, #4e74af 45%," +
                    " rgba(230,230,230,0) 50%); -fx-text-fill: white;");
        }else{
            button.setStyle(" -fx-background-color:  #c3c4c4, linear-gradient(#d6d6d6 50%, white 100%)," +
                    " radial-gradient(center 50% -40%, radius 200%, #e6e6e6 45%, rgba(230,230,230,0) 50%);");
        }
    }

    public HBox generateOnlineUserBox(String name){
        Label label1 = new Label(name);
        label1.setFont(Font.font("Arial"));
        HBox userBox = new HBox();
        userBox.setPadding(new Insets(3,0,0,3));
        userBox.setId(name);
        ImageView image = new ImageView("Client/gui/icons8-sphere-48.png");
        if(name.equals(controller.getUser().getName())){
            label1.setStyle("-fx-font-weight: bold");
        }
        image.setFitHeight(15);
        image.setFitWidth(15);
        userBox.getChildren().addAll(image, label1);
        return userBox;
    }
}
