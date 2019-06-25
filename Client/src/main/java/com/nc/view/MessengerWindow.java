package com.nc.view;

import com.nc.ClientApp;
import com.nc.model.users.User;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.LinkedList;
import java.util.List;

public class MessengerWindow {
    @FXML
    private Label fullName;
    @FXML
    private Label contactNameLabel;
    @FXML
    private TextArea chatTextArea;
    @FXML
    private TextField inputMessageTextField;
    @FXML
    private Button sendMessageButton;
    @FXML
    private ListView<User> myContactsList;
    @FXML
    private ListView<User> myChatList;
    private User user;
    private ClientApp clientApp;

    public void setUser(User user) {
        this.user = user;
        fullName.setText(user.getFullName());
    }

    public void setClientApp(ClientApp clientApp) {
        this.clientApp = clientApp;
        myContactsList.setItems(clientApp.getMyContacts());
        myChatList.setItems(clientApp.getMyChatContacts());
    }

    @FXML
    private void initialize() {
        showChatDetails(null);
        myContactsList.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> showChatDetails(newValue)
        );
        myChatList.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> showChatDetails(newValue)
        );
    }

    public void showChatDetails(User contactUser) {
        if (contactUser != null) {
            contactNameLabel.setText(contactUser.getFullName());
            messageProcessor(contactUser);
        } else {
            contactNameLabel.setText("");
            chatTextArea.setText("");
        }
    }

    private void messageProcessor(User selectedContact) {
        String computedMessage = "";
        for (String message: selectedContact.getMessageList()) {
            computedMessage += message + "\n";
        }
        chatTextArea.setText(computedMessage);
    }

    private void addAUserToMyChatList(User contactedUser) {
        if (contactedUser.getMessageList() != null) {
            clientApp.getMyChatContacts().add(contactedUser);
        }
    }

    @FXML
    private void sendMessageHandler() {
        User selectedContact = myContactsList.getSelectionModel().getSelectedItem();
        selectedContact.getMessageList().add(inputMessageTextField.getText());
        messageProcessor(selectedContact);
        if (!clientApp.getMyChatContacts().contains(selectedContact)) {
            addAUserToMyChatList(selectedContact);
        }
        inputMessageTextField.setText("");
    }

    @FXML
    private void addContactsHandler() {
        clientApp.addContactsDialog();
    }
}
