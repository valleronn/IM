package com.nc.view;

import com.nc.ClientApp;
import com.nc.controller.ClientController;
import com.nc.model.users.User;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
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
    @FXML
    private Label warningLabel;
    private ClientApp clientApp;
    private ClientController client;
    private User user;

    public void setClientController(ClientController client) {
        this.client = client;
    }

    public void setClientApp(ClientApp clientApp) {
        this.clientApp = clientApp;
    }

    /**
     * SignIn button click event
     * @throws IOException throws IOException
     */
    @FXML
    private void signInHandler() throws IOException {
        if (isInputValid()) {
            if (client.connect()) {
                boolean loginPassed = client.login(loginField.getText(), passwordField.getText());
                if (loginPassed) {
                    System.out.println("Logged in successfully");
                    reCreateExistingUser();
                    clientApp.showMessengerWindow(user);
                } else {
                    warningLabel.setText("User or password is incorrect");
                    warningLabel.setVisible(true);
                }
            } else {
                warningLabel.setText("Failed to login, check your connection and try again.");
                warningLabel.setVisible(true);
            }
        } else {
            warningLabel.setText("Login or password can't be empty.");
            warningLabel.setVisible(true);
        }
    }

    /**
     * Checks input validation.
     * @return true or false
     */
    private boolean isInputValid() {
        boolean result = true;
        byte[] passwordFieldBytes = passwordField.getText().getBytes();
        if (passwordFieldBytes.length == 0 || loginField.getText().equals("")) {
            result = false;
        }
        return result;
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
