package Client.gui;

import Client.NetworkClient;
import Data.DataMessage;
import Data.Message;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.time.LocalDateTime;

public class Login {
    private Controller controller;

    public Login(Controller controller){
        this.controller = controller;
    }

    public Stage getLoginStage(){
        //skapa ny stage och sätt lite egenskaper
        Stage loginStage = new Stage();
        loginStage.initModality(Modality.APPLICATION_MODAL);
        loginStage.setTitle("Välj användarnamn");
        loginStage.setMinWidth(300);
        loginStage.setMinHeight(400);

        //skapa element och egenskaperna för innehållet
        Label errorMessage = new Label();
        Label errorMessageNoInfo = new Label();
        errorMessage.setText("  Felaktigt användarnamn eller lösenord!\n(användaren kan redan vara uppkopplad)");
        errorMessageNoInfo.setText("Ange användarnamn och lösenord\nför att logga in!");
        errorMessage.setStyle("visibility: hidden");
        errorMessageNoInfo.setStyle("visibility: hidden");

        TextField nameInput = new TextField();
        nameInput.setMaxWidth(200);
        PasswordField passwordInput = new PasswordField();
        passwordInput.setMaxWidth(200);

        Button okButton = new Button("Ok");
        Button newUser = new Button("Inte medlem? \n Tryck här!");
        newUser.setOnAction(event -> registerForm(controller));

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
        loginStage.setResizable(false);

        //skapa funktionalitet och eventhantering
        loginStage.setOnCloseRequest(e -> {
            Platform.exit();
        });

        okButton.setDefaultButton(true);
        okButton.setOnAction(e -> {
            errorMessage.setStyle("visibility: hidden;");
            //om användaren inte har fyllt i ett namn
            //remove whitespaces
            String password = passwordInput.getText();
            String name = nameInput.getText().replaceAll("\\s+", "");

            if (name.equals("") || password.equals("")) {
                errorMessageNoInfo.setStyle("visibility: visible;");
            } else if(!loginOrRegister(3, new Message(password, LocalDateTime.now(), name, null))) {
                errorMessage.setStyle("visibility: visible;");
            } else{ // om användaren  fyllt i ett namn och lösen korrekt
                controller.activateMainProgram(name);
                loginStage.close();
            }
        });

        //skapa en ny scen med innehållet och lägg upp och visa den
        Scene scene = new Scene(layout, 300, 400);
        loginStage.setScene(scene);
        return loginStage;
    }

    private void registerForm(Controller controller){
        Label errorMessageName = new Label("Du måste fylla i ett användarnamn");
        errorMessageName.setStyle("visibility: hidden");
        Label nameLabel = new Label("Användarnamn");
        Label passwordLabel = new Label("Lösenord");

        Label errorMessagePassword = new Label("Lösenordet måste bestå av: \n Minst en liten bokstav \n Minst en stor bokstav \n Minst en siffra \n Inga blanka tecken \n Minst 5 tecken");
        errorMessagePassword.setStyle("visibility: hidden");

        Label errorMessageRegister = new Label("Användarnamnet är upptaget");
        errorMessageRegister.setStyle("visibility: hidden");

        Stage registerStage = new Stage();
        registerStage.initModality(Modality.APPLICATION_MODAL);
        registerStage.setTitle("Ny användare");
        registerStage.setResizable(false);
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
        Scene registerScene = new Scene(layout, 300, 350);
        layout.setAlignment(Pos.CENTER);

        registerStage.setScene(registerScene);
        registerStage.show();

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
            } else if (!loginOrRegister(2, new Message(password, LocalDateTime.now(), name, null))) {
                errorMessageRegister.setStyle("visibility: visible;");
            } else {
                registerStage.close();
            }
        });
    }

    public boolean loginOrRegister(int command, Message msg){
        while (controller.isServerWaiting()){}
        NetworkClient.getInstance().sendToServer(new DataMessage(command, msg));
        controller.setServerWaiting(true);
        while (controller.isServerWaiting()) {}
        return controller.getServerResponse();
    }
}
