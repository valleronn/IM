package com.nc.view;

import com.nc.controller.ClientController;
import javafx.fxml.FXML;
import com.nc.model.users.User;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class EditProfileDialog {
    @FXML
    private TextField loginField;
    @FXML
    private TextField passwordField;
    @FXML
    private Label warningLabel;
    private ClientController client;
    private User user;
    private Stage dialogStage;
    private MessengerWindow parentWindow;
    private boolean saveClicked = false;

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void setParentWindow(MessengerWindow parentWindow) {
        this.parentWindow = parentWindow;
    }

    public void setClientController(ClientController client) {
        this.client = client;
    }

    public void setUser(User user) {
        this.user = user;
        loginField.setText(user.getLogin());
        passwordField.setText(user.getPassword());
    }

    /**
     * Returns true if Add clicked, otherwise false
     * @return
     */
    public boolean isSaveClicked() {
        return saveClicked;
    }

    /**
     * Save profile click event
     */
    @FXML
    public void saveProfile() throws IOException, InterruptedException {
        if (isInputValid()) {
            client.setProfileUpdated(false);
            client.updateProfile(user.getLogin(), loginField.getText(), passwordField.getText());
            Thread.sleep(200);
            if (client.isProfileUpdated()) {
                user.setLogin(loginField.getText());
                user.setPassword(passwordField.getText());
                saveClicked = true;
                dialogStage.close();
                parentWindow.setFullName(user.getLogin());
            } else {
                warningLabel.setText("Login or password appear to be in use");
                warningLabel.setVisible(true);
            }
        } else {
            warningLabel.setText("Login or password can't be empty");
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
     * Cancel button click event.
     */
    @FXML
    private void cancel() {
        dialogStage.close();
    }
}
