package com.nc.view;

import com.nc.model.users.User;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class MessengerWindow {
    @FXML
    private Label fullName;
    private User user;


    public void setUser(User user) {
        this.user = user;
        fullName.setText(user.getFullName());
    }

    @FXML
    private void initialize() {

    }

}
