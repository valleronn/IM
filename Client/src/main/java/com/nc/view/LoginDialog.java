package com.nc.view;

import com.nc.ClientApp;
import com.nc.controller.ClientController;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;

public class LoginDialog {
    @FXML
    private TextField loginField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Hyperlink clickHereLink;
    @FXML
    private Button loginButton;

    private ClientApp clientApp;
    private ClientController client;

    public LoginDialog() {
        client = new ClientController("localhost", 4444);
    }

    @FXML
    private void initialize() {

    }

    public void setClientApp(ClientApp clientApp) {
        this.clientApp = clientApp;
    }

    @FXML
    private void signInHandler() throws IOException {
        if (client.connect()) {
            boolean loginPassed = client.login(loginField.getText(), passwordField.getText());
            if (loginPassed) {
                System.out.println("Logged in successfully");
                clientApp.showMessengerWindow(clientApp.getAdmin());
            } else {
                System.out.println("User or password is incorrect");
            }
        } else {
            System.err.println("Failed to connect");
        }
    }

    private boolean isUserValid() {
        boolean result = false;
        String nickName = clientApp.getAdmin().getLogin();
        String password = clientApp.getAdmin().getPassword();
        if (loginField.getText().equals(nickName) || passwordField.getText().equals(password)) {
            result = true;
        } else {
            System.out.println("User or password is incorrect");
        }
        return result;
    }

    @FXML
    private void clickHereHandler() {
        clientApp.showRegisterDialog();
    }
}
