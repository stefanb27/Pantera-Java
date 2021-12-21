package com.example.pantera.controller;

import com.example.pantera.domain.Connection;
import com.example.pantera.domain.Friendship;
import com.example.pantera.domain.Tuple;
import com.example.pantera.domain.User;
import com.example.pantera.domain.validators.FriendshipValidator;
import com.example.pantera.domain.validators.UserValidator;
import com.example.pantera.domain.validators.ValidateException;
import com.example.pantera.events.FriendshipChangeEvent;
import com.example.pantera.repository.db.FriendshipDBRepository;
import com.example.pantera.repository.db.MessageDBRepository;
import com.example.pantera.repository.db.UserDBRepository;
import com.example.pantera.service.FriendshipService;
import com.example.pantera.service.MessageService;
import com.example.pantera.service.UserService;
import com.example.pantera.utils.Observer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
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

    private Stage dialogStage;
    private User user;
    private final ObservableList<User> usersModel = FXCollections.observableArrayList();

    @FXML
    private Button backButton;
    @FXML
    private Button requestButton;
    @FXML
    private TableView<User> tableView;
    @FXML
    private TableColumn<User, String> id;
    @FXML
    private TableColumn<User, String> firstNameColumn;
    @FXML
    private TableColumn<User, String> lastNameColumn;
    @FXML
    private TextField searchText;

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

        uploadData();
    }

    private void uploadData() {

    }

    @Override
    public void update(FriendshipChangeEvent friendshipChangeEvent) {
        uploadData();
    }

    public void handleOnKeyTyped() {
        String textTyped = searchText.getText();
        List<User> users = (List<User>) userService.getAllUsers();
        List<User> result = users.stream().filter(x -> !x.getId().equals(user.getId()) &&
                (x.getFirstName().contains(textTyped) || x.getLastName().contains(textTyped))).collect(Collectors.toList());
        usersModel.setAll(result);
        tableView.setItems(usersModel);
    }

    public Long handleMouseClicked() {
        Long id = tableView.getSelectionModel().getSelectedItem().getId();
        Friendship friendship = friendshipService.findFriendship(new Tuple(user.getId(), id));
        if (friendship != null && friendship.getStatus().equals("pending")
                && friendship.getId().getLeft().equals(user.getId())) {
            requestButton.setText("Remove request");
        }
        else {
            requestButton.setText("Send request");
        }
        return id;
    }

    public void handleRequestButton() {
        Long id = handleMouseClicked();
        try {
            if (requestButton.getText().equals("Send request")) {
                friendshipService.addFriendship(user.getId(), id);
            } else {
                friendshipService.deleteFriendship(user.getId(), id);
                requestButton.setText("Send request");
            }
        } catch(ValidateException e) {
            //todo
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
            this.dialogStage.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
