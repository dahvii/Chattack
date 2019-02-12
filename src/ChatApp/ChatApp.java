package ChatApp;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;


public class ChatApp extends Application {

    private boolean isServer = true;
    private TextArea messages = new TextArea();
    //Check if we are creating a server or a client
    public NetworkConnection connection = createServer();

    @Override
    public void init() throws Exception {
        connection.startConnection();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("../gui/sample.fxml"));
        primaryStage.setScene(new Scene(root, 800, 500));
        primaryStage.show();
        primaryStage.setTitle("Chattack");
    }

    @Override
    public void stop() throws Exception {
        connection.closeConnection();
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
