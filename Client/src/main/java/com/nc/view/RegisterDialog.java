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
 * Represent RegisterDialog class.
 */
public class RegisterDialog {
    @FXML
    private TextField loginField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private PasswordField repeatPasswordField;
    @FXML
    private Label warningLabel;

    private ClientApp clientApp;
    private ClientController client;
    private User user;

    public void setClientApp(ClientApp clientApp) {
        this.clientApp = clientApp;
    }

    public void setClientController(ClientController client) {
        this.client = client;
    }

    /**
     * SignUp button click event.
     * @throws IOException
     */
    @FXML
    private void signUpHandler() throws IOException {
        if (isInputValid()) {
            if (client.connect()) {
                boolean registerPassed = client.register(loginField.getText(), passwordField.getText());
                if (registerPassed) {
                    createNewUser();
                    clientApp.showMessengerWindow(user);
                } else {
                    warningLabel.setText("Failed to register, login already in use.");
                    warningLabel.setVisible(true);
                }
            } else {
                warningLabel.setText("Failed to register, check your connection and try again.");
                warningLabel.setVisible(true);
            }
        } else {
            warningLabel.setText("Entered password does not match.");
            warningLabel.setVisible(true);
        }
    }

    /**
     * Checks input validation.
     * @return true or false
     */
    private boolean isInputValid() {
        boolean result = false;
        if (passwordField.getText().equals(repeatPasswordField.getText())) {
            result = true;
        }
        return result;
    }

    /**
     * Creates a new user.
     */
    private void createNewUser() {
        String nickName = loginField.getText();
        String pass = passwordField.getText();
        user = new User(nickName, pass, new Date());
    }
}
