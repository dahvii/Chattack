package Client.gui;

import Client.ClientSwitch;
import Data.User;
import Data.DataMessage;
import Client.ChatRoom;
import Data.Message;
import Client.NetworkClient;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;

public class Controller {
    private ClientSwitch clientSwitch;
    private Login login;
    private GenerateGraphics generateGFX;
    private AtomicBoolean serverResponse = new AtomicBoolean(false);
    private AtomicBoolean serverWaiting = new AtomicBoolean(true);
    private AtomicBoolean loggedIn = new AtomicBoolean(false);

    private User user = new User();
    private final String[] roomNames = new String[]{"main", "ninjas", "memes", "gaming", "horses"};
    private Map<String, ChatRoom> chatRoomMap = Collections.synchronizedMap(new HashMap<>());
    private String activeRoom;

    public TextArea inputTextArea;
    public ScrollPane allMessagesWindow;
    @FXML VBox msgBox;
    @FXML Accordion accOnlineUsers;
    @FXML TitledPane mainPane, ninjasPane, memesPane, gamingPane, horsesPane;
    private Map<String, TitledPane> onlinePanesMap = new HashMap<>();
    @FXML VBox mainBox, ninjasBox, memesBox, gamingBox, horsesBox;
    private Map<String, VBox> onlineVBoxMap = new HashMap<>();
    @FXML Button mainButton, ninjasButton, memesButton, gamingButton, horsesButton;
    private Map<String, Button> buttonMap = new HashMap<>();
    public Label userName;

    public Controller() {
        clientSwitch = new ClientSwitch(this);
        generateGFX = new GenerateGraphics(this);
        login = new Login(this);
    }

    @FXML
    public void initialize() {
        VBox[] onlineVBoxes = new VBox[]{mainBox, ninjasBox, memesBox, gamingBox, horsesBox};
        TitledPane[] onlinePanes = new TitledPane[]{mainPane, ninjasPane, memesPane, gamingPane, horsesPane};
        Button[] buttons = new Button[]{mainButton, ninjasButton, memesButton, gamingButton, horsesButton};

        Stream.of(roomNames).parallel().forEach(name -> chatRoomMap.put(name, new ChatRoom(name)));
        Stream.of(onlineVBoxes).parallel().forEach(vBox ->
                onlineVBoxMap.put(vBox.getId().replaceFirst("Box", ""), vBox));
        Stream.of(onlinePanes).parallel().forEach(titledPane ->
                onlinePanesMap.put(titledPane.getId().replaceFirst("Pane", ""), titledPane));
        Stream.of(buttons).parallel().forEach(button ->
                buttonMap.put(button.getId().replaceFirst("Button", ""), button));

        inputTextArea.setOnKeyPressed(event -> {
            if(event.getCode() == KeyCode.ENTER){
                event.consume();
                if(event.isShiftDown()){
                    inputTextArea.appendText(System.getProperty("line.separator"));
                }   else {
                    sendBtnClick();
                }
            }
        });
    }

    public void startLogin() {
        new Thread(clientSwitch::messageListener).start();
        login.getLoginStage().showAndWait();
    }

    public void activateMainProgram(String name){
        getUser().setName(name);
        setActiveRoom("main");
        generateGFX.styleButton(true, buttonMap.get(getActiveRoom()));
        addOnlineUser(getActiveRoom(), user.getName());
        accOnlineUsers.setExpandedPane(onlinePanesMap.get(getActiveRoom()));
        userName.setText(user.getName());
        setLoggedIn(true);
    }

    public void sendBtnClick() {
        String msg = inputTextArea.getText().trim();
        if (!msg.equals("")) {
            if(msg.length()>2000) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("För många tecken!");
                alert.setHeaderText("Max 2000 tecken i ett meddelande!");
                alert.showAndWait();
            } else {
                DataMessage dataMessage =
                        new DataMessage(0, new Message(msg, LocalDateTime.now(), user.getName(), getActiveRoom()));
                NetworkClient.getInstance().sendToServer(dataMessage);
                inputTextArea.clear();
            }
        }
    }

    public void addMessageToRoom(Message msg) {
        getChatRoom(msg.getReceiver()).addMessage(msg);
    }

    public void printMessage(Message msg) {
        Platform.runLater(() -> {
            msgBox.getChildren().add(generateGFX.generateMessageBox(msg));
            scroll();
        });
    }

    private void scroll() {
        msgBox.heightProperty().addListener(observable -> allMessagesWindow.setVvalue(1.0));
    }

    @FXML
    private void changeRoom(ActionEvent event) {
        generateGFX.styleButton(false, buttonMap.get(getActiveRoom()));
        setActiveRoom(((Button) event.getSource()).getId().replaceFirst("Button",""));
        NetworkClient.getInstance().sendToServer(
                new DataMessage(1, new Message(getActiveRoom(), null, user.getName(), null)));
        generateGFX.styleButton(true, buttonMap.get(getActiveRoom()));
        msgBox.getChildren().clear();
        getChatRoom(getActiveRoom()).getMessages().forEach(Controller.this::printMessage);
        accOnlineUsers.setExpandedPane(onlinePanesMap.get(getActiveRoom()));
    }

    public void loadChatRoomUsers(Message msg) {
        if (msg.getMessageData().length() >= 2) {
            Arrays.stream(msg.getMessageData().split(",")).parallel().forEach(user -> {
                getChatRoom(msg.getReceiver()).addUser(user);
                addOnlineUser(msg.getReceiver(), user);
            });
        }
    }

    private void addOnlineUser(String roomName, String name){
        Platform.runLater(() ->
            onlineVBoxMap.get(roomName).getChildren().add(generateGFX.generateOnlineUserBox(name)));
    }

    public void moveChatRoomUser(Message msg) {
        Platform.runLater(() -> {
            getChatRoom(msg.getReceiver()).removeUser(msg.getSender());
            onlineVBoxMap.get(msg.getReceiver()).getChildren()
                    .removeIf(vBox -> vBox.getId().equals(msg.getSender()));
            if(msg.getMessageData()!= null) {
                getChatRoom(msg.getMessageData()).addUser(msg.getSender());
                onlineVBoxMap.get(msg.getMessageData()).getChildren().add(
                        generateGFX.generateOnlineUserBox(msg.getSender()));
            }
        });
    }

    public synchronized String getActiveRoom() {
        return activeRoom;
    }

    public synchronized void setActiveRoom(String activeRoom) {
        this.activeRoom = activeRoom;
    }

    public boolean isLoggedIn() {
        return loggedIn.get();
    }

    public void setLoggedIn(boolean loggedIn) {
        this.loggedIn.set(loggedIn);
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

    private ChatRoom getChatRoom(String roomName) {
        return chatRoomMap.get(roomName);
    }

    public User getUser(){
        return user;
    }
}