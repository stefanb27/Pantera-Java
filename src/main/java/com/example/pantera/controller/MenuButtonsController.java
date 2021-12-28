package com.example.pantera.controller;

import com.example.pantera.domain.User;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;

public class MenuButtonsController {
    private Stage dialogStage;
    private User user;

    public MenuButtonsController(Stage dialogStage, User user) {
        this.dialogStage = dialogStage;
        this.user = user;
    }

    public void moveToSearchButton() {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/views/search.fxml"));

        try {
            AnchorPane root = loader.load();
            dialogStage.setTitle("Search");
            Scene scene = new Scene(root);
            dialogStage.setScene(scene);

            SearchController searchController = loader.getController();
            searchController.setService(dialogStage, user);
            dialogStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void moveToProfileButton() {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/views/profile.fxml"));

        try {
            AnchorPane root = loader.load();
            dialogStage.setTitle("Profile");
            Scene scene = new Scene(root);
            dialogStage.setScene(scene);

            ProfileController profileController = loader.getController();
            profileController.setService(dialogStage, user);
            dialogStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void moveToHomeButton() {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/views/home.fxml"));

        try {
            AnchorPane root = loader.load();
            dialogStage.setTitle("Home");
            Scene scene = new Scene(root);
            dialogStage.setScene(scene);

            HomeController homeController = loader.getController();
            homeController.setService(dialogStage, user);
            dialogStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void moveToNotificationsButton() {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/views/notifications.fxml"));

        try {
            AnchorPane root = loader.load();
            dialogStage.setTitle("Notifications");
            Scene scene = new Scene(root);
            dialogStage.setScene(scene);

            NotificationsController notificationsController = loader.getController();
            notificationsController.setService(dialogStage, user);
            dialogStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
