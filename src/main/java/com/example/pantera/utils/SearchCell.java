package com.example.pantera.utils;

import com.example.pantera.domain.Connection;
import com.example.pantera.domain.Tuple;
import com.example.pantera.domain.User;
import com.example.pantera.domain.validators.FriendshipValidator;
import com.example.pantera.domain.validators.UserValidator;
import com.example.pantera.domain.validators.ValidateException;
import com.example.pantera.repository.db.FriendshipDBRepository;
import com.example.pantera.repository.db.MessageDBRepository;
import com.example.pantera.repository.db.UserDBRepository;
import com.example.pantera.service.ControllerService;
import com.example.pantera.service.FriendshipService;
import com.example.pantera.service.MessageService;
import com.example.pantera.service.UserService;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;

public class SearchCell extends ListCell<User> {
    HBox hbox = new HBox();
    Label label = new Label("");
    Pane pane = new Pane();
    Button button = new Button("Add");
//    Image image1 = new Image();
//    Image image2 = new Image();
//    ImageView sendRequest = new Image(image1);
//    ImageView removeRequest = new Image(image2);

    User loggedUser;
    User user;

    Connection connection = new Connection();
    UserDBRepository userDBRepository = new UserDBRepository(connection);
    FriendshipDBRepository friendshipDBRepository = new FriendshipDBRepository(connection);
    UserService userService = new UserService(userDBRepository, friendshipDBRepository, new UserValidator());
    FriendshipService friendshipService = new FriendshipService(userDBRepository, friendshipDBRepository, new FriendshipValidator());
    MessageDBRepository messageDBRepository = new MessageDBRepository(connection);
    MessageService messageService = new MessageService(userDBRepository, friendshipDBRepository, messageDBRepository);
    ControllerService controllerService = new ControllerService(userDBRepository, friendshipDBRepository, messageDBRepository, connection);


    public SearchCell(User loggedUser) {
        super();
        this.loggedUser = loggedUser;
        //this.button.setStyle("-fx-background-color: red;");
//        this.button.setStyle(" -fx-background-color: #0DF6E3;\n" +
//                "    -fx-background-radius: 50;\n" +
//                "    -fx-text-fill: #1f1a30;");
        this.button.getStylesheets().add("cssStyle/buttonLOGIN.css");
        this.button.setPrefWidth(60);
        hbox.getChildren().addAll(label, pane, button);
        HBox.setHgrow(pane, Priority.ALWAYS);
        button.setOnAction(event -> handleAddButton(user));
    }

    public void handleAddButton(User user) {
        if (this.button.getText().equals("Add")) {
            friendshipService.addFriendship(loggedUser.getId(), user.getId());
            this.button.setText("Remove");
            //this.button.setStyle("-fx-border-radius: 20");
            //this.button.setStyle("-fx-border-radius: 20");
                   // getStyleClass().add("X:\\pantera\\src\\main\\resources\\cssStyle\\buttonLOGIN.css");
        } else if (this.button.getText().equals("Remove")) {
            friendshipService.deleteFriendship(loggedUser.getId(), user.getId());
            this.button.setText("Add");
            //this.button.setStyle("-fx-border-radius: 20");
            //this.button.getStyleClass().add("X:\\pantera\\src\\main\\resources\\cssStyle\\buttonLOGIN.css");
        }
    }

    @Override
    protected void updateItem(User user, boolean empty) {
        super.updateItem(user, empty);
        setText(null);
        setGraphic(null);
        this.user = user;
        if (user != null) {
                if (controllerService.findPendingFriendship(loggedUser, user)) {
                    this.button.setText("Remove");
                    //this.button.getStyleClass().add("X:\\pantera\\src\\main\\resources\\cssStyle\\buttonLOGIN.css");
                }
                if (controllerService.findApprovedFriendship(loggedUser, user)) {
                    this.button.setText("Friend");
                    //this.button.getStyleClass().add("X:\\pantera\\src\\main\\resources\\cssStyle\\buttonLOGIN.css");
                }
        }
        if (user != null && !empty) {
            label.setText(user.toString());
            setGraphic(hbox);
        }
    }
}