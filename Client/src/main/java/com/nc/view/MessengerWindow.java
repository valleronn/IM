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
    private Label contactName;
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
    private List<String> messageList = new LinkedList<>();


    public void setUser(User user) {
        this.user = user;
        fullName.setText(user.getFullName());
    }

    public void setClientApp(ClientApp clientApp) {
        this.clientApp = clientApp;
        myContactsList.setItems(clientApp.getMyContacts());
    }

    @FXML
    private void initialize() {
        showChatDetails(null);
        myContactsList.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> showChatDetails(newValue)
        );
    }

    public void showChatDetails(User contactUser) {
        if (contactUser != null) {
            contactName.setText(contactUser.getFullName());
        } else {
            contactName.setText("");
            chatTextArea.setText("");
        }
    }

    @FXML
    private void sendMessageHandler() {
        messageList.add(inputMessageTextField.getText());
        String computedMessage = "";
        for (String message: messageList) {
            computedMessage += message + "\n";
        }
        chatTextArea.setText(computedMessage);

        inputMessageTextField.setText("");
    }

    @FXML
    private void addContactsHandler() {
        clientApp.addContactsDialog();
    }
}
