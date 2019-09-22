package com.nc.view;

import com.nc.ClientApp;
import com.nc.controller.ClientController;
import com.nc.model.users.ChatRoom;
import com.nc.model.users.User;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

import java.io.IOException;

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
    @FXML
    private Button addButton;
    @FXML
    private Button removeButton;
    @FXML
    private Button banButton;
    @FXML
    private Button unBanButton;
    private Stage dialogStage;
    private ClientApp clientApp;
    private User user;
    private User chatContact;
    private ChatRoom chatRoom;
    private ClientController client;

    public void setUser(User user) {
        this.user = user;
        hideAdminButtonsIfNotAdmin();
    }

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

    private void hideAdminButtonsIfNotAdmin() {
        if (!user.getLogin().equals("admin")) {
            addButton.setVisible(false);
            removeButton.setVisible(false);
            banButton.setVisible(false);
            unBanButton.setVisible(false);
        }
    }

    /**
     * Adds a user to a group chat click event.
     */
    @FXML
    public void addUserToGroupChatHandler() throws IOException {
        if (chatRoom != null) {
            boolean addClicked = clientApp.createNewChatDialog(chatRoom);
            if (addClicked) {
                client.inviteUsersToGroupChat(chatRoom.getUsers(), chatRoom.getChatName());
            }
        }
    }

    /**
     * Removes a user from a group chat click event.
     */
    @FXML
    public void removeUserFromGroupChatHandler() throws IOException {
        User user = chatList.getSelectionModel().getSelectedItem();
        if (user != null) {
            chatList.getItems().remove(user);
            client.removeUserFromGroupChat(chatRoom.getChatName(), user.getLogin());
        } else {
            warningLabel.setVisible(true);
            warningLabel.setText("Please select a user to remove");
        }
    }

    /**
     * Adds selected contact to a ban list
     */
    @FXML
    public void banUserHandler() throws IOException {
        User user = chatList.getSelectionModel().getSelectedItem();
        if (user != null) {
            banLabel.setVisible(true);
            banLabel.setText("Banned");
            client.banUser(user.getLogin());
        } else {
            warningLabel.setVisible(true);
            warningLabel.setText("Please select a user to add to the ban list");
        }
    }

    /**
     * Removes selected contact from the ban list
     */
    @FXML
    public void unBanUserHandler() throws IOException {
        User user = chatList.getSelectionModel().getSelectedItem();
        if (user != null) {
            banLabel.setVisible(false);
            client.unBanUser(user.getLogin());
        } else {
            warningLabel.setVisible(true);
            warningLabel.setText("Please select a user to add to the ban list");
        }
    }

    /**
     * Close button click event
     */
    @FXML
    public void closeDialogHandler() {
        dialogStage.close();
    }
}
