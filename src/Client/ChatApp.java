package Client;

import Data.DataHandler;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
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
        DataHandler.getInstance().saveMessages();
        NetworkClient.getInstance().setActive(false);
    }
    public static void main(String[] args) {
        launch(args);
    }
}
