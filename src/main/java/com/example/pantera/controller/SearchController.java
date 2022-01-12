package com.example.pantera.controller;

import com.example.pantera.domain.Connection;
import com.example.pantera.domain.Page;
import com.example.pantera.domain.User;
import com.example.pantera.domain.validators.FriendshipValidator;
import com.example.pantera.domain.validators.UserValidator;
import com.example.pantera.events.FriendshipChangeEvent;
import com.example.pantera.repository.db.FriendshipDBRepository;
import com.example.pantera.repository.db.MessageDBRepository;
import com.example.pantera.repository.db.UserDBRepository;
import com.example.pantera.service.ControllerService;
import com.example.pantera.service.FriendshipService;
import com.example.pantera.service.MessageService;
import com.example.pantera.service.UserService;
import com.example.pantera.utils.Observer;
import com.example.pantera.utils.SearchCell;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.util.List;
import java.util.stream.Collectors;

public class SearchController implements Observer<FriendshipChangeEvent> {
    Connection connection = new Connection();
    UserDBRepository userDBRepository = new UserDBRepository(connection);
    FriendshipDBRepository friendshipDBRepository = new FriendshipDBRepository(connection);
    UserService userService = new UserService(userDBRepository, friendshipDBRepository, new UserValidator());
    FriendshipService friendshipService = new FriendshipService(userDBRepository, friendshipDBRepository, new FriendshipValidator());
    MessageDBRepository messageDBRepository = new MessageDBRepository(connection);
    MessageService messageService = new MessageService(userDBRepository, friendshipDBRepository, messageDBRepository);
    ControllerService controllerService = new ControllerService(userDBRepository, friendshipDBRepository, messageDBRepository, connection);
    MenuButtonsController menuButtonsController;

    private Stage dialogStage;
    private User user;
    private final ObservableList<User> usersModel = FXCollections.observableArrayList();

    @FXML
    private Button requestButton;

    @FXML
    private ImageView inboxButton;

    @FXML
    private ImageView profileButton;
    @FXML
    private ImageView homeButton;
    @FXML
    private ImageView notificationsButton;
    @FXML
    private TextField searchText;
    @FXML
    private ListView<User> listView;

    @FXML
    private void initialize() {
        Tooltip.install(inboxButton, new Tooltip("Inbox"));
        Tooltip.install(homeButton, new Tooltip("Home"));
        Tooltip.install(notificationsButton, new Tooltip("Notifications"));
        Tooltip.install(profileButton, new Tooltip("Profile"));
    }

    @FXML
    public void setService(Stage dialogStage, Page user) {
        this.dialogStage = dialogStage;
        this.user = user;
        this.menuButtonsController = new MenuButtonsController(dialogStage, user);
        friendshipService.addObserver(this);
        uploadData();
    }

    private void uploadData() {
        List<User> model = controllerService.searchListFilter(user);
        usersModel.setAll(model);
        listView.setCellFactory(param -> new SearchCell(user));
        listView.setItems(usersModel);
    }

    @Override
    public void update(FriendshipChangeEvent friendshipChangeEvent) {
        uploadData();
    }

    public void handleOnKeyTyped() {
        if (searchText.getText().equals("")) {
            List<User> model = controllerService.searchListFilter(user);
            usersModel.setAll(model);
            listView.setItems(usersModel);
        } else {
            String textTyped = searchText.getText();
            List<User> users = controllerService.searchBoxFilter(user, textTyped);
            usersModel.setAll(users);
            listView.setItems(usersModel);
        }
    }

    public void handleNotificationsButton() {
        menuButtonsController.moveToNotificationsButton();
    }

    public void handleProfileButton() {
        menuButtonsController.moveToProfileButton();
    }

    public void handleHomeButton() {
        menuButtonsController.moveToHomeButton();
    }

    public void handleInboxButton() { menuButtonsController.moveToInboxController();}
}
