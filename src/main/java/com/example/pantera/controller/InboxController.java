package com.example.pantera.controller;

import com.example.pantera.domain.*;
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
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.w3c.dom.Text;

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
    ControllerService controllerService = new ControllerService(userDBRepository, friendshipDBRepository, messageDBRepository, connection);
    MenuButtonsController menuButtonsController;

    private Stage dialogStage;
    private User user;
    private final ObservableList<User> usersModel = FXCollections.observableArrayList();

    @FXML
    private TextField nameOfUser;
    @FXML
    private Button backButton;
    @FXML
    private ImageView searchButton;
    @FXML
    private ImageView homeButton;
    @FXML
    private ImageView profileButton;
    @FXML
    private ImageView notificationButton;
    @FXML
    private Button deleteConversationButton;
    @FXML
    private ListView<User> listView;
    @FXML
    private VBox chatBox;

    @FXML
    private void initialize() {

    }

    @FXML
    public void setService(Stage dialogStage, User user) {
        this.dialogStage = dialogStage;
        this.user = user;
        this.menuButtonsController = new MenuButtonsController(dialogStage, user);
        uploadData();
    }

    private void uploadData() {
        List<User> model = (List<User>) friendshipService.getAllFriends(user.getId());
        usersModel.setAll(model);
        listView.setItems(usersModel);
    }

    public void handleMouseClicked() {
        User newUser = listView.getSelectionModel().getSelectedItem();
        nameOfUser.setPromptText(newUser.getFirstName() + " " + newUser.getLastName());
        List<Message> messages = controllerService.getConversation(user, newUser);
        for (Message message : messages) {
            if(message.getFrom().equals(newUser.getId())){
                Label label = new Label(message.getMessage());
                label.setStyle("-fx-text-fill: #ffffff");
                //label.getStylesheets().add("cssStyle/textField.css");
                label.setId("send");
                HBox hBox = new HBox();
                hBox.getChildren().add(label);
                hBox.setAlignment(Pos.BASELINE_RIGHT);
                chatBox.getChildren().add(hBox);
                chatBox.setSpacing(10);
            }
            else
            {
                Label label = new Label(newUser.getFirstName() + " : " + message.getMessage());
                label.setStyle("-fx-text-fill: #ffffff");
                //label.getStylesheets().add("cssStyle/textField.css");
                label.setId("send");
                HBox hBox = new HBox();
                hBox.getChildren().add(label);
                hBox.setAlignment(Pos.BASELINE_LEFT);
                chatBox.getChildren().add(hBox);
                chatBox.setSpacing(10);
            }

        }
    }

    @Override
    public void update(FriendshipChangeEvent friendshipChangeEvent) {
        uploadData();
    }

    public void handleNotificationsButton() { menuButtonsController.moveToNotificationsButton(); }

    public void handleSearchButton() {
        menuButtonsController.moveToSearchButton();
    }

    public void handleHomeButton() {
        menuButtonsController.moveToHomeButton();
    }

    public void handleProfileButton() { menuButtonsController.moveToProfileButton(); }

}
