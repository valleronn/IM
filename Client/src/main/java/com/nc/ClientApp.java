package com.nc;

import com.nc.controller.ClientController;
import com.nc.model.users.Admin;
import com.nc.model.users.User;
import com.nc.view.AddContactsDialog;
import com.nc.view.LoginDialog;
import com.nc.view.MessengerWindow;
import com.nc.view.RegisterDialog;
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

/**
 * Hello world!
 *
 */
public class ClientApp extends Application {

    private Stage primaryStage;
    private User admin;
    private ObservableList<User> users = FXCollections.observableArrayList();
    private ObservableList<User> myContacts = FXCollections.observableArrayList();
    private ObservableList<User> myChatContacts = FXCollections.observableArrayList();

    private String serverName = "localhost";
    private int port = 4444;
    private ClientController client;

    public ClientApp() {
        admin = new Admin("admin", "admin", new Date());
        client = new ClientController(serverName, port);
//        users.add(new User("user2", "user", new Date()));
//        users.add(new User("user3", "user", new Date()));
//        users.add(new User("user4", "user", new Date()));

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
        showLoginDialog();
        primaryStage.setOnCloseRequest(event -> {
            try {
                client.logoff();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                System.exit(0);
            }
        });
    }

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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

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
            e.printStackTrace();
        }
    }

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
            e.printStackTrace();
        }

    }

    public void addContactsDialog() {
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
            //return controller.isOkClicked();
        } catch (IOException e) {
            e.printStackTrace();
            //return false;
        }
    }

    public static void main(String[] args) {
        System.out.println("Hello from client!");
        launch(args);
    }
}
