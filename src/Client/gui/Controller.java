package Client.gui;

import Client.ClientSwitch;
import Client.User;
import Data.DataMessage;
import Server.ChatRoom;
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
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;

public class Controller {

    private ClientSwitch clientSwitch;
    public Button sendBtn;
    public TextField input;
    public Label inlogg;
    public ScrollPane allMessagesWindow;
    private User user = new User();
    private String receiverName = "Jebidiah";
    public VBox msgBox;
    private ArrayList<ChatRoom> chatRooms = new ArrayList<>();


    public Controller(){
        clientSwitch = new ClientSwitch(this);
    }

    @FXML
    public void initialize(){
        promt();
        DataHandler.getInstance().loadMessages(user.getName());
        DataHandler.getInstance().getAllMessages().forEach(this::printMessage);
        new Thread(clientSwitch::messageListener).start();

        chatRooms.add(new ChatRoom("skitsnack"));
        chatRooms.add(new ChatRoom("Om Ninjas"));
        chatRooms.add(new ChatRoom("Fräsiga Memes"));
        chatRooms.add(new ChatRoom("spel"));
        chatRooms.add(new ChatRoom("Hästklubben"));

        printMesseges(0);
    }


    public void sendBtnClick(){
            if( !input.getText().equals("")){
                DataMessage dataMessage = new DataMessage(0, new Message(input.getText(), new Date().getTime(), user.getName(), receiverName));
                NetworkClient.getInstance().sendToServer(dataMessage);
                input.clear();
            }
    }


    private void promt(){
        //TODO: skapa scen istället för stage och lägg till i primarystage

        //skapa ny stage och sätt lite egenskaper
        Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("Välj användarnamn");
        window.setMinWidth(300);
        window.setMinHeight(400);

        //skapa element och egenskaperna för innehållet
        Label errorMessage= new Label();
        errorMessage.setText("Du måste fylla i ett användarnamn");
        errorMessage.setStyle("visibility: hidden");

        TextField nameInput= new TextField();
        TextField passwordInput= new TextField();

        Button okButton = new Button("Ok");
        Button newUser = new Button ("Inte meddlem? \n Tryck här!");

        Label userLabel = new Label();
        Label passwordLabel = new Label();
        Label welcomeLabel = new Label();
        welcomeLabel.setText("Välkommen till Chatack!");

        userLabel.setText("Användarnamn");
        passwordLabel.setText("Lösenord");

        //lägg till elementen till layouten
        VBox layout = new VBox(10);
        layout.getChildren().addAll(welcomeLabel, userLabel ,nameInput, passwordLabel, passwordInput,  okButton, errorMessage, newUser);
        layout.setAlignment(Pos.CENTER);
        window.setResizable(false);




        //skapa funktionalitet och eventhantering
        window.setOnCloseRequest(e -> {
            //TODO: nedan kommando gör vad vi vill användarmässigt men skapar massa exceptions - dvs inte vackert
            Platform.exit();
        });

        okButton.setDefaultButton(true);
        okButton.setOnAction(e -> {
            //om användaren inte har fyllt i ett namn
            //remove whitespaces
            String password = passwordInput.getText().replaceAll("\\s+", "");
            String name = nameInput.getText().replaceAll("\\s+","");
            if( name.equals("") & password.equals("")) {
                errorMessage.setStyle("visibility: visible;");
            } else{ // om användaren  fyllt i ett namn
                user.setName(name);
                user.setPassword(password);
                window.close();
            }

        });

        

        //skapa en ny scen med innehållet och lägg upp och visa den
        Scene scene = new Scene(layout);
        window.setScene(scene);
        window.showAndWait();
        //inlogg.setText("Inloggad användare: " + user.getName());

    }
    private void printMesseges(int roomNr) {
        //loopa igenom meddelanden i det aktuella chatrummet
        for (int counter = 0; counter < chatRooms.get(roomNr).getMessages().size(); counter++) {

            Message msg = chatRooms.get(roomNr).getMessages().get(counter);
            printMessage(msg);
        }
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

    public User getUser() {
        return user;
    }

    private void scroll(){
        msgBox.heightProperty().addListener(observable -> allMessagesWindow.setVvalue(1.0));
    }


    @FXML
    private void changeRoom(ActionEvent event) {
        Button button=(Button) event.getSource();
        int roomNr =Integer.parseInt(button.getId());

        msgBox.getChildren().clear();

        printMesseges(roomNr);
    }


}




