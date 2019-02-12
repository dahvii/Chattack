package gui;


import ChatApp.ChatApp;
import ChatApp.NetworkConnection;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Controller {

    TextField input;
    TextArea messages;
    String message;

    private Parent createContent() {

        TextField input = new TextField();
//        Display on screen if message comes from client or server
        input.setOnAction(event -> {
            message += input.getText();
            //Clear text from box after enter
            input.clear();

            messages.appendText(message + "\n");

            try {
                connection.send(message);
                NetworkConnection.s

            } catch (Exception e) {
                messages.appendText("Failed to send.\n");

            }
        });

        VBox root = new VBox(20, messages, input);
//        root.setPrefSize(600, 600);
        return root;
    }
}