package com.nc.view;

import com.nc.ClientApp;
import com.nc.controller.ClientController;
import com.nc.model.users.User;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Represents AddContactsDialog class
 */
public class AddContactsDialog {
    @FXML
    private ListView contactsList;
    private Stage dialogStage;
    private ClientApp clientApp;
    private ClientController client;
    private User user;
    private boolean okClicked = false;

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void setClientApp(ClientApp clientApp) {
        this.clientApp = clientApp;
        contactsList.setItems(clientApp.getUsers());
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setClientController(ClientController client) {
        this.client = client;
    }

    @FXML
    private void initialize() {

    }

    /**
     * Returns true if Add clicked, otherwise false
     * @return
     */
    public boolean isOkClicked() {
        return okClicked;
    }

    /**
     * Add button click event
     */
    @FXML
    private void addContact() throws IOException {
            okClicked = true;
            ObservableList<User> selectedContacts =  contactsList.getSelectionModel().getSelectedItems();
            for (User contact: selectedContacts) {
                clientApp.getMyContacts().add(contact);
                client.inviteUserToChat(contact.getLogin(), user.getLogin());
            }
            dialogStage.close();
    }

    /**
     * Cancel button click event
     */
    @FXML
    private void cancel() {
        dialogStage.close();
    }
}
