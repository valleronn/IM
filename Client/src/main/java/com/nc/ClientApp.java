package com.nc;

import com.nc.controller.ClientController;
import com.nc.controller.ConfigReader;
import com.nc.model.users.ChatRoom;
import com.nc.model.users.User;
import com.nc.view.*;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.IOException;
import org.apache.log4j.Logger;

/**
 * Client main class of Messenger application.
 */
public class ClientApp extends Application {

    private static final Logger LOGGER = Logger.getLogger(ClientApp.class);
    private static final String LOGIN_DIALOG = "view/LoginDialog.fxml";
    private static final String REGISTER_DIALOG = "view/RegisterDialog.fxml";
    private static final String MESSENGER_WINDOW = "view/MessengerWindow.fxml";
    private static final String ADD_CONTACTS_DIALOG = "view/AddContactsDialog.fxml";
    private static final String CREATE_NEW_CHAT_DIALOG = "view/CreateNewChatDialog.fxml";
    private static final String EDIT_PROFILE_DIALOG = "view/EditProfileDialog.fxml";
    private static final String CHAT_DETAILS_DIALOG = "view/ChatDetailsDialog.fxml";

    private Stage primaryStage;
    private ObservableList<User> users = FXCollections.observableArrayList();
    private ObservableList<User> myContacts = FXCollections.observableArrayList();
    private ObservableList<User> myChatContacts = FXCollections.observableArrayList();

    private String serverName;
    private int port;
    private ClientController client;

    public ClientApp() {
        ConfigReader configReader = new ConfigReader();
        this.serverName = configReader.getIp();
        this.port = configReader.getPort();
        client = new ClientController(serverName, port);
    }

    public ObservableList<User> getUsers() {
        return users;
    }

    public ObservableList<User> getMyContacts() {
        return myContacts;
    }

    public ObservableList<User> getMyChatContacts() {
        return myChatContacts;
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Messenger");
        showLoginDialog();
        primaryStage.setOnCloseRequest(event -> {
            try {
                client.logoff();
            } catch (IOException e) {
                LOGGER.error("Client start error: ", e);
            } finally {
                System.exit(0);
            }
        });
    }

    /**
     * Initializes Login dialog.
     */
    private void showLoginDialog() {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(ClientApp.class.getResource(LOGIN_DIALOG));
        try {
            AnchorPane loginDialog = loader.load();
            Scene scene = new Scene(loginDialog);
            primaryStage.setScene(scene);
            primaryStage.show();
            LoginDialog controller = loader.getController();
            controller.setClientApp(this);
            controller.setClientController(client);
        } catch (IOException e) {
            LOGGER.error("Show login dialog error: ", e);
        }
    }

    /**
     * Initializes Register dialog.
     */
    public void showRegisterDialog() {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(ClientApp.class.getResource(REGISTER_DIALOG));
        try {
            AnchorPane registerDialog = loader.load();
            Scene scene = new Scene(registerDialog);
            primaryStage.setScene(scene);
            primaryStage.show();
            RegisterDialog controller = loader.getController();
            controller.setClientApp(this);
            controller.setClientController(client);
        } catch (IOException e) {
            LOGGER.error("Show register dialog error: ", e);
        }
    }

    /**
     * Initializes main window.
     */
    public void showMessengerWindow(User user) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(ClientApp.class.getResource(MESSENGER_WINDOW));
            AnchorPane messengerWindow = loader.load();
            Scene scene = new Scene(messengerWindow);
            primaryStage.setScene(scene);
            MessengerWindow controller = loader.getController();
            controller.setClientApp(this);
            controller.setUser(user);
            controller.setClientController(client);
            primaryStage.show();
        } catch (IOException e) {
            LOGGER.error("Show messenger window error: ", e);
        }

    }

    /**
     * Shows a dialog to add new contacts.
     */
    public boolean addContactsDialog(User user) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(ClientApp.class.getResource(ADD_CONTACTS_DIALOG));
            AnchorPane addContactsDialog = loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Add a new contact");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(primaryStage);

            Scene scene = new Scene(addContactsDialog);
            dialogStage.setScene(scene);

            AddContactsDialog controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setUser(user);
            controller.setClientApp(this);
            controller.setClientController(client);
            dialogStage.showAndWait();
            return controller.isOkClicked();
        } catch (IOException e) {
            LOGGER.error("Show adding contacts dialog error: ", e);
            return false;
        }
    }

    /**
     * Shows a dialog to create new group chat.
     */
    public boolean createNewChatDialog(ChatRoom chatRoom) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(ClientApp.class.getResource(CREATE_NEW_CHAT_DIALOG));
            AnchorPane addContactsDialog = loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Add users to chat");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(primaryStage);

            Scene scene = new Scene(addContactsDialog);
            dialogStage.setScene(scene);

            CreateNewChatDialog controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setChatRoom(chatRoom);
            controller.setClientApp(this);
            controller.setClientController(client);
            dialogStage.showAndWait();
            return controller.isAddClicked();
        } catch (IOException e) {
            LOGGER.error("Show New Chat dialog error: ", e);
            return false;
        }
    }

    /**
     * Initializes Edit Profile dialog.
     */
    public boolean showEditProfileDialog(User user, MessengerWindow parentWindow) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(ClientApp.class.getResource(EDIT_PROFILE_DIALOG));
            AnchorPane page = loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Edit profile");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            EditProfileDialog controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setUser(user);
            controller.setClientController(client);
            controller.setParentWindow(parentWindow);
            dialogStage.showAndWait();
            return controller.isSaveClicked();
        } catch (IOException e) {
            e.printStackTrace();
            LOGGER.error("Failed to initialize Edit Profile dialog", e);
            return false;
        }
    }

    /**
     * Initializes Chat Details dialog.
     */
    public void showChatDetailsDialog(User user, User contact, ChatRoom chatRoom) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(ClientApp.class.getResource(CHAT_DETAILS_DIALOG));
            AnchorPane page = loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle(chatRoom + " group chat details");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            ChatDetailsDialog controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setChatContact(contact);
            controller.setChatRoom(chatRoom);
            controller.setClientApp(this);
            controller.setUser(user);
            controller.setClientController(client);
            dialogStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
            LOGGER.error("Failed to initialize Chat Details dialog", e);
        }
    }


    public static void main(String[] args) {
        System.out.println("Hello from messenger!");
        launch(args);
    }
}
