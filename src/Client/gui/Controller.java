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
import javafx.scene.Node;
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
import java.util.Iterator;
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
    @FXML Accordion accOnlineUsers;
    @FXML TitledPane mainPane, ninjasPane, memesPane, gamingPane, horsesPane;
    private TitledPane[] onlinePanes;
    private Map<String, TitledPane> onlinePanesMap = new HashMap<>();
    @FXML VBox mainBox, ninjasBox, memesBox, gamingBox, horsesBox;
    private VBox[] onlineVBoxes;
    private Map<String, VBox> onlineVBoxMap = new HashMap<>();
    @FXML Button main, ninjas, memes, gaming, horses;
    private Button [] buttons;
    private final String[] roomNames = new String[]{"main", "ninjas", "memes", "gaming", "horses"};

    public Controller() {
        clientSwitch = new ClientSwitch(this);
    }

    @FXML
    public void initialize() {
        chatRooms = Collections.synchronizedMap(new HashMap<>());
        onlineVBoxes = new VBox[]{mainBox, ninjasBox, memesBox, gamingBox, horsesBox};
        onlinePanes = new TitledPane[]{mainPane, ninjasPane, memesPane, gamingPane, horsesPane};

        for (int i = 0; i<roomNames.length; i++){
            chatRooms.put(roomNames[i], new ChatRoom(roomNames[i]));
            onlineVBoxMap.put(roomNames[i], onlineVBoxes[i]);
            onlinePanesMap.put(roomNames[i], onlinePanes[i]);
        }

        buttons = new Button[]{main, ninjas, memes, gaming, horses};
        styleActiveButton(true, main);

        setActiveRoom("main");
        new Thread(clientSwitch::messageListener).start();
        loginPrompt();
        addOnlineUsersGraphic(getActiveRoom(), user.getName());
        accOnlineUsers.setExpandedPane(onlinePanesMap.get(getActiveRoom()));
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
        Label errorMessage = new Label();
        errorMessage.setText("  Felaktigt användarnamn eller lösenord\n(användaren kan redan vara uppkopplad)");
        errorMessage.setStyle("visibility: hidden");

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
                errorMessage,
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
            errorMessage.setStyle("visibility: hidden;");
            //om användaren inte har fyllt i ett namn
            //remove whitespaces
            String password = passwordInput.getText();
            String name = nameInput.getText().replaceAll("\\s+", "");

            if (name.equals("")) {
                errorMessage.setStyle("visibility: visible;");
            } else if (!passwordCheck(name, password, errorMessage)) {
            } else { // om användaren  fyllt i ett namn och lösen korrekt
                user.setName(name);
//                user.setPassword(password);
                window.close();
            }
        });

        //skapa en ny scen med innehållet och lägg upp och visa den
        Scene scene = new Scene(layout, 300, 400);
        window.setScene(scene);
        window.showAndWait();
