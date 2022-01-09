package com.example.pantera.utils;

import com.example.pantera.domain.Connection;
import com.example.pantera.domain.Page;
import com.example.pantera.domain.User;
import com.example.pantera.domain.validators.FriendshipValidator;
import com.example.pantera.domain.validators.UserValidator;
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
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;

import java.util.ArrayList;
import java.util.List;

public class GroupCell extends ListCell<User> {
    HBox hbox = new HBox();
    Label label = new Label("");
    Pane pane = new Pane();
    Button button = new Button("Add");
//    Image image1 = new Image();
//    Image image2 = new Image();
//    ImageView sendRequest = new Image(image1);
//    ImageView removeRequest = new Image(image2);

    Page loggedUser;
    User user;
    List<User> groupGuys = new ArrayList<>();

    Connection connection = new Connection();
    UserDBRepository userDBRepository = new UserDBRepository(connection);
    FriendshipDBRepository friendshipDBRepository = new FriendshipDBRepository(connection);
    UserService userService = new UserService(userDBRepository, friendshipDBRepository, new UserValidator());
    FriendshipService friendshipService = new FriendshipService(userDBRepository, friendshipDBRepository, new FriendshipValidator());
    MessageDBRepository messageDBRepository = new MessageDBRepository(connection);
    MessageService messageService = new MessageService(userDBRepository, friendshipDBRepository, messageDBRepository);
    ControllerService controllerService = new ControllerService(userDBRepository, friendshipDBRepository, messageDBRepository, connection);


    public GroupCell(Page loggedUser, List<User> groupGuys) {
        super();
        this.loggedUser = loggedUser;
        this.groupGuys = groupGuys;
        this.button.getStylesheets().add("cssStyle/buttonLOGIN.css");
        this.button.setPrefWidth(70);
        hbox.getChildren().addAll(label, pane, button);
        HBox.setHgrow(pane, Priority.ALWAYS);
        button.setOnAction(event -> handleAddButton(user));
    }
    //enum
    public void handleAddButton(User user) {
        if (this.button.getText().equals("Add")) {
            groupGuys.add(user);
            this.button.setText("Remove");
        } else if (this.button.getText().equals("Remove")) {
            groupGuys.remove(user);
            this.button.setText("Add");
        }
    }

    @Override
    protected void updateItem(User user, boolean empty) {
        super.updateItem(user, empty);
        setText(null);
        setGraphic(null);
        this.user = user;
//        if (user != null) {
//            if (controllerService.findPendingFriendship(loggedUser, user)) {
//                this.button.setText("Remove");
//                //this.button.getStyleClass().add("X:\\pantera\\src\\main\\resources\\cssStyle\\buttonLOGIN.css");
//            }
//            if (controllerService.findApprovedFriendship(loggedUser, user)) {
//                this.button.setText("Friend");
//                //this.button.getStyleClass().add("X:\\pantera\\src\\main\\resources\\cssStyle\\buttonLOGIN.css");
//            }
//        }
        if (user != null && !empty) {
            label.setText(user.toString());
            setGraphic(hbox);
        }
    }

    public List<User> getGroupGuys(){
        return groupGuys;
    }
}
