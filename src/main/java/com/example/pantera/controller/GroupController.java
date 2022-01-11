package com.example.pantera.controller;

import com.example.pantera.domain.*;
import com.example.pantera.domain.validators.FriendshipValidator;
import com.example.pantera.domain.validators.UserValidator;
import com.example.pantera.events.FriendshipChangeEvent;
import com.example.pantera.repository.db.FriendshipDBRepository;
import com.example.pantera.repository.db.GroupDBRepository;
import com.example.pantera.repository.db.MessageDBRepository;
import com.example.pantera.repository.db.UserDBRepository;
import com.example.pantera.service.*;
import com.example.pantera.utils.GroupCell;
import com.example.pantera.utils.Observer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class GroupController implements Observer<FriendshipChangeEvent> {
    Connection connection = new Connection();
    UserDBRepository userDBRepository = new UserDBRepository(connection);
    FriendshipDBRepository friendshipDBRepository = new FriendshipDBRepository(connection);
    GroupDBRepository groupDBRepository = new GroupDBRepository(connection);
    UserService userService = new UserService(userDBRepository, friendshipDBRepository, new UserValidator());
    FriendshipService friendshipService = new FriendshipService(userDBRepository, friendshipDBRepository, new FriendshipValidator());
    MessageDBRepository messageDBRepository = new MessageDBRepository(connection);
    MessageService messageService = new MessageService(userDBRepository, friendshipDBRepository, messageDBRepository);
    ControllerService controllerService = new ControllerService(userDBRepository, friendshipDBRepository, messageDBRepository, connection);
    MenuButtonsController menuButtonsController;

    private Stage dialogStage;
    private Page user;
    GroupCell groupCell;
    List<User> groupGuys = new ArrayList<>();
    private final ObservableList<User> usersModel = FXCollections.observableArrayList();

    @FXML
    private Button createGroup;

    @FXML
    private TextField groupName;

    @FXML
    private ImageView arrowBack;

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
    private void initialize() {}

    @FXML
    public void setService(Stage dialogStage, Page user) {
        this.dialogStage = dialogStage;
        this.user = user;
        this.menuButtonsController = new MenuButtonsController(dialogStage, user);
        friendshipService.addObserver(this);
        uploadData();
    }

    private void uploadData() {
        List<User> model = (List<User>) friendshipService.getAllFriends(user.getId());
        usersModel.setAll(model);
        //groupCell = new GroupCell(user);
        groupGuys.add(user);
        listView.setCellFactory(p -> new GroupCell(user, groupGuys));
        //listView.getCellFactory().getClass().;
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

    public void handleCreateGroup(){
        if (!groupName.getText().equals("") && groupGuys.size() != 1) {
            Group newGroup = new Group(groupGuys, groupName.getText());
            groupDBRepository.save(newGroup);
        }
    }
}
