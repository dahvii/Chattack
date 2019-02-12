package ChatApp;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;


public class ChatApp extends Application {

    private boolean isServer = false;

    private TextArea messages = new TextArea();
    //Check if we are creating a server or a client
    private NetworkConnection connection = isServer ? createServer() : createClient();

    private Parent createContent() {
        //Set up JavaFX stuff
        messages.setPrefHeight(550);

        TextField input = new TextField();
        //Display on screen if message comes from client or server
        input.setOnAction(event -> {
            String message = isServer ? "Server: " : "Client: ";
            message += input.getText();
            //Clear text from box after enter
            input.clear();

            messages.appendText(message + "\n");

            try {
                connection.send(message);
            } catch (Exception e) {
                messages.appendText("Failed to send.\n");

            }
        });

        VBox root = new VBox(20, messages, input);
        root.setPrefSize(600, 600);
        return root;
    }

    @Override
    public void init() throws Exception {
        connection.startConnection();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setScene(new Scene(createContent()));
        primaryStage.show();
    }

    @Override
    public void stop() throws Exception {
        connection.closeConnection();
    }

    public void send(String input){

        System.out.println("i send metod, input är : "+ input);
        String text = isServer ? "Server: " : "Client: ";
        text += input;//.getText();
        Message message = new Message(text);
        DataMessage currMessage= new DataMessage(0, message);

        //Clear text from box after enter
//        input.clear();

        messages.appendText(message.messageData + "\n");

        try {
            System.out.println("försöker skicka1");

            connection.send(message);
            System.out.println("försöker skicka2");
        } catch (Exception e) {
            messages.appendText("Failed to send.\n");
            e.printStackTrace();

        }

    }

    private Server createServer() {
        return new Server(8080, data -> {
            //runLater is used to execute updates on a JavaFX app thread
            //Since this will be a long-running operation we can't clog up the whole thread or the UI would freeze.
            Platform.runLater(() -> {
                messages.appendText(data.toString() + "\n");
            });
        });
    }

    private Client createClient() {
        return new Client("127.0.0.1", 8080, data -> {
            Platform.runLater(() -> {
                messages.appendText(data.toString() + "\n");
            });
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}
