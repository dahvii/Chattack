package Client.gui;

import Data.Message;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;

import java.time.format.DateTimeFormatter;

public class GenerateGraphics {
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    Controller controller;

    public GenerateGraphics(Controller controller){
        this.controller = controller;
    }

    public HBox generateMessageBox(Message msg){
        HBox chatMessageContainer = new HBox();
        Label message = new Label(msg.getSender() + "\n" + msg.getMessageData() + "\n" + msg.getTime().format(formatter));
        message.setMinHeight(Control.USE_PREF_SIZE);
        message.setMinWidth(450);
        styleMessage(message, chatMessageContainer, msg);
        return chatMessageContainer;
    }

    private void styleMessage(Label messageLabel, HBox chatMessageContainer, Message msg) {
        DropShadow dropShadow = new DropShadow();
        dropShadow.setRadius(5.0);
        dropShadow.setOffsetX(3.0);
        dropShadow.setOffsetY(3.0);
        dropShadow.setColor(Color.color(0.4, 0.5, 0.5));
        messageLabel.setWrapText(true);
        messageLabel.setPadding(new Insets(5, 5, 5, 5));
        messageLabel.setStyle("-fx-background-color: linear-gradient(from 25% 25% to 100% 100%, #67b75d, #6fc165); -fx-background-radius: 5");
        if(msg.getSender().equals(controller.getUser().getName())){
            messageLabel.setStyle("-fx-background-color: linear-gradient(from 25% 25% to 100% 100%, #5d7799, #6986a9); -fx-background-radius: 5");
            messageLabel.setTextFill(Color.WHITE);
        }
        chatMessageContainer.getChildren().add(messageLabel);
        chatMessageContainer.setMargin(messageLabel, new Insets(5, 5, 5, 5));
        chatMessageContainer.setEffect(dropShadow);
    }

    public void styleButton(Boolean isActive, Button button){
        if(isActive){
            button.setStyle("-fx-background-color:  #5274b4, linear-gradient(from 25% 25% to 100% 100%, #37517a, #4e74af), radial-gradient(center 50% -40%, radius 200%, #4e74af 45%, rgba(230,230,230,0) 50%); -fx-text-fill: white;");
        }else{
            button.setStyle(" -fx-background-color:  #c3c4c4, linear-gradient(#d6d6d6 50%, white 100%), radial-gradient(center 50% -40%, radius 200%, #e6e6e6 45%, rgba(230,230,230,0) 50%);");
        }
    }

    public HBox generateOnlineUserBox(String name){
        Label nameLabel = new Label(name);
        HBox userBox = new HBox();
        userBox.setPadding(new Insets(3,0,0,3));
        userBox.setId(name);
        ImageView image = new ImageView("Client/gui/icons8-sphere-48.png");
        image.setFitHeight(15);
        image.setFitWidth(15);
        userBox.getChildren().addAll(image, nameLabel);
        return userBox;
    }
}
