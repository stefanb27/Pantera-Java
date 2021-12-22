package com.example.pantera.controller;

import com.example.pantera.domain.*;
import com.example.pantera.domain.validators.FriendshipValidator;
import com.example.pantera.domain.validators.UserValidator;
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
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class InboxController implements Observer<FriendshipChangeEvent> {
    Connection connection = new Connection();
    UserDBRepository userDBRepository = new UserDBRepository(connection);
    FriendshipDBRepository friendshipDBRepository = new FriendshipDBRepository(connection);
    UserService userService = new UserService(userDBRepository, friendshipDBRepository, new UserValidator());
    FriendshipService friendshipService = new FriendshipService(userDBRepository, friendshipDBRepository, new FriendshipValidator());
    MessageDBRepository messageDBRepository = new MessageDBRepository(connection);
    MessageService messageService = new MessageService(userDBRepository, friendshipDBRepository, messageDBRepository);

    private Stage dialogStage;
    private User user;
    private final ObservableList<MessageWrapper> messageWrapperModel = FXCollections.observableArrayList();

    @FXML
    private Button backButton;
    @FXML
    private Button deleteConversationButton;
    @FXML
    private TableView<MessageWrapper> tableView;
    @FXML
    private TableColumn<MessageWrapper, String> id;
    @FXML
    private TableColumn<MessageWrapper, String> firstNameColumn;
    @FXML
    private TableColumn<MessageWrapper, String> lastNameColumn;
    @FXML
    private TableColumn<MessageWrapper, String> messageColumn;

    @FXML
    private void initialize() {
    }

    @FXML
    public void setService(Stage dialogStage, User user) {
        this.dialogStage = dialogStage;
        this.user = user;
        //todo
        //messageService.addObserver(this);

        id.setCellValueFactory(new PropertyValueFactory<>("id"));
        firstNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        lastNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        messageColumn.setCellValueFactory(new PropertyValueFactory<>("message"));

        uploadData();
    }

    private void uploadData() {
        List<User> all = (List<User>) friendshipService.getAllFriends(user.getId());
        List<MessageWrapper> result = all.stream().map(x -> {
            Message message = messageService.showConversations(user.getId(), x.getId()).stream().
                    reduce((first, second) -> second).orElse(null);
            return new MessageWrapper(x.getId(), x.getFirstName(), x.getLastName(),
                    message.getMessage());
        }).collect(Collectors.toList());
        List<MessageWrapper> messageTaskList = StreamSupport.stream(result.spliterator(), false)
                .collect(Collectors.toList());
        messageWrapperModel.setAll(messageTaskList);
        tableView.setItems(messageWrapperModel);
    }

    @Override
    public void update(FriendshipChangeEvent friendshipChangeEvent) {
        uploadData();
    }

    public void handleMouseClicked() {
        Long id = tableView.getSelectionModel().getSelectedItem().getId();
        User newUser = userService.findUser(id);
        //todo: needs 2 clicks
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/views/message.fxml"));

        try {
            BorderPane root = (BorderPane) loader.load();
            Stage dialogStage = new Stage();
            dialogStage.setTitle("User Interface");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            //dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(root);
            dialogStage.setScene(scene);

            MessageController userViewController = loader.getController();
            userViewController.setService(dialogStage, user, newUser);
            dialogStage.show();
            this.dialogStage.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void handleBackButton() {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/views/profile.fxml"));

        try {
            BorderPane root = (BorderPane) loader.load();
            Stage dialogStage = new Stage();
            dialogStage.setTitle("User Interface");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            //dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(root);
            dialogStage.setScene(scene);

            ProfileController userViewController = loader.getController();
            userViewController.setService(dialogStage, user);
            dialogStage.show();
            this.dialogStage.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void handleDeleteConversationButton() {
        Long id = tableView.getSelectionModel().getSelectedItem().getId();
        //todo
    }
}
