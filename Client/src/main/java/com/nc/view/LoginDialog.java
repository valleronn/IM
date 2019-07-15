package com.nc.view;

import com.nc.ClientApp;
import com.nc.controller.ClientController;
import com.nc.model.users.User;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import java.io.IOException;
import java.util.Date;

/**
 * Represents LoginDialog class.
 */
public class LoginDialog {
    @FXML
    private TextField loginField;
    @FXML
    private PasswordField passwordField;
    private ClientApp clientApp;
    private ClientController client;
    private User user;

    public void setClientController(ClientController client) {
        this.client = client;
    }

    @FXML
    private void initialize() {

    }

    public void setClientApp(ClientApp clientApp) {
        this.clientApp = clientApp;
    }

    /**
     * SignIn button click event
     * @throws IOException
     */
    @FXML
    private void signInHandler() throws IOException {
        if (client.connect()) {
            boolean loginPassed = client.login(loginField.getText(), passwordField.getText());
            if (loginPassed) {
                System.out.println("Logged in successfully");
                reCreateExistingUser();
                clientApp.showMessengerWindow(user);
            } else {
                System.out.println("User or password is incorrect");
            }
        } else {
            System.err.println("Failed to connect");
        }
    }

    /**
     * ClickHere link click event
     */
    @FXML
    private void clickHereHandler() {
        clientApp.showRegisterDialog();
    }

    /**
     * Recreates existing user
     */
    private void reCreateExistingUser() {
        String nickName = loginField.getText();
        String pass = passwordField.getText();
        user = new User(nickName, pass, new Date());
    }
}
