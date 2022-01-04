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
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

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
    private List<Long> sendToUser = new ArrayList<>();
    private final ObservableList<User> usersModel = FXCollections.observableArrayList();
    private final ObservableList<Message> messagesModel = FXCollections.observableArrayList();

    @FXML
    private TextField nameOfUser;
    @FXML
    private ImageView searchButton;
    @FXML
    private ImageView homeButton;
    @FXML
    private ImageView profileButton;
    @FXML
    private ImageView notificationButton;
    @FXML
    private Button sendButton;
    @FXML
    private TextField sendTextField;
    @FXML
    private ListView<User> listView;
    @FXML
    private VBox chatBox;
    @FXML
    private ScrollPane scrollPane;

    @FXML
    private void initialize() {

    }

    @FXML
    public void setService(Stage dialogStage, User user) {
        this.dialogStage = dialogStage;
        this.user = user;
        this.menuButtonsController = new MenuButtonsController(dialogStage, user);
        this.sendButton.setDisable(true);
        List<User> model = (List<User>) friendshipService.getAllFriends(user.getId());
        usersModel.setAll(model);
        listView.setItems(usersModel);
        messageService.addObserver(this);
    }

    private void uploadData(Long newUser) {
        chatBox.getChildren().clear();
        List<Message> messages = controllerService.getConversation(user.getId(), newUser);
        for (Message message : messages) {
            Label label = new Label(message.getMessage());
            //label.setMinWidth(Region.USE_PREF_SIZE);
            label.setStyle("-fx-text-fill: #ffffff;-fx-border-radius: 15; -fx-border-color: #8284AD;");
            label.setPadding(new Insets(0, 5, 0, 5));        //label.getStylesheets().add("cssStyle/textField.css");
            label.setId("send");
            label.getStylesheets().add("cssStyle/textField.css");
            HBox hBox = new HBox();
            hBox.getChildren().add(label);
            if(message.getFrom().equals(user.getId()))
                hBox.setAlignment(Pos.BASELINE_RIGHT);
            else
                hBox.setAlignment(Pos.BASELINE_LEFT);

            //hBox.setStyle("");
            //hBox.setMinWidth(Region.USE_PREF_SIZE);
            chatBox.getChildren().add(hBox);
            chatBox.setSpacing(10);
        }
        scrollPane.setVvalue(1.0);
    }

    public void handleMouseClicked() {
        chatBox.getChildren().clear();
        User newUser = listView.getSelectionModel().getSelectedItem();
        nameOfUser.setText(newUser.getFirstName() + " " + newUser.getLastName());

        sendToUser.clear();
        sendToUser.add(newUser.getId());
        this.sendButton.setDisable(false);
        uploadData(newUser.getId());
    }

    @Override
    public void update(FriendshipChangeEvent friendshipChangeEvent) {
        uploadData(sendToUser.get(0));
    }

    public void handleSendButton() {
        if (sendTextField.getText() != null) {
            messageService.sendMessage(user.getId(), sendToUser, sendTextField.getText());
            sendTextField.clear();
        }
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

    public void handleProfileButton() {
        menuButtonsController.moveToProfileButton();
    }

}
