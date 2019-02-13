package Client.gui;


import Client.NetworkClient;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;


public class Controller {

    public Button sendBtn;
    public TextField input;
    public TextArea messages;

    public Controller(){
        new Thread(this::messageListener).start();
    }

    public void sendBtnClick(){
        String message = input.getText();
        sendBtn.setText("Send");
        input.clear();
        NetworkClient.getInstance().sendToServer(message);
    }

    public void messageListener(){
        while (NetworkClient.getInstance().isActive()){
            Object o = NetworkClient.getInstance().getMessageQueue().poll();
            if (o != null) {
                messages.appendText("\n" + (String) o);
            }
        }
    }





}
