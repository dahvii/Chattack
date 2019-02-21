package Client.gui;

import Client.ClientSwitch;
import Data.User;
import Data.DataMessage;
import javafx.application.Platform;
import javafx.geometry.Insets;
import Data.DataHandler;
import Data.Message;
import Client.NetworkClient;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.w3c.dom.Text;

import java.sql.Timestamp;
import java.util.Date;

public class Controller {

    private ClientSwitch clientSwitch;
    public Button sendBtn;
    public TextField input;
    public VBox messages;
    public Label inlogg;
    public ScrollPane allMessagesWindow;
    private User user = new User();
    private String receiverName = "Jebidiah";
    public Controller(){
        clientSwitch = new ClientSwitch(this);
    }

    @FXML
    public void initialize(){
        promt();
        DataHandler.getInstance().loadMessages(user.getName());
        DataHandler.getInstance().getAllMessages().forEach(this::printMessage);
        new Thread(clientSwitch::messageListener).start();

    }


    public void sendBtnClick(){
            if( !input.getText().equals("")){
                DataMessage dataMessage = new DataMessage(0, new Message(input.getText(), new Date().getTime(), user.getName(), receiverName));
                NetworkClient.getInstance().sendToServer(dataMessage);
                input.clear();
            }
    }


    protected void promt(){
        //skapa ny stage och sätt lite egenskaper
        Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("Välj användarnamn");
        window.setMinWidth(300);
        window.setMinHeight(400);

        //skapa element och egenskaperna för innehållet
        Label errorMessageName= new Label();
        errorMessageName.setText("Du måste fylla i ett användarnamn");
        errorMessageName.setStyle("visibility: hidden");
        Label errorMessagePassword= new Label();
        errorMessagePassword.setText("Minst en liten bokstav \n Minst en stor bokstav \n Minst en siffra \n Inga blanka tecken \n Minst 5 tecken");
        errorMessagePassword.setStyle("visibility: hidden");

        TextField nameInput= new TextField();
        TextField passwordInput= new TextField();

        Button okButton = new Button("Ok");
        Button newUser = new Button ("Inte medlem? \n Tryck här!");
        newUser.setOnAction(event -> registerForm());

        Label userLabel = new Label("Användarnamn");
        Label passwordLabel = new Label("Lösenord");
        Label welcomeLabel = new Label("Chattack!");

        //lägg till elementen till layouten
        VBox layout = new VBox(10);
        layout.getChildren().addAll(
                welcomeLabel,
                userLabel,
                nameInput,
                passwordLabel,
                passwordInput,
                okButton,
                errorMessageName,
                errorMessagePassword,
                newUser);
        layout.setAlignment(Pos.CENTER);
        window.setResizable(false);

        //skapa funktionalitet och eventhantering
        window.setOnCloseRequest(e -> {
            //TODO: nedan kommando gör vad vi vill användarmässigt men skapar massa exceptions - dvs inte vackert
            Platform.exit();
        });

        okButton.setDefaultButton(true);
        okButton.setOnAction(e -> {
            errorMessageName.setStyle("visibility: hidden;");
            //om användaren inte har fyllt i ett namn
            //remove whitespaces
            String password = passwordInput.getText();
            String name = nameInput.getText().replaceAll("\\s+","");

            if (name.equals("")) {
                errorMessageName.setStyle("visibility: visible;");
            }else if (!passwordCheck(password, errorMessagePassword )){
            }else { // om användaren  fyllt i ett namn och lösen korrekt
                user.setName(name);
                user.setPassword(password);
                window.close();
            }
        });

        //skapa en ny scen med innehållet och lägg upp och visa den
        Scene scene = new Scene(layout);
        window.setScene(scene);
        window.showAndWait();
        inlogg.setText("Inloggad användare: " + user.getName());
    }

    public boolean passwordCheck(String password, Label errorMessagePassword){
        if (!password.matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).{5,}$")){
            System.out.println("false");
            errorMessagePassword.setStyle("visibility: visible");
            return false;
        }

        System.out.println("True");
        return true;
    }

    public void registerForm(){
        Stage registerWindow = new Stage();
        registerWindow.initModality(Modality.APPLICATION_MODAL);
        registerWindow.setTitle("Ny användare");
        registerWindow.setResizable(false);
        TextField name = new TextField();
//        name.setPromptText("Namn");
        name.setMaxWidth(200);
        TextField password = new TextField();
//        password.setPromptText("Lösenord");
        password.setMaxWidth(200);
        Button registerButton = new Button("Registrera");
        VBox layout = new VBox(10);

        layout.getChildren().addAll(name, password, registerButton);
        Scene scene1 = new Scene(layout, 300, 300);
        layout.setAlignment(Pos.TOP_CENTER);

        registerWindow.setScene(scene1);
        registerWindow.show();




    }


    
    public void printMessage(Message msg) {
        HBox chatMessageContainer = new HBox();
        Label message = new Label(msg.getSender() + "\n" + msg.getMessageData() + "\n" + new Timestamp(msg.getTime()));
        message.setMinHeight(Control.USE_PREF_SIZE);
        messages.getChildren().add(chatMessageContainer);
        styleMessage(message, chatMessageContainer);
        scroll();
    }

    public void styleMessage(Label message, HBox chatMessageContainer){
        DropShadow dropShadow = new DropShadow();
        dropShadow.setRadius(5.0);
        dropShadow.setOffsetX(3.0);
        dropShadow.setOffsetY(3.0);
        dropShadow.setColor(Color.color(0.4, 0.5, 0.5));
        message.setWrapText(true);
        message.setPadding(new Insets(5, 5, 5, 5));
        message.setStyle("-fx-background-color: linear-gradient(from 25% 25% to 100% 100%, #3ead3a, #93d379); -fx-background-radius: 5");
        chatMessageContainer.getChildren().add(message);
        chatMessageContainer.setMargin(message, new Insets(5, 5, 5, 5));
        chatMessageContainer.setEffect(dropShadow);
    }

    public User getUser() {
        return user;
    }

    private void scroll(){
        messages.heightProperty().addListener(observable -> allMessagesWindow.setVvalue(1.0));
    }
}




