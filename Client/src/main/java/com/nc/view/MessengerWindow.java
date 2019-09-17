package com.nc.view;

import com.nc.ClientApp;
import com.nc.controller.ClientController;
import com.nc.controller.MessageListener;
import com.nc.controller.UserStatusListener;
import com.nc.model.users.ChatRoom;
import com.nc.model.users.User;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Side;
import javafx.scene.control.*;
import java.io.IOException;
import java.util.Optional;

import org.apache.log4j.Logger;


/**
 * Represents MessengerWindow class
 */
public class MessengerWindow implements UserStatusListener, MessageListener {

    private final static Logger LOGGER = Logger.getLogger(MessengerWindow.class);

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
    @FXML
    private Button editProfileButton;
    @FXML
    private ContextMenu editProfileContextMenu;
    @FXML
    private Button chatDetailsButton;
    @FXML
    private ContextMenu leaveChatContextMenu;
    @FXML
    private MenuItem showChatDetailsMenuItem;
    @FXML
    private TabPane tabs;
    @FXML
    private Tab chatsTab;
    @FXML
    private Tab contactsTab;
    private User user;
    private User selectedContact;
    private ChatRoom selectedChat;
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

    public void setFullName(String login) {
        this.fullName.setText(login);
    }

    /**
     * Specifies chat details during initialization
     */
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

    /**
     * Shows chat details
     * @param contactUser contactUser parameter
     */
    private void showChatDetails(User contactUser) {
        if (contactUser != null) {
            makeChatElementsVisible(true);
            contactNameLabel.setText(contactUser.getLogin());
            messageProcessor(contactUser);
            if (!clientApp.getUsers().contains(contactUser) && !(contactUser instanceof ChatRoom)) {
                makeChatElementsVisible(false);
                contactNameLabel.setText(contactUser + " is currently offline");
            }
            selectedContact = contactUser;
            if (!isChatRoom()) {
                showChatDetailsMenuItem.setVisible(false);
            } else {
                showChatDetailsMenuItem.setVisible(true);
            }
        } else {
            contactNameLabel.setText("");
            chatTextArea.setText("");
            makeChatElementsVisible(false);
        }
    }

    /**
     * Makes Input Message TextField, Chat TextArea,
     * Send message button visible or hidden
     * @param visible can be true or false
     */
    private void makeChatElementsVisible(boolean visible) {
        inputMessageTextField.setVisible(visible);
        chatTextArea.setVisible(visible);
        sendMessageButton.setVisible(visible);
        chatDetailsButton.setVisible(visible);
    }

    /**
     * Displays input text on ChatTextArea
     * @param selectedContact selectedContact parameter
     */
    private void messageProcessor(User selectedContact) {
        String computedMessage = "";
        for (String message: selectedContact.getMessageList()) {
            computedMessage += message + "\n";
        }
        chatTextArea.setText(computedMessage);
    }

    /**
     * Adds new user to a chat list.
     * @param contactedUser contactedUser parameter
     */
    private void addAUserToMyChatList(User contactedUser) {
        if (contactedUser.getMessageList() != null) {
            clientApp.getMyChatContacts().add(contactedUser);
        }
    }

    /**
     * Send button click event.
     * @param ae parameter
     */
    @FXML
    private void sendMessageHandler(ActionEvent ae) {
        User selectedContact = myContactsList.getSelectionModel().getSelectedItem();
        if (selectedContact == null) {
            selectedContact = myChatList.getSelectionModel().getSelectedItem();
        }
        selectedContact.getMessageList().add(inputMessageTextField.getText());
        messageProcessor(selectedContact);
        try {
            if (inputMessageTextField.getText() != null) {
                client.sendChatMessage(selectedContact.getLogin(), user.getLogin(), inputMessageTextField.getText());
            }
        } catch (IOException e) {
            LOGGER.error("Client.sendChatMessage error: ", e);
        }
        if (!clientApp.getMyChatContacts().contains(selectedContact)) {
            addAUserToMyChatList(selectedContact);
        }
        inputMessageTextField.setText("");
    }

    /**
     * Add contacts button click event
     */
    @FXML
    private void addContactsHandler() {
        clientApp.addContactsDialog(user);
    }

    /**
     * Add contacts to chat button click event
     */
    @FXML
    private void createNewChatHandler() throws IOException {
        ChatRoom chatRoom = new ChatRoom();
        boolean addClicked = clientApp.createNewChatDialog(chatRoom);
        if (addClicked) {
            clientApp.getMyChatContacts().add(chatRoom);
            client.joinGroupChat(chatRoom.getChatName());
            client.inviteUsersToGroupChat(chatRoom.getUsers(), chatRoom.getChatName());
        }
    }

    /**
     * Open context menu button click event
     */
    @FXML
    private void showEditProfileContextMenuHandler() {
        editProfileContextMenu.show(editProfileButton, Side.BOTTOM, 0, 0);
    }

    /**
     * Open chat details context menu button click event
     */
    @FXML
    private void openContextMenuHandler() {
        leaveChatContextMenu.show(chatDetailsButton, Side.BOTTOM, 0, 0);
    }


    /**
     * Open Edit Profile click event
     */
    @FXML
    private void editProfileHandler() {
        clientApp.showEditProfileDialog(user, this);
    }

