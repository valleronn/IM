package com.nc;

import com.nc.model.users.Admin;
import com.nc.model.users.User;
import com.nc.view.LoginDialog;
import com.nc.view.MessengerWindow;
import com.nc.view.RegisterDialog;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
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

    public ClientApp() {
        admin = new Admin(1, "admin", "Administrator", "admin", new Date());
    }

    public User getAdmin() {
        return admin;
    }


    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        showLoginDialog();
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
            controller.setUser(user);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        System.out.println("Hello from client!");
        launch(args);
    }
}
