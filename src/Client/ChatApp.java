package Client;

import Client.gui.Controller;
import Data.DataHandler;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ChatApp extends Application {

    private Controller controller;

    public ChatApp(){

    }

    @Override
    public void init() {
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("gui/sample.fxml"));

        Parent root = loader.load();
        controller = loader.getController();
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
        primaryStage.setResizable(false);
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
