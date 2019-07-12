package com.nc.view;

import com.nc.ClientApp;
import com.nc.controller.ClientController;
import com.nc.model.users.User;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.util.Date;

public class RegisterDialog {
    @FXML
    private TextField loginField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private PasswordField repeatPasswordField;

    private ClientApp clientApp;
    private ClientController client;
    private User user;

    public void setClientApp(ClientApp clientApp) {
        this.clientApp = clientApp;
    }

    public void setClientController(ClientController client) {
        this.client = client;
    }

    @FXML
    private void signUpHandler() throws IOException {
        if (client.connect()) {
            boolean registerPassed = client.register(loginField.getText(), passwordField.getText());
            if (isInputValid() && registerPassed) {
                createNewUser();
                clientApp.showMessengerWindow(user);
            } else {
                System.out.println("Something went wrong");
            }
        } else {
            System.err.println("Failed to connect");
        }
    }

    private boolean isInputValid() {
        boolean result = false;
        if (passwordField.getText().equals(repeatPasswordField.getText())) {
            result = true;
        }
        return result;
    }

    private void createNewUser() {
        String nickName = loginField.getText();
        String pass = passwordField.getText();
        user = new User(nickName, pass, new Date());
        clientApp.getUsers().add(user);
    }
}