//        inlogg.setText("Inloggad användare: " + user.getName());
    }

    public boolean passwordCheck(String userName, String password, Label errorMessage) {
        DataMessage dataMessage = new DataMessage(3, new Message(password, LocalDateTime.now(), userName, null));
        return loginOrRegister(dataMessage, errorMessage);
    }


    private boolean registerCheck(String userName, String password, Label errorMessage) {
        DataMessage dataMessage = new DataMessage(2, new Message(password, LocalDateTime.now(), userName, null));
        return loginOrRegister(dataMessage, errorMessage);
    }

    private boolean loginOrRegister(DataMessage msg, Label errorMessage){
        while (isServerWaiting()){}
        NetworkClient.getInstance().sendToServer(msg);
        setServerWaiting(true);

        while (isServerWaiting()) {
        }
        if (getServerResponse()) {
            return true;
        } else {
            errorMessage.setStyle("visibility: visible");
        }
        return false;
    }

    public void registerForm() {
        Label errorMessageName = new Label("Du måste fylla i ett användarnamn");
        errorMessageName.setStyle("visibility: hidden");
        Label nameLabel = new Label("Användarnamn");
        Label passwordLabel = new Label("Lösenord");

        Label errorMessagePassword = new Label("Lösenordet måste bestå av: \n Minst en liten bokstav \n Minst en stor bokstav \n Minst en siffra \n Inga blanka tecken \n Minst 5 tecken");
        errorMessagePassword.setStyle("visibility: hidden");

        Label errorMessageRegister = new Label("Användarnamnet är upptaget");
        errorMessageRegister.setStyle("visibility: hidden");

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
                errorMessageRegister,
                registerButton);
        Scene scene1 = new Scene(layout, 300, 300);
        layout.setAlignment(Pos.CENTER);

        registerWindow.setScene(scene1);
        registerWindow.show();

        registerButton.setDefaultButton(true);
        registerButton.setOnAction(e -> {
            errorMessageName.setStyle("visibility: hidden;");
            errorMessagePassword.setStyle("visibility: hidden;");
            errorMessageRegister.setStyle("visibility: hidden;");


            //getpasswordinput
            String password = passwordInputRegistration.getText();
            //get nameinput and remove whitespaces
            String name = nameInputRegistration.getText().replaceAll("\\s+", "");

            if (name.equals("")) {
                errorMessageName.setStyle("visibility: visible;");
            } else if(!password.matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).{5,}$")){
                errorMessagePassword.setStyle("visibility: visible;");
            } else if (!registerCheck(name, password, errorMessageRegister)) { //Metod som kollar att lösen är korrekt
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
        message.setMinWidth(450);
        msgBox.getChildren().add(chatMessageContainer);
        styleMessage(message, chatMessageContainer, msg);
        scroll();
    }

    public void styleMessage(Label message, HBox chatMessageContainer, Message msg) {
        DropShadow dropShadow = new DropShadow();
        dropShadow.setRadius(5.0);
        dropShadow.setOffsetX(3.0);
        dropShadow.setOffsetY(3.0);
        dropShadow.setColor(Color.color(0.4, 0.5, 0.5));
        message.setWrapText(true);
        message.setPadding(new Insets(5, 5, 5, 5));
        message.setStyle("-fx-background-color: linear-gradient(from 25% 25% to 100% 100%, #3ead3a, #93d379); -fx-background-radius: 5");
        if(msg.getSender().equals(user.getName())){
            message.setStyle("-fx-background-color: linear-gradient(from 25% 25% to 100% 100%, #525F73, #7a96aa); -fx-background-radius: 5");
            message.setTextFill(Color.WHITE);
        }
        chatMessageContainer.getChildren().add(message);
        chatMessageContainer.setMargin(message, new Insets(5, 5, 5, 5));
        chatMessageContainer.setEffect(dropShadow);
    }

    public void addOnlineUsersGraphic(String roomName, String name){
        Label label1 = new Label(name);
        label1.setId(name);
        try{
            onlineVBoxMap.get(roomName).getChildren().add(label1);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void scroll() {
        msgBox.heightProperty().addListener(observable -> allMessagesWindow.setVvalue(1.0));
    }

    @FXML
    private void changeRoom(ActionEvent event) {
        String newRoom = ((Button) event.getSource()).getId();
        msgBox.getChildren().clear();
        printRoomMessages(newRoom);

        //find the exited roomButton and style it back to default
        for(Button button : buttons){
            if (button.getId().equals(activeRoom)){
                styleActiveButton(false, button);
            }
        }
        setActiveRoom(newRoom);
        NetworkClient.getInstance().sendToServer(new DataMessage(1, new Message(newRoom, null, user.getName(), null)));
        //style the active roomButton
        styleActiveButton(true, ((Button) event.getSource()));

        //open accordion for new room
        accOnlineUsers.setExpandedPane(onlinePanesMap.get(newRoom));
    }

    private void styleActiveButton(Boolean isActive, Button button){
        if(isActive){
            button.setStyle("-fx-background-color:  #c3c4c4, linear-gradient(from 25% 25% to 100% 100%, #3ead3a, #93d379), radial-gradient(center 50% -40%, radius 200%, #e6e6e6 45%, rgba(230,230,230,0) 50%);");
        }else{
            button.setStyle(" -fx-background-color:  #c3c4c4, linear-gradient(#d6d6d6 50%, white 100%), radial-gradient(center 50% -40%, radius 200%, #e6e6e6 45%, rgba(230,230,230,0) 50%);");
        }
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
                addOnlineUsersGraphic(msg.getReceiver(), user);
            }

        }
    }

    public void moveChatRoomUser(Message msg) {
        getChatRoom(msg.getReceiver()).removeUser(msg.getSender());
        if(msg.getMessageData()!=null) getChatRoom(msg.getMessageData()).addUser(msg.getSender());

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                Label newLabel = new Label(msg.getSender());
                newLabel.setId(msg.getSender());
                Iterator<Node> onlineLabels = onlineVBoxMap.get(msg.getReceiver()).getChildren().iterator();
                while(onlineLabels.hasNext()){
                    String id = onlineLabels.next().getId();
                    if(id.equals(msg.getSender())) onlineLabels.remove();
                }
                if(msg.getMessageData()!= null) onlineVBoxMap.get(msg.getMessageData()).getChildren().add(newLabel);
            }
        });
    }

    private ChatRoom getChatRoom(String roomName) {
        return chatRooms.get(roomName);
    }
}