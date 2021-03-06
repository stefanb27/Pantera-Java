package com.example.pantera.utils;

import com.example.pantera.controller.ProfileController;
import com.example.pantera.domain.Connection;
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
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;

public class ProfileCell extends ListCell<User> {
    HBox hbox = new HBox();
    Label label = new Label("");
    Pane pane = new Pane();
    Button button = new Button("Remove");
    Page loggedUser;
    User user;
    FriendshipService friendshipService;

    Connection connection = new Connection();
    UserDBRepository userDBRepository = new UserDBRepository(connection);
    FriendshipDBRepository friendshipDBRepository = new FriendshipDBRepository(connection);
    UserService userService = new UserService(userDBRepository, friendshipDBRepository, new UserValidator());
    //FriendshipService friendshipService = new FriendshipService(userDBRepository, friendshipDBRepository, new FriendshipValidator());
    MessageDBRepository messageDBRepository = new MessageDBRepository(connection);
    MessageService messageService = new MessageService(userDBRepository, friendshipDBRepository, messageDBRepository);


    public ProfileCell(Page loggedUser, FriendshipService friendshipService) {
        super();
        this.loggedUser = loggedUser;
        this.button.getStylesheets().add("cssStyle/buttonLOGIN.css");
        hbox.getChildren().addAll(label, pane, button);
        HBox.setHgrow(pane, Priority.ALWAYS);
        button.setOnAction(event -> handleDeleteButton(user));
        this.friendshipService = friendshipService;
    }

    public void handleDeleteButton(User user) {
        friendshipService.deleteFriendship(loggedUser.getId(), user.getId());
        loggedUser.removeFriend(user);
    }

    @Override
    protected void updateItem(User user, boolean empty) {
        super.updateItem(user, empty);
        setText(null);
        setGraphic(null);
        this.user = user;
        if (user != null && !empty) {
            label.setText(user.toString());
            setGraphic(hbox);
        }
    }
}