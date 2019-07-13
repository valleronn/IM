package com.nc.view;

import com.nc.ClientApp;
import com.nc.controller.ClientController;
import com.nc.controller.MessageListener;
import com.nc.controller.UserStatusListener;
import com.nc.model.users.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.IOException;


public class MessengerWindow implements UserStatusListener, MessageListener {
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
    private ClientController client;

    public void setUser(User user) {
        this.user = user;
        fullName.setText(user.getLogin());
    }

    public void setClientApp(ClientApp clientApp) {
        this.clientApp = clientApp;
        myContactsList.setItems(clientApp.getMyContacts());
        myChatList.setItems(clientApp.getMyChatContacts());
    }

    public void setClientController(ClientController client) {
        this.client = client;
        this.client.addUserStatusListener(this);
        this.client.addMessageListener(this);
        client.messageReader();
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
            inputMessageTextField.setVisible(true);
            chatTextArea.setVisible(true);
            sendMessageButton.setVisible(true);
            contactNameLabel.setText(contactUser.getLogin());
            messageProcessor(contactUser);
        } else {
            contactNameLabel.setText("");
            chatTextArea.setText("");
            inputMessageTextField.setVisible(false);
            chatTextArea.setVisible(false);
            sendMessageButton.setVisible(false);
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
    private void sendMessageHandler(ActionEvent ae) {
        User selectedContact = myContactsList.getSelectionModel().getSelectedItem();
        selectedContact.getMessageList().add(inputMessageTextField.getText());
        messageProcessor(selectedContact);
        try {
            if (inputMessageTextField.getText() != null) {
                client.sendChatMessage(selectedContact.getLogin(), user.getLogin(), inputMessageTextField.getText());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (!clientApp.getMyChatContacts().contains(selectedContact)) {
            addAUserToMyChatList(selectedContact);
        }
        inputMessageTextField.setText("");
    }

    @FXML
    private void addContactsHandler() {
        clientApp.addContactsDialog();
    }

    @Override
    public void online(User user) {
        clientApp.getUsers().add(user);
    }

    @Override
    public void offline(User user) {
        clientApp.getUsers().remove(user);
    }

    @Override
    public void onMessage(String from, String body) {
        User selectedContact = myContactsList.getSelectionModel().getSelectedItem();
        if (selectedContact != null) {
            selectedContact.getMessageList().add(from + ": " + body);
            messageProcessor(selectedContact);
        }
    }
}
