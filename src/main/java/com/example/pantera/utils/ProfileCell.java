package com.example.pantera.utils;

import com.example.pantera.domain.Connection;
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
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;

public class ProfileCell extends ListCell<User> {
    HBox hbox = new HBox();
    Label label = new Label("");
    Pane pane = new Pane();
    Button button = new Button();
    User loggedUser;
    User user;

    Connection connection = new Connection();
    UserDBRepository userDBRepository = new UserDBRepository(connection);
    FriendshipDBRepository friendshipDBRepository = new FriendshipDBRepository(connection);
    UserService userService = new UserService(userDBRepository, friendshipDBRepository, new UserValidator());
    FriendshipService friendshipService = new FriendshipService(userDBRepository, friendshipDBRepository, new FriendshipValidator());
    MessageDBRepository messageDBRepository = new MessageDBRepository(connection);
    MessageService messageService = new MessageService(userDBRepository, friendshipDBRepository, messageDBRepository);


    public ProfileCell(User loggedUser) {
        super();
        this.loggedUser = loggedUser;
        this.button.getStylesheets().add("cssStyle/buttonLOGIN.css");
        ImageView imageView2 = new ImageView("X:\\pantera\\src\\main\\resources\\images\\x.png");
        imageView2.setFitWidth(13);
        imageView2.setFitHeight(13);
        this.button.setGraphic(imageView2);
        hbox.getChildren().addAll(label, pane, button);
        HBox.setHgrow(pane, Priority.ALWAYS);
        button.setOnAction(event -> handleDeleteButton(user));
    }

    public void handleDeleteButton(User user) {
        friendshipService.deleteFriendship(loggedUser.getId(), user.getId());
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