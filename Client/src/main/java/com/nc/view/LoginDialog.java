package com.nc.view;

import com.nc.ClientApp;
import com.nc.model.users.User;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

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

    public LoginDialog() {

    }

    @FXML
    private void initialize() {

    }

    public void setClientApp(ClientApp clientApp) {
        this.clientApp = clientApp;
    }

    @FXML
    private void signInHandler() {
        if (isUserValid()) {
            System.out.println("Logged in successfully");
            clientApp.showMessengerWindow(clientApp.getAdmin());
        }
    }

    private boolean isUserValid() {
        boolean result = false;
        String nickName = clientApp.getAdmin().getNickName();
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
