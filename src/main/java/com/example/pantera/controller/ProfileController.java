package com.example.pantera.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.ImageCursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
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
import com.example.pantera.repository.db.MessageDBRepository;
import com.example.pantera.repository.db.UserDBRepository;
import com.example.pantera.service.FriendshipService;
import com.example.pantera.service.MessageService;
import com.example.pantera.service.UserService;

import java.io.IOException;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class ProfileController {
    Connection connection = new Connection();
    UserDBRepository userDBRepository = new UserDBRepository(connection);
    FriendshipDBRepository friendshipDBRepository = new FriendshipDBRepository(connection);
    UserService userService = new UserService(userDBRepository, friendshipDBRepository, new UserValidator());
    FriendshipService friendshipService = new FriendshipService(userDBRepository, friendshipDBRepository, new FriendshipValidator());
    MessageDBRepository messageDBRepository = new MessageDBRepository(connection);
    MessageService messageService = new MessageService(userDBRepository, friendshipDBRepository, messageDBRepository);

    private Stage dialogStage;
    private User user;

    @FXML
    private ImageView searchButton;
    @FXML
    private ImageView homeButton;
    @FXML
    private ImageView notificationsButton;

    @FXML
    private Button backButton;
    @FXML
    private Button editProfileButton;
    @FXML
    private Button settingsButton;
    @FXML
    private Button friendsButton;
    @FXML
    private Button myPostsButton;
    @FXML
    private Button tagsButton;

    @FXML
    private Label firstName;

    @FXML
    private Label lastName;

    @FXML
    private ListView<User> friends;

    @FXML
    private void initialize() {
    }

    @FXML
    public void setService(Stage dialogStage, User user) {
        this.dialogStage = dialogStage;
        this.user = user;
        firstName.setText(user.getFirstName());
        lastName.setText(user.getLastName());

        ObservableList<User> myFriendsModel = FXCollections.observableArrayList();
        Iterable<User> all = friendshipService.getAllFriends(user.getId());
        List<User> messageTaskList = StreamSupport.stream(all.spliterator(), false)
                .collect(Collectors.toList());
        myFriendsModel.setAll(messageTaskList);
        friends.setItems(myFriendsModel);

//        ObservableList<User> model = (ObservableList<User>) friendshipService.getAllFriends(user.getId());
//        List<User> result = (List<User>) friendshipService.getAllFriends(user.getId());
//        friends.setItems(model);
//        friendsButton.setText(result.size() + "");
    }

    public void handleFriendsButton() {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/views/friends.fxml"));

        try {
            BorderPane root = (BorderPane) loader.load();
            Stage dialogStage = new Stage();
            dialogStage.setTitle("User Interface");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            //dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(root);
            dialogStage.setScene(scene);

            FriendsController userViewController = loader.getController();
            userViewController.setService(dialogStage, user);
            dialogStage.show();
            //this.dialogStage.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void handleBackButton() {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/views/home.fxml"));

        try {
            BorderPane root = (BorderPane) loader.load();
            Stage dialogStage = new Stage();
            dialogStage.setTitle("User Interface");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            //dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(root);
            dialogStage.setScene(scene);

            HomeController userViewController = loader.getController();
            userViewController.setService(dialogStage, user);
            dialogStage.show();
            //this.dialogStage.close();

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

    public void handleHomeButton() {
        toHome();
    }

    public void toHome(){
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

}
