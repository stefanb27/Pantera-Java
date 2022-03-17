package com.example.pantera.controller;

import com.example.pantera.domain.Connection;
import com.example.pantera.domain.Page;
import com.example.pantera.domain.User;
import com.example.pantera.domain.validators.FriendshipValidator;
import com.example.pantera.domain.validators.UserValidator;
import com.example.pantera.repository.db.EventDBRepository;
import com.example.pantera.repository.db.FriendshipDBRepository;
import com.example.pantera.repository.db.MessageDBRepository;
import com.example.pantera.repository.db.UserDBRepository;
import com.example.pantera.service.ControllerService;
import com.example.pantera.service.FriendshipService;
import com.example.pantera.service.MessageService;
import com.example.pantera.service.UserService;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.util.List;

public class SignUpController {
    Connection connection = new Connection();
    UserDBRepository userDBRepository = new UserDBRepository(connection);
    FriendshipDBRepository friendshipDBRepository = new FriendshipDBRepository(connection);
    UserService userService = new UserService(userDBRepository, friendshipDBRepository, new UserValidator());
    FriendshipService friendshipService = new FriendshipService(userDBRepository, friendshipDBRepository, new FriendshipValidator());
    MessageDBRepository messageDBRepository = new MessageDBRepository(connection);
    MessageService messageService = new MessageService(userDBRepository, friendshipDBRepository, messageDBRepository);
    ControllerService controllerService = new ControllerService(userDBRepository, friendshipDBRepository, messageDBRepository, connection);
    EventDBRepository eventDBRepository = new EventDBRepository(connection);
    Stage dialogStage;

    @FXML
    private Button logInButton;
    @FXML
    private Button signupButton;
    @FXML
    private TextField firstNameText;
    @FXML
    private TextField lastNameText;
    @FXML
    private TextField emailText;
    @FXML
    private TextField passwordText;
    @FXML
    private TextField confirmText;
    @FXML
    private Label infoLabel;

    public void setService(Stage stage){
        this.dialogStage = stage;
    }

    @FXML
    private void initialize() {

    }

    public void handleOnSignUpButton() {
        String firstName = firstNameText.getText();
        String lastName = lastNameText.getText();
        String password = passwordText.getText();
        String confirm = confirmText.getText();
        if (firstNameText.getText().equals("") || lastNameText.getText().equals("") ||
                emailText.getText().equals("") || passwordText.getText().equals("") || confirmText.getText().equals("")) {
            infoLabel.setText("Please complete all the fields.");
        } else if (!passwordText.getText().equals(confirmText.getText())) {
            infoLabel.setText("Passwords do not match.");
            passwordText.clear();
            confirmText.clear();
        } else {
            String email = emailText.getText();
            List<User> userList = (List<User>) userService.getAllUsers();
            boolean ok = true;
            for (User x : userList) {
                if (x.getEmail().equals(email)) {
                    ok = false;
                }
            }
            if (!ok) {
                infoLabel.setText("This email is already used.");
                emailText.clear();
                passwordText.clear();
                confirmText.clear();
            } else {
                Page user = new Page(firstName, lastName, email, password);
                Long id = userService.getMaxId() + 1;
                user.setId(id);
                String hashedPassword = controllerService.hashPassword(password);
                userService.addUser(firstName, lastName, id, email, hashedPassword);
                //infoLabel.setText("Sign up successful");
                MenuButtonsController menuButtonsController = new MenuButtonsController(dialogStage, user);
                loadPage(user);
                menuButtonsController.moveToHomeButton();
            }
        }
    }

    public void onSignInButton(){
        MenuButtonsController menuButtonsController = new MenuButtonsController(dialogStage, null);
        menuButtonsController.moveToLogInController();
    }

    public void loadPage(Page user){
        user.setFriends(controllerService.findFriends(user)); //pt profile
        user.setRequestsReceived(controllerService.notificationsFilter(user));
        user.setRequestsSent(controllerService.findRequestSent(user));
        user.setEvents(eventDBRepository.getAllEvents());
        //user.setMessages(controllerService.findAllMessForAnUser(user));
    }
}