    /**
     * Leave chat click event
     */
    @FXML
    private void leaveChatHandler() {
        if (chatsTab.isSelected()) {
            selectedContact = myChatList.getSelectionModel().getSelectedItem();
            if (isChatRoom()) {
                selectedChat = (ChatRoom) myChatList.getSelectionModel().getSelectedItem();
                try {
                    client.leaveGroupChat(selectedChat.getChatName());
                    clientApp.getMyChatContacts().remove(selectedChat);
                    clientApp.getMyContacts().remove(selectedChat);
                } catch (IOException e) {
                    LOGGER.error("Fails to leave " + selectedChat.getChatName() + " group chat", e);
                }
            } else {
                clientApp.getMyChatContacts().remove(selectedContact);
                clientApp.getMyContacts().remove(selectedContact);
            }
        } else {
            selectedContact = myContactsList.getSelectionModel().getSelectedItem();
            clientApp.getMyChatContacts().remove(selectedContact);
            clientApp.getMyContacts().remove(selectedContact);
        }
    }

    private boolean isChatRoom() {
        if (selectedContact.getLogin().startsWith("#")) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Open Chat Details dialog click event
     */
    @FXML
    public void showChatDetailsHandler() {
        if (chatsTab.isSelected()) {
            selectedContact = myChatList.getSelectionModel().getSelectedItem();
            if (isChatRoom()) {
                selectedChat = (ChatRoom) myChatList.getSelectionModel().getSelectedItem();
                clientApp.showChatDetailsDialog(user, null, selectedChat);
            } else {
                clientApp.showChatDetailsDialog(user, selectedContact, null);
            }
        } else {
            selectedContact = myContactsList.getSelectionModel().getSelectedItem();
            if (isChatRoom()) {
                selectedChat = (ChatRoom) myContactsList.getSelectionModel().getSelectedItem();
                clientApp.showChatDetailsDialog(user, null, selectedChat);
            } else {
                clientApp.showChatDetailsDialog(user, selectedContact, null);
            }
        }
    }

    /**
     * Adds new user to a collection when online
     * @param user User that goes online
     */
    @Override
    public void online(String user) {
        User newUser = new User();
        newUser.setLogin(user);
        clientApp.getUsers().add(newUser);
        if (myContactsList.getItems().contains(newUser)) {
            selectUser();
        }
    }

    /**
     * Removes a user from a collection when offline
     * @param user User that goes online
     */
    @Override
    public void offline(String user) {
        for (User u: clientApp.getUsers()) {
            if (u.getLogin().equals(user)) {
                clientApp.getUsers().remove(u);
                if (myContactsList.getItems().contains(u)) {
                    selectUser();
                }
            }
        }
    }

    /**
     * Clears list selection and choose previously selected user
     */
    private void selectUser() {
        int index = myContactsList.getSelectionModel().getSelectedIndex();
        Platform.runLater(() -> {
                myContactsList.getSelectionModel().clearSelection();
                myContactsList.getSelectionModel().select(index);
            }
        );
    }

    /**
     * Adds input message into user's message list
     * passes selected contact into messageProcessor
     * @param from sender
     * @param body message body
     */
    @Override
    public void onMessage(String from, String body) {
        User selectedContact = myContactsList.getSelectionModel().getSelectedItem();
        if (selectedContact == null) {
            selectedContact = myChatList.getSelectionModel().getSelectedItem();
        }
        if (selectedContact != null) {
            selectedContact.getMessageList().add(from + ": " + body);
            messageProcessor(selectedContact);
        }
    }

    /**
     * Invites user to start a new chat
     * @param fromUser sender user
     */
    public void invite(String fromUser, ObservableList<User> groupChatContacts) {
        if (fromUser.startsWith("#")) {
            Platform.runLater(() -> {
                String alertTitle = "Start " + fromUser + " group chat?";
                String alertContentText = "Would you like to join " + fromUser + " group chat?";
                if (showInvitationAlert(alertTitle, alertContentText)){
                    ChatRoom chatRoom = new ChatRoom();
                    chatRoom.setChatName(fromUser);
                    chatRoom.setUsers(groupChatContacts);
                    clientApp.getMyChatContacts().add(chatRoom);
                    tabs.getSelectionModel().select(chatsTab);
                    try {
                        client.joinGroupChat(chatRoom.getChatName());
                    } catch (IOException e) {
                        LOGGER.error("Fails to join " + chatRoom.getChatName() + " group chat", e);
                    }
                }
            });
        } else {
            Platform.runLater(() -> {
                String alertTitle = "Start a new chat with " + fromUser + "?";
                String alertContentText = "Would you like to start a new chat with  "
                        + fromUser + "?";
                if (showInvitationAlert(alertTitle, alertContentText)){
                    User contactUser = new User();
                    contactUser.setLogin(fromUser);
                    clientApp.getMyContacts().add(contactUser);
                    tabs.getSelectionModel().select(contactsTab);
                }
            });
        }
    }

    /**
     * Shows chat invitation alert
     * @param alertTitle
     * @param alertContentText
     * @return
     */
    private boolean showInvitationAlert(String alertTitle, String alertContentText) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.initOwner(clientApp.getPrimaryStage());
        alert.setTitle(alertTitle);
        alert.setHeaderText(null);
        alert.setContentText(alertContentText);
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            return true;
        } else {
            return false;
        }
    }
}
