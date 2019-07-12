package com.nc.view;

import com.nc.ClientApp;
import com.nc.controller.UserStatusListener;
import com.nc.model.users.User;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

public class AddContactsDialog {
    @FXML
    private ListView contactsList;
    private Stage dialogStage;
    private ClientApp clientApp;
    private boolean okClicked = false;

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void setClientApp(ClientApp clientApp) {
        this.clientApp = clientApp;
        contactsList.setItems(clientApp.getUsers());
    }

    @FXML
    private void initialize() {

    }

    public boolean isOkClicked() {
        return okClicked;
    }

    @FXML
    private void AddContact() {
            okClicked = true;
            ObservableList<User> selectedContacts =  contactsList.getSelectionModel().getSelectedItems();
            for (User contact: selectedContacts) {
                clientApp.getMyContacts().add(contact);
            }
            dialogStage.close();
    }

    @FXML
    private void cancel() {
        dialogStage.close();
    }
}
