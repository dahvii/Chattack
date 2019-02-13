package Client;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;


public class ChatApp extends Application {

    public ChatApp(){

    }

    @Override
    public void init() {
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("gui/sample.fxml"));
        primaryStage.setScene(new Scene(root, 800, 500));
        primaryStage.show();
        primaryStage.setTitle("Chattack");
    }

    @Override
    public void stop() {
        NetworkClient.getInstance().setActive(false);
    }


//    public void send(String input){
//
//        System.out.println("i send metod, input är : " + input);
//        String text = input;//.getText();
//        Message message = new Message(text);
//        DataMessage currMessage= new DataMessage(0, message);
//
//        //Clear text from box after enter
////        input.clear();
//
//        messages.appendText(message.messageData + "\n");
//
//        try {
//            System.out.println("försöker skicka1");
////            client.sendToServer(currMessage);
//            System.out.println("försöker skicka2");
//        } catch (Exception e) {
//            messages.appendText("Failed to send.\n");
//            e.printStackTrace();
//        }
//
//    }

    public static void main(String[] args) {
        launch(args);
    }
}
