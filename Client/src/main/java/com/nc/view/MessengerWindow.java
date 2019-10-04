package com.nc.view;

import com.nc.ClientApp;
import com.nc.controller.ClientController;
import com.nc.controller.MessageListener;
import com.nc.controller.ServerListener;
import com.nc.controller.UserStatusListener;
import com.nc.model.users.ChatRoom;
import com.nc.model.users.User;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Side;
import javafx.scene.control.*;
import java.io.IOException;
import java.util.Iterator;
import java.util.Optional;

import org.apache.log4j.Logger;


/**
 * Represents MessengerWindow class
 */
public class MessengerWindow implements UserStatusListener,
                                MessageListener, ServerListener {

    private final static Logger LOGGER = Logger.getLogger(MessengerWindow.class);

    @FXML
    private Label fullName;
    @FXML
    private Button createNewChatButton;
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
        if (user.getLogin().equals("admin")) {
            createNewChatButton.setVisible(true);
        }
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
        this.client.addServerListener(this);
        this.client.setUser(user);
        client.messageReader();
        try {
            client.requestContacts();
            client.requestChats();
        } catch (IOException e) {
            LOGGER.error("Fails to request user data", e);
        }
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
            selectedContact = contactUser;
            makeChatElementsVisible(true);
            contactNameLabel.setText(contactUser.getLogin());
            messageProcessor(contactUser);
            if (!clientApp.getUsers().contains(contactUser) && !(contactUser instanceof ChatRoom)) {
                makeChatElementsVisible(false);
                contactNameLabel.setText(contactUser + " is currently offline");
            }
            if (!isChatRoom(contactUser)) {
                showChatDetailsMenuItem.setVisible(false);
            } else {
                showChatDetailsMenuItem.setVisible(true);
            }
            if (user.isBanned()) {
                makeChatElementsVisible(false);
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
        if (this.selectedContact != null && this.selectedContact.equals(selectedContact)) {
            chatTextArea.setText(computedMessage);
        }
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
            if (isChatRoom(selectedContact)) {
                selectedChat = (ChatRoom) myChatList.getSelectionModel().getSelectedItem();
                try {
                    client.leaveGroupChat(selectedChat.getChatName());
                    clientApp.getMyChatContacts().remove(selectedChat);
                    clientApp.getMyContacts().remove(selectedChat);
                } catch (IOException e) {
                    LOGGER.error("Fails to leave " + selectedChat.getChatName() + " group chat", e);
                }
            } else {
                try {
                    clientApp.getMyChatContacts().remove(selectedContact);
                    clientApp.getMyContacts().remove(selectedContact);
                    client.leaveSingleChat(selectedContact.getLogin());
                } catch (IOException e) {
                    LOGGER.error("Fails to leave chat with " + selectedContact.getLogin());
                }
            }
        } else {
            selectedContact = myContactsList.getSelectionModel().getSelectedItem();
            clientApp.getMyChatContacts().remove(selectedContact);
            clientApp.getMyContacts().remove(selectedContact);
        }
    }

    private boolean isChatRoom(User selectedChat) {
        return selectedChat.getLogin().startsWith("#");
    }

    /**
     * Open Chat Details dialog click event
     */
    @FXML
    public void showChatDetailsHandler() {
        if (chatsTab.isSelected()) {
            selectedContact = myChatList.getSelectionModel().getSelectedItem();
            if (isChatRoom(selectedContact)) {
                selectedChat = (ChatRoom) myChatList.getSelectionModel().getSelectedItem();
                clientApp.showChatDetailsDialog(user, null, selectedChat);
            } else {
                clientApp.showChatDetailsDialog(user, selectedContact, null);
            }
        } else {
            selectedContact = myContactsList.getSelectionModel().getSelectedItem();
            if (isChatRoom(selectedContact)) {
                selectedChat = (ChatRoom) myContactsList.getSelectionModel().getSelectedItem();
                clientApp.showChatDetailsDialog(user, null, selectedChat);
            } else {
                clientApp.showChatDetailsDialog(user, selectedContact, null);
            }
        }
    }

    /**
     * Removes user selection on Contacts tab if
     * Chats tab selected
     */
    @FXML
    public void chatsTabClickHandler() {
        if (myContactsList != null) {
            myContactsList.getSelectionModel().clearSelection();
        }
    }

    /**
     * Removes user selection on Chats tab if
     * Contacts tab selected
     */
    @FXML
    public void contactsTabClickHandler() {
        if (myChatList != null) {
            myChatList.getSelectionModel().clearSelection();
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
        if (!clientApp.getUsers().contains(newUser)) {
            clientApp.getUsers().add(newUser);
        }
        if (myContactsList.getItems().contains(newUser)) {
            selectUser();
        }
    }

    /**
     * Removes a user from a collection when offline
     * @param login User that goes online
     */
    @Override
    public void offline(String login) {
        Platform.runLater(() -> {
            Iterator<User> userIterator = clientApp.getUsers().iterator();
            while (userIterator.hasNext()) {
                User user = userIterator.next();
                if (user.getLogin().equals(login)) {
                    userIterator.remove();
                    if (myContactsList.getItems().contains(user)) {
                        selectUser();
                    }
                }
            }
        });
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
    public void onMessage(String from, String to, String body) {
        if (to.startsWith("#")) {
            for(User user: clientApp.getMyChatContacts()) {
                if (user.getLogin().equals(to)) {
                    user.getMessageList().add(from + ": " + body);
                    messageProcessor(user);
                }
            }
        } else {
            for(User user: clientApp.getMyChatContacts()) {
                if (user.getLogin().equals(from)) {
                    user.getMessageList().add(from + ": " + body);
                    messageProcessor(user);
                }
            }
        }
    }

    /**
     * Invites user to start a new chat
     * @param fromUser sender user
     */
    @Override
    public void invite(String fromUser, ObservableList<User> groupChatContacts) {
        if (fromUser.startsWith("#")) {
            Platform.runLater(() -> {
                String alertTitle = "Start " + fromUser + " group chat?";
                String alertContentText = "Would you like to join " + fromUser + " group chat?";
                if (showInvitationAlert(alertTitle, alertContentText)) {
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
                    clientApp.getMyChatContacts().add(contactUser);
                    tabs.getSelectionModel().select(contactsTab);
                    try {
                        client.joinSingleChat(fromUser);
                    } catch (IOException e) {
                        LOGGER.error("Fails to join chat with " + fromUser);
                    }
                }
            });
        }
    }

    /**
     * Shows chat invitation alert
     * @param alertTitle alert title
     * @param alertContentText alert content text
     * @return returns true or false
     */
    private boolean showInvitationAlert(String alertTitle, String alertContentText) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.initOwner(clientApp.getPrimaryStage());
        alert.setTitle(alertTitle);
        alert.setHeaderText(null);
        alert.setContentText(alertContentText);
        Optional<ButtonType> result = alert.showAndWait();
        return result.get() == ButtonType.OK;
    }

    /**
     * Removes user from chat
     * @param chatName chat name
     */
    @Override
    public void removeFromChat(String chatName) {
        Platform.runLater(() -> {
            String alertTitle = "Chat Removal";
            String alertContextText = "You have been removed from " + chatName;
            showInfoAlert(alertTitle, alertContextText);
            ObservableList<User> contacts = clientApp.getMyChatContacts();
            contacts.removeIf(contactUser -> (contactUser.getLogin().equals(chatName)));
        });
    }

    /**
     * Shows information alert
     * @param alertTitle alert title
     * @param alertContentText alert content text
     */
    private void showInfoAlert(String alertTitle, String alertContentText) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.initOwner(clientApp.getPrimaryStage());
        alert.setTitle(alertTitle);
        alert.setHeaderText(null);
        alert.setContentText(alertContentText);
        alert.showAndWait();
    }

    /**
     * Bans user
     */
    @Override
    public void banned() {
        Platform.runLater(() -> {
            String alertTitle = "You have been banned!";
            String alertContentText = "You have been banned!";
            showInfoAlert(alertTitle, alertContentText);
            user.setBanned(true);
            makeChatElementsVisible(false);
        });
    }

    /**
     * Unban user
     */
    @Override
    public void unbanned() {
        Platform.runLater(() -> {
            String alertTitle = "Ban removal";
            String alertContentText = "The ban has been removed";
            showInfoAlert(alertTitle, alertContentText);
            user.setBanned(false);
            makeChatElementsVisible(true);
        });
    }

    /**
     * Shows an alert when server goes offline
     */
    @Override
    public void serverOffline() {
        Platform.runLater(() -> {
            makeChatElementsVisible(false);
            String alertTitle = "Server is currently offline";
            contactNameLabel.setText(alertTitle);
            showInfoAlert(alertTitle, alertTitle);
        });
    }

    /**
     * Shows an alert when server goes back online
     */
    @Override
    public void serverOnline() {
        Platform.runLater(() -> {
            if (myChatList.getSelectionModel().isEmpty()
                    || myContactsList.getSelectionModel().isEmpty()) {
                contactNameLabel.setText("");
            } else {
                makeChatElementsVisible(true);
                contactNameLabel.setText(selectedContact.getLogin());
            }
            String alertTitle = "Server is back";
            showInfoAlert(alertTitle, alertTitle);
        });
        client.messageReader();
    }

    /**
     * Sets user's contacts
     * @param myContacts contacts to set
     */
    @Override
    public void setUserContacts(ObservableList<User> myContacts) {
        myContactsList.setItems(myContacts);
        clientApp.setMyContacts(myContacts);
        ObservableList<User> myChatContacts = FXCollections.observableArrayList();
        myChatContacts.addAll(myContacts);
        myChatList.setItems(myChatContacts);
        clientApp.setMyChatContacts(myChatContacts);
    }

    /**
     * Sets BanList
     * @param banList BanList to set
     */
    @Override
    public void setBanList(ObservableList<User> banList) {
        for(User user: clientApp.getMyChatContacts()) {
            if (isChatRoom(user)) {
                ChatRoom chatRoom = (ChatRoom) user;
                for(User groupChatContact: chatRoom.getUsers()) {
                    for(User bannedUser: banList) {
                        if (groupChatContact.getLogin().equals(bannedUser.getLogin())) {
                            groupChatContact.setBanned(true);
                        }
                    }
                }
            }
        }
    }

    /**
     * Sets user's group chats
     * @param myChats
     */
    @Override
    public void setUserChatRooms(ObservableList<ChatRoom> myChats) {
        Platform.runLater(() -> {
            for(ChatRoom chatRoom: myChats) {
                if (!clientApp.getMyChatContacts().contains(chatRoom)) {
                    clientApp.getMyChatContacts().add(chatRoom);
                }
            }
            myChatList.setItems(clientApp.getMyChatContacts());
            try {
                client.requestGroupChatContacts();
                if (user.getLogin().equals("admin")) {
                    client.requestBanList();
                }
            } catch (IOException e) {
                LOGGER.error("Fails to request group chat data", e);
            }
        });
    }

    /**
     * Sets group chat contacts
     * @param chatName chat name
     * @param groupChatContacts group chat contacts
     */
    @Override
    public void setGroupChatContacts(String chatName, ObservableList<User> groupChatContacts) {
        for(User user: clientApp.getMyChatContacts()) {
            if (isChatRoom(user) && user.getLogin().equals(chatName)) {
                ChatRoom chatRoom = (ChatRoom) user;
                chatRoom.setUsers(groupChatContacts);
            }
        }
    }
}
