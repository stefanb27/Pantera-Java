package com.example.pantera.controller;

import com.example.pantera.domain.*;
import com.example.pantera.events.FriendshipChangeEvent;
import com.example.pantera.repository.db.EventDBRepository;
import com.example.pantera.service.ControllerService;
import com.example.pantera.utils.NotificationsCell;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import com.example.pantera.domain.validators.FriendshipValidator;
import com.example.pantera.domain.validators.UserValidator;
import com.example.pantera.repository.db.FriendshipDBRepository;
import com.example.pantera.repository.db.MessageDBRepository;
import com.example.pantera.repository.db.UserDBRepository;
import com.example.pantera.service.FriendshipService;
import com.example.pantera.service.MessageService;
import com.example.pantera.service.UserService;
import com.example.pantera.utils.Observer;

import java.sql.Date;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class NotificationsController implements Observer<FriendshipChangeEvent> {
    Connection connection = new Connection();
    UserDBRepository userDBRepository = new UserDBRepository(connection);
    FriendshipDBRepository friendshipDBRepository = new FriendshipDBRepository(connection);
    UserService userService = new UserService(userDBRepository, friendshipDBRepository, new UserValidator());
    FriendshipService friendshipService = new FriendshipService(userDBRepository, friendshipDBRepository, new FriendshipValidator());
    MessageDBRepository messageDBRepository = new MessageDBRepository(connection);
    MessageService messageService = new MessageService(userDBRepository, friendshipDBRepository, messageDBRepository);
    ControllerService controllerService = new ControllerService(userDBRepository, friendshipDBRepository, messageDBRepository, connection);
    MenuButtonsController menuButtonsController;
    EventDBRepository eventDBRepository = new EventDBRepository(connection);

    private Stage dialogStage;
    private Page user;
    private final ObservableList<NotificationsWrapper> friendshipsModel = FXCollections.observableArrayList();


    @FXML
    private ImageView searchButton;
    @FXML
    private ImageView homeButton;
    @FXML
    private ImageView profileButton;
    @FXML
    private ListView<NotificationsWrapper> listView;

    @FXML
    private ImageView inboxButton;

    @FXML
    private void initialize() {
        Tooltip tooltip1 = new Tooltip("Home");
        tooltip1.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 14;");
        Tooltip.install(homeButton, tooltip1);
        Tooltip tooltip2 = new Tooltip("Notifications");
        tooltip2.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 14;");
        Tooltip.install(inboxButton, tooltip2);
        Tooltip tooltip3 = new Tooltip("Search");
        tooltip3.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 14;");
        Tooltip.install(searchButton, tooltip3);
        Tooltip tooltip4 = new Tooltip("Profile");
        tooltip4.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 14;");
        Tooltip.install(profileButton, tooltip4);
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
        List<NotificationsWrapper> result = new ArrayList<>();
        //controllerService.notificationsFilter(user);

        for(NiceEvent event : eventDBRepository.getAllEvents()){
            if(eventDBRepository.isGoing(user.getId(), event.getId())){
                result.add(new NotificationsWrapper(event.getId() + 1000, event.getNameEvent(), "a", "a",
                        LocalDateTime.parse(event.getDateTime() + "T" + event.getHours())));
            }
        }

        result.addAll(user.getRequestsReceived());

        friendshipsModel.setAll(result);
        //NotificationsWrapper notificationsWrapper = new NotificationsWrapper(user.getId(), user.getFirstName(), user.getLastName(), "approved", LocalDateTime.now())
        listView.setCellFactory(param -> new NotificationsCell(user, friendshipService));
        listView.setItems(friendshipsModel);
    }

    @Override
    public void update(FriendshipChangeEvent friendshipChangeEvent) {
        uploadData();
    }

    public void handleSearchButton() {
        menuButtonsController.moveToSearchButton();
    }

    public void handleProfileButton() {
        menuButtonsController.moveToProfileButton();
    }

    public void handleHomeButton() {
        menuButtonsController.moveToHomeButton();
    }

    public void handleInboxButton() { menuButtonsController.moveToInboxController();}
}