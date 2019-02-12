package gui;


import ChatApp.ChatApp;
import ChatApp.NetworkConnection;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;


public class Controller {

    public Button sendBtn;
    public TextField input;
    public TextArea messages;

    public void sendBtnClick(){
        String message = input.getText();
        sendBtn.setText("Send");
        input.clear();
        ChatApp chatApp = new ChatApp();
        chatApp.send(message);
        messages.appendText(message + "\n");
    }



}
