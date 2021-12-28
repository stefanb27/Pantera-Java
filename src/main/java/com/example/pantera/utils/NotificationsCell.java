package com.example.pantera.utils;

import com.example.pantera.domain.Connection;
import com.example.pantera.domain.NotificationsWrapper;
import com.example.pantera.domain.User;
import com.example.pantera.domain.validators.FriendshipValidator;
import com.example.pantera.domain.validators.UserValidator;
import com.example.pantera.repository.db.FriendshipDBRepository;
import com.example.pantera.repository.db.MessageDBRepository;
import com.example.pantera.repository.db.UserDBRepository;
import com.example.pantera.service.FriendshipService;
import com.example.pantera.service.MessageService;
import com.example.pantera.service.UserService;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;

import java.time.LocalDateTime;

public class NotificationsCell extends ListCell<NotificationsWrapper> {
    HBox hbox = new HBox();
    Label label = new Label("");
    Pane pane = new Pane();
    Button addButton = new Button("accept");
    Button deleteButton = new Button("delete");
    User loggedUser;
    NotificationsWrapper user;

    Connection connection = new Connection();
    UserDBRepository userDBRepository = new UserDBRepository(connection);
    FriendshipDBRepository friendshipDBRepository = new FriendshipDBRepository(connection);
    UserService userService = new UserService(userDBRepository, friendshipDBRepository, new UserValidator());
    FriendshipService friendshipService = new FriendshipService(userDBRepository, friendshipDBRepository, new FriendshipValidator());
    MessageDBRepository messageDBRepository = new MessageDBRepository(connection);
    MessageService messageService = new MessageService(userDBRepository, friendshipDBRepository, messageDBRepository);


    public NotificationsCell(User loggedUser) {
        super();
        this.loggedUser = loggedUser;
        hbox.getChildren().addAll(label, pane, addButton, deleteButton);
        HBox.setHgrow(pane, Priority.ALWAYS);
        addButton.setOnAction(event -> handleAddButton(user));
        deleteButton.setOnAction(event -> handleDeleteButton(user));
    }

    public void handleAddButton(NotificationsWrapper user) {
        friendshipService.approveFriendship(loggedUser.getId(), user.getId());
        addButton.setText("frend");
    }

    public void handleDeleteButton(NotificationsWrapper user) {
        friendshipService.deleteFriendship(loggedUser.getId(), user.getId());
        deleteButton.setText("enemy");
    }

    @Override
    protected void updateItem(NotificationsWrapper user, boolean empty) {
        super.updateItem(user, empty);
        setText(null);
        setGraphic(null);
        this.user = user;
        if (user != null && !empty) {
            label.setText(user.getId() + " " + user.getFirstName() + " " + user.getLastName() + " " + user.getDate());
            setGraphic(hbox);
        }
    }
}