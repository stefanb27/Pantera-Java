package com.example.pantera.controller;

import com.example.pantera.utils.ProfileCell;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
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
    MenuButtonsController menuButtonsController;

    ObservableList<User> myFriendsModel = FXCollections.observableArrayList();

    private Stage dialogStage;
    private User user;

    @FXML
    private ImageView searchButton;
    @FXML
    private ImageView homeButton;
    @FXML
    private ImageView notificationsButton;
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
    private ImageView inboxButton;
    @FXML
    private Label firstName;
    @FXML
    private Label lastName;
    @FXML
    private ListView<User> listView;

    @FXML
    private void initialize() {
    }

    @FXML
    public void setService(Stage dialogStage, User user) {
        this.dialogStage = dialogStage;
        this.user = user;
        this.menuButtonsController = new MenuButtonsController(dialogStage, user);
        firstName.setText(user.getFirstName());
        lastName.setText(user.getLastName());
        Iterable<User> all = friendshipService.getAllFriends(user.getId());
        List<User> messageTaskList = StreamSupport.stream(all.spliterator(), false)
                .collect(Collectors.toList());
        listView.setCellFactory(param -> new ProfileCell(user));
        myFriendsModel.setAll(messageTaskList);
        listView.setItems(myFriendsModel);
    }

    public void handleNotificationsButton() {
        menuButtonsController.moveToNotificationsButton();
    }

    public void handleSearchButton() {
        menuButtonsController.moveToSearchButton();
    }

    public void handleHomeButton() {
        menuButtonsController.moveToHomeButton();
    }

    public void handleInboxButton() { menuButtonsController.moveToInboxController();}

}
