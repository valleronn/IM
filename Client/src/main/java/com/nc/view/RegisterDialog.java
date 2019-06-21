package com.nc.view;

import com.nc.ClientApp;
import com.nc.model.users.User;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.util.Date;

public class RegisterDialog {
    @FXML
    private TextField loginField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private PasswordField repeatPasswordField;

    private ClientApp clientApp;
    private User user;

    public void setClientApp(ClientApp clientApp) {
        this.clientApp = clientApp;
    }

    @FXML
    private void signUpHandler() {
        if (isInputValid()) {
            createNewUser();
            clientApp.showMessengerWindow(user);
        } else {
            System.out.println("Something went wrong");
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
        String fullName = "User 1";
        user = new User(2, nickName, fullName, pass, new Date());
    }
}
