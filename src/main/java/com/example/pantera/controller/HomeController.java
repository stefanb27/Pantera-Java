package com.example.pantera.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import com.example.pantera.domain.Connection;
import com.example.pantera.domain.User;
import com.example.pantera.domain.validators.FriendshipValidator;
import com.example.pantera.domain.validators.UserValidator;
import com.example.pantera.repository.db.FriendshipDBRepository;
import com.example.pantera.repository.db.UserDBRepository;
import com.example.pantera.service.FriendshipService;
import com.example.pantera.service.UserService;

import java.io.IOException;

public class HomeController {
    Connection connection = new Connection();
    UserDBRepository userDBRepository = new UserDBRepository(connection);
    FriendshipDBRepository friendshipDBRepository = new FriendshipDBRepository(connection);
    UserService userService = new UserService(userDBRepository, friendshipDBRepository, new UserValidator());
    FriendshipService friendshipService = new FriendshipService(userDBRepository, friendshipDBRepository, new FriendshipValidator());

    private Stage dialogStage;
    private User user;

    @FXML
    private ScrollPane feed;
    @FXML
    private Button messageButton;
    @FXML
    private ImageView homeButton;
    @FXML
    private ImageView searchButton;
    @FXML
    private ImageView profileButton;
    @FXML
    private ImageView notificationsButton;

    @FXML
    private void initialize() {

    }

    @FXML
    public void setService(Stage dialogStage, User user) {
        this.dialogStage = dialogStage;
        this.user = user;
    }

    public void handleMessageButton() {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/views/inbox.fxml"));

        try {
            BorderPane root = (BorderPane) loader.load();
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Inbox");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            //dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(root);
            dialogStage.setScene(scene);

            InboxController userViewController = loader.getController();
            userViewController.setService(dialogStage, user);
            dialogStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void handleHomeButton() {

    }


    public void handleRequestsButton() {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/views/notifications.fxml"));

        try {
            BorderPane root = (BorderPane) loader.load();
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Notifications");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            //dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(root);
            dialogStage.setScene(scene);

            NotificationsController userViewController = loader.getController();
            userViewController.setService(dialogStage, user);
            dialogStage.show();
            this.dialogStage.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void handleNotificationsButton() {
        toNotifications();
    }

    public void toNotifications(){
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

    public void handleSearchButton() {
        toSearch();
    }

    public void toSearch(){
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

    public void handleProfileButton() {
        toProfile();
    }

    public void toProfile(){
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
}
