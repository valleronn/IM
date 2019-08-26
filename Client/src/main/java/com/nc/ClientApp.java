package com.nc;

import com.nc.controller.ClientController;
import com.nc.controller.ConfigReader;
import com.nc.model.users.Admin;
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
import java.util.Date;
import org.apache.log4j.Logger;

/**
 * Client main class of Messenger application.
 */
public class ClientApp extends Application {

    private final static Logger LOGGER = Logger.getLogger(ClientApp.class);

    private Stage primaryStage;
    private User admin;
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
        admin = new Admin("admin", "admin", new Date());
        client = new ClientController(serverName, port);
    }

    public User getAdmin() {
        return admin;
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
        loader.setLocation(ClientApp.class.getResource("view/LoginDialog.fxml"));
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
        loader.setLocation(ClientApp.class.getResource("view/RegisterDialog.fxml"));
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
            loader.setLocation(ClientApp.class.getResource("view/MessengerWindow.fxml"));
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
    public boolean addContactsDialog() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(ClientApp.class.getResource("view/AddContactsDialog.fxml"));
            AnchorPane addContactsDialog = loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Add a new contact");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(primaryStage);

            Scene scene = new Scene(addContactsDialog);
            dialogStage.setScene(scene);

            AddContactsDialog controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setClientApp(this);
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
    public boolean createNewChatDialog() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(ClientApp.class.getResource("view/CreateNewChatDialog.fxml"));
            AnchorPane addContactsDialog = loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Create a new chat");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(primaryStage);

            Scene scene = new Scene(addContactsDialog);
            dialogStage.setScene(scene);

            CreateNewChatDialog controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setClientApp(this);
            controller.setClientController(client);
            dialogStage.showAndWait();
            return controller.isOkClicked();
        } catch (IOException e) {
            LOGGER.error("Show New Chat dialog error: ", e);
            return false;
        }
    }

    public static void main(String[] args) {
        System.out.println("Hello from messenger!");
        launch(args);
    }
}
