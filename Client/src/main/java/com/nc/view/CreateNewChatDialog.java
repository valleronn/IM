package com.nc.view;

import com.nc.ClientApp;
import com.nc.model.users.ChatRoom;
import com.nc.model.users.User;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.controlsfx.control.CheckListView;

/**
 * Represents CreateNewChatDialog class
 */
public class CreateNewChatDialog {
    @FXML
    private CheckListView contactsList;
    @FXML
    private TextField chatNameTextField;
    @FXML
    private Label warningLabel;
    private Stage dialogStage;
    private ClientApp clientApp;
    private ChatRoom chatRoom;
    private boolean addClicked = false;

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void setClientApp(ClientApp clientApp) {
        this.clientApp = clientApp;
        contactsList.setItems(clientApp.getMyContacts());
        if (chatRoom != null) {
            chatNameTextField.setText(chatRoom.getChatName());
        }
    }

    public void setChatRoom(ChatRoom chatRoom) {
        this.chatRoom = chatRoom;
    }

    /**
     * Returns true if Add clicked, otherwise false
     * @return returns addClicked
     */
    public boolean isAddClicked() {
        return addClicked;
    }

    /**
     * Add button click event
     */
    @FXML
    private void addContactsToChat() {
        if (isInputValid()) {
            addClicked = true;
            ObservableList<User> selectedContacts =  contactsList.getCheckModel().getCheckedItems();
            if (chatRoom.getChatName() == null) {
                chatRoom.setChatName("#" + chatNameTextField.getText());
            }
            for (User contact: selectedContacts) {
                chatRoom.getUsers().add(contact);
            }
            dialogStage.close();
        } else {
            warningLabel.setVisible(true);
        }
    }

    /**
     * Checks input validation.
     * @return true or false
     */
    private boolean isInputValid() {
        boolean result = true;
        if (chatNameTextField.getText().equals("")
                || contactsList.getCheckModel().getCheckedItems() == null) {
            result = false;
        }
        return result;
    }

    /**
     * Cancel button click event
     */
    @FXML
    private void cancel() {
        dialogStage.close();
    }

}
