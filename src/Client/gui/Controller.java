package Client.gui;


import Client.ChatApp;
import Client.NetworkClient;
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
        NetworkClient.getInstance().sendToServer(message);
//        ChatApp chatApp = new ChatApp();
//        chatApp.send(message);
        messages.appendText(message + "\n");
    }



}
