package Client.gui;

import Client.ClientSwitch;
import Data.User;
import Data.DataMessage;
import Client.ChatRoom;
import javafx.application.Platform;
import javafx.event.ActionEvent;
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
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.sql.Timestamp;
import java.util.*;

public class Controller {

    private ClientSwitch clientSwitch;
    public TextField input;
    public ScrollPane allMessagesWindow;
    private User user = new User();
    public VBox msgBox;
    private Map<String, ChatRoom> chatRooms;
    private String activeRoom;
    private Accordion accOnlineUsers;

    private final String[] roomNames = new String[]{"main", "ninjas", "memes", "gaming", "horses"};


    public Controller(){
        clientSwitch = new ClientSwitch(this);
    }

    @FXML
    public void initialize(){
        loginPrompt();
        chatRooms = Collections.synchronizedMap(new HashMap<>());

        for(String roomName: roomNames) {
            chatRooms.put(roomName, new ChatRoom(roomName));
            DataHandler.getInstance().addRoom(roomName);
            DataHandler.getInstance().loadRoomMessages(roomName);
        }

        setActiveRoom("main");
        printRoomMessages(getActiveRoom());

        new Thread(clientSwitch::messageListener).start();
    }

    public void sendBtnClick(){
            if( !input.getText().equals("")){
                DataMessage dataMessage = new DataMessage(0, new Message(input.getText(), new Date().getTime(), user.getName(), getActiveRoom()));
                NetworkClient.getInstance().sendToServer(dataMessage);
                input.clear();
            }
    }

    private void loginPrompt(){
        //TODO: skapa scen istället för stage och lägg till i primarystage

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
        nameInput.setMaxWidth(200);
        PasswordField passwordInput= new PasswordField();
        passwordInput.setMaxWidth(200);

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
                // TODO: Kolla användare mot listan och se om det matchar
            }else { // om användaren  fyllt i ett namn och lösen korrekt
                user.setName(name);
                user.setPassword(password);
                window.close();
            }
        });

        //skapa en ny scen med innehållet och lägg upp och visa den
        Scene scene = new Scene(layout, 300, 300);
        window.setScene(scene);
        window.showAndWait();
//        inlogg.setText("Inloggad användare: " + user.getName());
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
        Label errorMessageName= new Label("Du måste fylla i ett användarnamn");
        errorMessageName.setStyle("visibility: hidden");
        Label nameLabel = new Label("Användarnamn");
        Label passwordLabel = new Label("Lösenord");

        Label errorMessagePassword= new Label("Minst en liten bokstav \n Minst en stor bokstav \n Minst en siffra \n Inga blanka tecken \n Minst 5 tecken");
        errorMessagePassword.setStyle("visibility: hidden");

        Stage registerWindow = new Stage();
        registerWindow.initModality(Modality.APPLICATION_MODAL);
        registerWindow.setTitle("Ny användare");
        registerWindow.setResizable(false);
        TextField nameInputRegistration = new TextField();
        nameInputRegistration.setPromptText("Namn");
        nameInputRegistration.setMaxWidth(200);
        TextField passwordInputRegistration = new TextField();
        passwordInputRegistration.setPromptText("Lösenord");
        passwordInputRegistration.setMaxWidth(200);
        Button registerButton = new Button("Registrera");
        VBox layout = new VBox(10);

        layout.getChildren().addAll(
                nameLabel,
                nameInputRegistration,
                errorMessageName,
                passwordLabel,
                passwordInputRegistration,
                errorMessagePassword,
                registerButton);
        Scene scene1 = new Scene(layout, 300, 300);
        layout.setAlignment(Pos.CENTER);

        registerWindow.setScene(scene1);
        registerWindow.show();

        registerButton.setDefaultButton(true);
        registerButton.setOnAction(e -> {
            errorMessageName.setStyle("visibility: hidden;");
            //om användaren inte har fyllt i ett namn
            //remove whitespaces
            String password = passwordInputRegistration.getText();
            String name = nameInputRegistration.getText().replaceAll("\\s+","");
            if (name.equals("")) {
                errorMessageName.setStyle("visibility: visible;");
            }else if (!passwordCheck(password, errorMessagePassword )){ //Metod som kollar att lösen är korrekt
            }else { // om användaren  fyllt i ett namn och lösen korrekt
                user.setName(name);
                user.setPassword(password);
                registerWindow.close();
            }
        });
    }

    private void printRoomMessages(String roomName) {
        DataHandler.getInstance().getRoomMessages(roomName).forEach(this::printMessage);
    }

    public void printMessage(Message msg) {
        HBox chatMessageContainer = new HBox();
        Label message = new Label(msg.getSender() + "\n" + msg.getMessageData() + "\n" + new Timestamp(msg.getTime()));
        message.setMinHeight(Control.USE_PREF_SIZE);
        msgBox.getChildren().add(chatMessageContainer);
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

    private void scroll(){
        msgBox.heightProperty().addListener(observable -> allMessagesWindow.setVvalue(1.0));
    }

    @FXML
    private void changeRoom(ActionEvent event) {
        String newRoom = ((Button) event.getSource()).getId();
        msgBox.getChildren().clear();
        printRoomMessages(newRoom);
        setActiveRoom(newRoom);
    }

    public User getUser() {
        return user;
    }

    public synchronized String getActiveRoom() {
        return activeRoom;
    }

    public synchronized void setActiveRoom(String activeRoom) {
        this.activeRoom = activeRoom;
    }
}




