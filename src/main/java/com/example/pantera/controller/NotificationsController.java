package com.example.pantera.controller;

import com.example.pantera.domain.*;
import com.example.pantera.events.FriendshipChangeEvent;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
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

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class NotificationsController implements Observer<FriendshipChangeEvent> {
    Connection connection = new Connection();
    UserDBRepository userDBRepository = new UserDBRepository(connection);
    FriendshipDBRepository friendshipDBRepository = new FriendshipDBRepository(connection);
    UserService userService = new UserService(userDBRepository, friendshipDBRepository, new UserValidator());
    FriendshipService friendshipService = new FriendshipService(userDBRepository, friendshipDBRepository, new FriendshipValidator());
    MessageDBRepository messageDBRepository = new MessageDBRepository(connection);
    MessageService messageService = new MessageService(userDBRepository, friendshipDBRepository, messageDBRepository);

    private Stage dialogStage;
    private User user;
    private final ObservableList<NotificationsWrapper> friendshipsModel = FXCollections.observableArrayList();


    @FXML
    private ImageView searchButton;
    @FXML
    private ImageView homeButton;
    @FXML
    private ImageView profileButton;

    @FXML
    private Button backButton;
    @FXML
    private Button acceptButton;
    @FXML
    private Button deleteButton;
    @FXML
    private TableView<NotificationsWrapper> tableView;
    @FXML
    private TableColumn<NotificationsWrapper, String> id;
    @FXML
    private TableColumn<NotificationsWrapper, String> firstNameColumn;
    @FXML
    private TableColumn<NotificationsWrapper, String> lastNameColumn;
    @FXML
    private TableColumn<NotificationsWrapper, String> dateColumn;

    @FXML
    private void initialize() {
    }

    @FXML
    public void setService(Stage dialogStage, User user) {
        this.dialogStage = dialogStage;
        this.user = user;

        friendshipService.addObserver(this);

        id.setCellValueFactory(new PropertyValueFactory<>("id"));
        firstNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        lastNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));

        uploadData();
    }
    private void uploadData() {
        List<Friendship> requests = friendshipService.getAllRequest(user.getId());
        List<NotificationsWrapper> result = requests.stream().map(x -> {
            User user = userService.findUser(x.getId().getLeft());
            return new NotificationsWrapper(user.getId(), user.getFirstName(), user.getLastName(),
                    x.getDateTime());
        }).collect(Collectors.toList());
        List<NotificationsWrapper> messageTaskList = StreamSupport.stream(result.spliterator(), false)
                .collect(Collectors.toList());
        friendshipsModel.setAll(messageTaskList);
        tableView.setItems(friendshipsModel);
    }

    @Override
    public void update(FriendshipChangeEvent friendshipChangeEvent) {
        uploadData();
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
            this.dialogStage.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void handleAcceptButton() {
        Long id = tableView.getSelectionModel().getSelectedItem().getId();
        friendshipService.approveFriendship(user.getId(), id);
    }

    public void handleDeleteButton() {
        Long id = tableView.getSelectionModel().getSelectedItem().getId();
        friendshipService.deleteFriendship(user.getId(), id);
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

    public void handleNotificationsButton(MouseEvent mouseEvent) {
    }
}
