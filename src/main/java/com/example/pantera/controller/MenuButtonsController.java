package com.example.pantera.controller;

import com.example.pantera.domain.Page;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;

public class MenuButtonsController {
    private Stage dialogStage;
    private Page user;

    public MenuButtonsController(Stage dialogStage, Page user) {
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

    public void moveToInboxController() {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/views/inbox.fxml"));

        try {
            AnchorPane root = loader.load();
            dialogStage.setTitle("Inbox");
            Scene scene = new Scene(root);
            dialogStage.setScene(scene);

            InboxController inboxController = loader.getController();
            inboxController.setService(dialogStage, user);
            dialogStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void moveToGroupController() {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/views/group.fxml"));

        try {
            AnchorPane root = loader.load();
            dialogStage.setTitle("Group");
            Scene scene = new Scene(root);
            dialogStage.setScene(scene);

            GroupController groupController = loader.getController();
            groupController.setService(dialogStage, user);
            dialogStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void moveToSignUpController() {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/views/sign-up.fxml"));

        try {
            AnchorPane root = loader.load();
            dialogStage.setTitle("Sign Up");
            Scene scene = new Scene(root);
            dialogStage.setScene(scene);

            SignUpController signUpController = loader.getController();
            signUpController.setService(dialogStage);
            dialogStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void moveToLogInController() {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/views/login.fxml"));

        try {
            AnchorPane root = loader.load();
            dialogStage.setTitle("Log in");
            Scene scene = new Scene(root);
            dialogStage.setScene(scene);

            LogInController logInController = loader.getController();
            logInController.setService(dialogStage);
            dialogStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
