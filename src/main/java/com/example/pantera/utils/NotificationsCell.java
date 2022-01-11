package com.example.pantera.utils;

import com.example.pantera.domain.Connection;
import com.example.pantera.domain.NotificationsWrapper;
import com.example.pantera.domain.Page;
import com.example.pantera.domain.User;
import com.example.pantera.domain.validators.FriendshipValidator;
import com.example.pantera.domain.validators.UserValidator;
import com.example.pantera.repository.db.FriendshipDBRepository;
import com.example.pantera.repository.db.MessageDBRepository;
import com.example.pantera.repository.db.UserDBRepository;
import com.example.pantera.service.FriendshipService;
import com.example.pantera.service.MessageService;
import com.example.pantera.service.UserService;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;

import java.sql.Time;
import java.time.LocalDateTime;
import java.util.Calendar;

public class NotificationsCell extends ListCell<NotificationsWrapper> {
    HBox hbox = new HBox();
    Label label = new Label("");
    Pane pane = new Pane();
    @FXML
    Button addButton = new Button();
    @FXML
    Button deleteButton = new Button();
    Page loggedUser;
    NotificationsWrapper user;

    Connection connection = new Connection();
    UserDBRepository userDBRepository = new UserDBRepository(connection);
    FriendshipDBRepository friendshipDBRepository = new FriendshipDBRepository(connection);
    UserService userService = new UserService(userDBRepository, friendshipDBRepository, new UserValidator());
    FriendshipService friendshipService = new FriendshipService(userDBRepository, friendshipDBRepository, new FriendshipValidator());
    MessageDBRepository messageDBRepository = new MessageDBRepository(connection);
    MessageService messageService = new MessageService(userDBRepository, friendshipDBRepository, messageDBRepository);


    public NotificationsCell(Page loggedUser) {
        super();
        this.loggedUser = loggedUser;

        this.addButton.getStylesheets().add("cssStyle/buttonLOGIN.css");
        this.deleteButton.getStylesheets().add("cssStyle/buttonLOGIN.css");
        ImageView imageView1 = new ImageView("D:\\ubb\\semestrul III\\metode avansate\\pantera\\src\\main\\resources\\images\\check.png");
        ImageView imageView2 = new ImageView("D:\\ubb\\semestrul III\\metode avansate\\pantera\\src\\main\\resources\\images\\x.png");
        imageView1.setFitWidth(13);
        imageView2.setFitWidth(13);
        imageView1.setFitHeight(13);
        imageView2.setFitHeight(13);

        this.addButton.setGraphic(imageView1);
        this.deleteButton.setGraphic(imageView2);
        hbox.getChildren().addAll(label, pane, addButton, deleteButton);
        HBox.setHgrow(pane, Priority.ALWAYS);
        addButton.setOnAction(event -> handleAddButton(user));
        deleteButton.setOnAction(event -> handleDeleteButton(user));
    }

    public void handleAddButton(NotificationsWrapper user) {
        friendshipService.approveFriendship(loggedUser.getId(), user.getId());
        addButton.setText("Friend");
        addButton.setDisable(true);
        deleteButton.setDisable(true);
        loggedUser.addFriend(userDBRepository.findOne(user.getId())); //paging
    }

    public void handleDeleteButton(NotificationsWrapper user) {
        friendshipService.deleteFriendship(loggedUser.getId(), user.getId());
        deleteButton.setText("Enemy");
        addButton.setDisable(true);
        deleteButton.setDisable(true);
    }

    @Override
    protected void updateItem(NotificationsWrapper user, boolean empty) {
        super.updateItem(user, empty);
        setText(null);
        setGraphic(null);
        this.user = user;
        if (user != null && !empty) {
            if (user.getStatus().equals("approved")) {
                label.setText(user.getFirstName() + " is your friend since " + user.getDate());
                addButton.setVisible(false);
                deleteButton.setVisible(false);
            } else {
                label.setText(user.getFirstName() + " " + user.getLastName() + " " + user.getDate());
            }
            setGraphic(hbox);
        }
    }
}