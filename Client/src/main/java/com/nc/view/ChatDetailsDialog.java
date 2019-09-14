package com.nc.view;

import com.nc.ClientApp;
import com.nc.controller.ClientController;
import com.nc.model.users.ChatRoom;
import com.nc.model.users.User;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.stage.Stage;


public class ChatDetailsDialog {
    @FXML
    private ListView<User> chatList;
    @FXML
    private Label statusLabel;
    @FXML
    private Label onlineLabel;
    @FXML
    private Label banLabel;
    @FXML
    private Label warningLabel;
    private Stage dialogStage;
    private ClientApp clientApp;
    private User chatContact;
    private ChatRoom chatRoom;
    private ClientController client;

    public void setChatContact(User chatContact) {
        this.chatContact = chatContact;
    }

    public void setChatRoom(ChatRoom chatRoom) {
        this.chatRoom = chatRoom;
        chatList.setItems(chatRoom.getUsers());

    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void setClientApp(ClientApp clientApp) {
        this.clientApp = clientApp;
    }

    public void setClientController(ClientController client) {
        this.client = client;
    }

    @FXML
    private void initialize() {
        showUserDetails(null);
        chatList.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> showUserDetails(newValue)
        );
    }

    /**
     * Shows user details
     * @param contactUser contactUser parameter
     */
    private void showUserDetails(User contactUser) {
        if (contactUser != null) {
            statusLabel.setVisible(true);
            onlineLabel.setText("Online");
        } else {
            statusLabel.setText("");
            onlineLabel.setText("");
            banLabel.setText("");
        }
    }

    /**
     * Adds a user to a group chat click event.
     */
    @FXML
    public void addUserToGroupChatHandler() {
        if (chatRoom != null) {
            clientApp.createNewChatDialog(chatRoom);
        }
    }

    /**
     * Removes a user from a group chat click event.
     */
    @FXML
    public void removeUserFromGroupChatHandler() {
        User user = chatList.getSelectionModel().getSelectedItem();
        if (user != null) {
            chatList.getItems().remove(user);
        } else {
            warningLabel.setVisible(true);
            warningLabel.setText("Please select a user to remove");
        }
    }

    @FXML
    public void banUserHandler() {

    }

    @FXML
    public void unBanUserHandler() {

    }

    /**
     * Close button click event
     */
    @FXML
    public void closeDialogHandler() {
        dialogStage.close();
    }



}
