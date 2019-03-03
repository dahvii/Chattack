package Client;

import Client.gui.Controller;
import Server.DataHandler;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ChatApp extends Application {

    private Controller controller;

    public ChatApp() {

    }

    @Override
    public void init() {
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("gui/sample.fxml"));
        Parent root = loader.load();
        controller = loader.getController();
        controller.initialize();
        controller.startLogin();
        if(controller.isLoggedIn()) {
            primaryStage.setScene(new Scene(root));
            primaryStage.setResizable(false);
            primaryStage.setTitle("Chattack");
            primaryStage.show();
        }
    }

    @Override
    public void stop() {
        NetworkClient.getInstance().setActive(false);
    }

    public static void main(String[] args) {
        launch(args);
    }
}