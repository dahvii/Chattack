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
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.w3c.dom.Text;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

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
    private Accordion accOnlineUsers;


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
        changeRoom(1);
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
    private void printMesseges(int roomNr) {
        //loopa igenom meddelanden i det aktuella chatrummet
        for (int counter = 0; counter < chatRooms.get(roomNr).getMessages().size(); counter++) {

            Message msg = chatRooms.get(roomNr).getMessages().get(counter);
            printMessage(msg);
        }
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


    @FXML
    private void changeRoom(int i) {
        System.out.println("changeroom metoden för rum nr"+i);
        msgBox.getChildren().clear();

        //loopa igenom meddelanden i det aktuella chatrummet
        for (int counter = 0; counter < chatRooms.get(i-1).getMessages().size(); counter++) {
            Message msg = chatRooms.get(i-1).getMessages().get(counter);

            Label message = new Label(msg.getSender() + "\n" + msg.getMessageData() + "\n" + new Timestamp(msg.getTime()));

            msgBox.getChildren().add(message);
        }
        /*

        System.out.println("1"+stackPane.getChildren());
        stackPane.getChildren().get(2).toFront();
        System.out.println("2"+stackPane.getChildren());


        ObservableList<Node> childs = this.stackPane.getChildren();

        if (childs.size() > 1) {

            Node topNode = childs.get(childs.size()-1);
            topNode.toFront();
        }
      */
    }

}




