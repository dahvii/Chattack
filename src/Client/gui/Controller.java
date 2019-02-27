package Client.gui;

import Client.ClientSwitch;
import Data.User;
import Data.DataMessage;
import Client.ChatRoom;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class Controller {

    private ClientSwitch clientSwitch;
    private AtomicBoolean serverResponse = new AtomicBoolean(false);
    private AtomicBoolean serverWaiting = new AtomicBoolean(true);
    public TextField input;
    public ScrollPane allMessagesWindow;
    private User user = new User();
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    public VBox msgBox;
    private Map<String, ChatRoom> chatRooms;
    private String activeRoom;
    private Accordion accOnlineUsers;

    private final String[] roomNames = new String[]{"main", "ninjas", "memes", "gaming", "horses"};


    public Controller() {
        clientSwitch = new ClientSwitch(this);
    }

    @FXML
    public void initialize() {
        new Thread(clientSwitch::messageListener).start();
        loginPrompt();

        chatRooms = Collections.synchronizedMap(new HashMap<>());

        for (String roomName : roomNames) {
            chatRooms.put(roomName, new ChatRoom(roomName));
        }

        setActiveRoom("main");
    }

    public void sendBtnClick() {
        if (!input.getText().equals("")) {
            DataMessage dataMessage = new DataMessage(0, new Message(input.getText(), LocalDateTime.now(), user.getName(), getActiveRoom()));
            NetworkClient.getInstance().sendToServer(dataMessage);
            input.clear();
        }
    }

    private void loginPrompt() {
        //skapa ny stage och sätt lite egenskaper
        Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("Välj användarnamn");
        window.setMinWidth(300);
        window.setMinHeight(400);

        //skapa element och egenskaperna för innehållet
        Label errorMessageName = new Label();
        errorMessageName.setText("Du måste fylla i ett användarnamn");
        errorMessageName.setStyle("visibility: hidden");
        Label errorMessagePassword = new Label();
        errorMessagePassword.setText("Minst en liten bokstav \n Minst en stor bokstav \n Minst en siffra \n Inga blanka tecken \n Minst 5 tecken");
        errorMessagePassword.setStyle("visibility: hidden");

        TextField nameInput = new TextField();
        nameInput.setMaxWidth(200);
        PasswordField passwordInput = new PasswordField();
        passwordInput.setMaxWidth(200);

        Button okButton = new Button("Ok");
        Button newUser = new Button("Inte medlem? \n Tryck här!");
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
            String name = nameInput.getText().replaceAll("\\s+", "");

            if (name.equals("")) {
                errorMessageName.setStyle("visibility: visible;");
            } else if (!password.matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).{5,}$")) {
                errorMessageName.setStyle("visibility: visible;");
            } else if (!passwordCheck(name, password, errorMessagePassword)) {
            } else { // om användaren  fyllt i ett namn och lösen korrekt
                user.setName(name);
//                user.setPassword(password);
                window.close();
            }
        });

        //skapa en ny scen med innehållet och lägg upp och visa den
        Scene scene = new Scene(layout, 300, 300);
        window.setScene(scene);
        window.showAndWait();
//        inlogg.setText("Inloggad användare: " + user.getName());
    }

    public boolean passwordCheck(String userName, String password, Label errorMessagePassword) {
        DataMessage dataMessage = new DataMessage(3, new Message(password, LocalDateTime.now(), userName, null));
        return loginOrRegister(dataMessage, errorMessagePassword);
    }


    private boolean registerCheck(String userName, String password, Label errorMessagePassword) {
        DataMessage dataMessage = new DataMessage(2, new Message(password, LocalDateTime.now(), userName, null));
        return loginOrRegister(dataMessage, errorMessagePassword);
    }

    private boolean loginOrRegister(DataMessage msg, Label errorMessagePassword) {
        while (isServerWaiting()) {
        }
        NetworkClient.getInstance().sendToServer(msg);
        setServerWaiting(true);

        while (isServerWaiting()) {
        }
        if (getServerResponse()) {
            System.out.println("True");
            return true;
        } else {
            System.out.println("false");
            errorMessagePassword.setStyle("visibility: visible");
        }
        return false;
    }

    public void registerForm() {
        Label errorMessageName = new Label("Du måste fylla i ett användarnamn");
        errorMessageName.setStyle("visibility: hidden");
        Label nameLabel = new Label("Användarnamn");
        Label passwordLabel = new Label("Lösenord");

        Label errorMessagePassword = new Label("Minst en liten bokstav \n Minst en stor bokstav \n Minst en siffra \n Inga blanka tecken \n Minst 5 tecken");
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
            String name = nameInputRegistration.getText().replaceAll("\\s+", "");
            if (name.equals("")) {
                errorMessageName.setStyle("visibility: visible;");
            } else if (!password.matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).{5,}$")) {
                errorMessageName.setStyle("visibility: visible;");
            } else if (!registerCheck(name, password, errorMessagePassword)) { //Metod som kollar att lösen är korrekt
            } else { // om användaren  fyllt i ett namn och lösen korrekt
                registerWindow.close();
            }
        });
    }

    public void addMessageToRoom(Message msg) {
        getChatRoom(msg.getReceiver()).addMessage(msg);
    }

    private void printRoomMessages(String roomName) {
        getChatRoom(roomName).getMessages().forEach(this::printMessage);
    }

    public void printMessage(Message msg) {
        HBox chatMessageContainer = new HBox();
        Label message = new Label(msg.getSender() + "\n" + msg.getMessageData() + "\n" + msg.getTime().format(formatter));
        message.setMinHeight(Control.USE_PREF_SIZE);
        msgBox.getChildren().add(chatMessageContainer);
        styleMessage(message, chatMessageContainer);
        scroll();
    }

    public void styleMessage(Label message, HBox chatMessageContainer) {
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

    private void scroll() {
        msgBox.heightProperty().addListener(observable -> allMessagesWindow.setVvalue(1.0));
    }

    @FXML
    private void changeRoom(ActionEvent event) {
        String newRoom = ((Button) event.getSource()).getId();
        msgBox.getChildren().clear();
        printRoomMessages(newRoom);
        setActiveRoom(newRoom);
        NetworkClient.getInstance().sendToServer(new DataMessage(1, new Message(newRoom, null, user.getName(), null)));
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

    public boolean getServerResponse() {
        return serverResponse.get();
    }

    public void setServerResponse(boolean serverResponse) {
        this.serverResponse.set(serverResponse);
    }

    public boolean isServerWaiting() {
        return serverWaiting.get();
    }

    public void setServerWaiting(boolean serverWaiting) {
        this.serverWaiting.set(serverWaiting);
    }

    public void loadChatRoomUsers(Message msg) {
        String[] users = null;
        if (msg.getMessageData().length() >= 2) {
            users = msg.getMessageData().split(",");
        }
        if (users != null) {
            for (String user : users) {
                getChatRoom(msg.getReceiver()).addUser(user);
            }
        }
    }

    public void moveChatRoomUser(Message msg) {
        getChatRoom(msg.getReceiver()).removeUser(msg.getSender());
        getChatRoom(msg.getMessageData()).addUser(msg.getSender());
//        TEST
        System.out.println(getChatRoom(msg.getReceiver()).getUsers().size());
        System.out.println(getChatRoom(msg.getMessageData()).getUsers().size());
    }

    private ChatRoom getChatRoom(String roomName) {
        return chatRooms.get(roomName);
    }
}