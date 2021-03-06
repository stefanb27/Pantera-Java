package com.example.pantera.controller;

import com.example.pantera.domain.*;
import com.example.pantera.repository.db.EventDBRepository;
import com.example.pantera.service.ControllerService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import com.example.pantera.domain.validators.FriendshipValidator;
import com.example.pantera.domain.validators.UserValidator;
import com.example.pantera.repository.db.FriendshipDBRepository;
import com.example.pantera.repository.db.MessageDBRepository;
import com.example.pantera.repository.db.UserDBRepository;
import com.example.pantera.service.FriendshipService;
import com.example.pantera.service.MessageService;
import com.example.pantera.service.UserService;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class LogInController {
    Connection connection = new Connection();
    UserDBRepository userDBRepository = new UserDBRepository(connection);
    FriendshipDBRepository friendshipDBRepository = new FriendshipDBRepository(connection);
    UserService userService = new UserService(userDBRepository, friendshipDBRepository, new UserValidator());
    FriendshipService friendshipService = new FriendshipService(userDBRepository, friendshipDBRepository, new FriendshipValidator());
    MessageDBRepository messageDBRepository = new MessageDBRepository(connection);
    MessageService messageService = new MessageService(userDBRepository, friendshipDBRepository, messageDBRepository);
    ControllerService controllerService = new ControllerService(userDBRepository, friendshipDBRepository, messageDBRepository, connection);
    EventDBRepository eventDBRepository = new EventDBRepository(connection);
    Stage logInStage;

    @FXML
    private Button logInButton;
    @FXML
    private Button signupButton;
    @FXML
    private Button forgotPasswordButton;
    @FXML
    private TextField usernameText;
    @FXML
    private TextField passwordText;

    public void setService(Stage stage){
        this.logInStage = stage;
    }

    @FXML
    private void initialize() {
//        usernameText.setText("john@gmail.com");
//        passwordText.setText("123456");
//        System.out.println(controllerService.hashPassword("123456"));
//        System.out.println(controllerService.hashPassword("qwerty"));
    }

    public void onLoginButtonClick() {
        String email = usernameText.getText();
        String password = passwordText.getText();
        String hashedPass = controllerService.hashPassword(password);
        Page user = controllerService.checkLogIn(email, hashedPass);
        if (user == null) {
            usernameText.setText("");
            passwordText.setText("");
            usernameText.setPromptText("Invalid login credentials");
        } else {
            loadPage(user);
            runUser(user);
        }
    }

    public void loadPage(Page user){
        user.setFriends(controllerService.findFriends(user)); //pt profile
        user.setRequestsReceived(controllerService.notificationsFilter(user));
        user.setRequestsSent(controllerService.findRequestSent(user));
        user.setEvents(eventDBRepository.getAllEvents());
        //user.setMessages(controllerService.findAllMessForAnUser(user));
    }

    public void runUser(Page user){
        MenuButtonsController menuButtonsController = new MenuButtonsController(logInStage, user);
        menuButtonsController.moveToHomeButton();
    }

    public void onSignUpButtonClick() {
        MenuButtonsController menuButtonsController = new MenuButtonsController(logInStage, null);
        menuButtonsController.moveToSignUpController();
    }

    public void onForgotPasswordClick() {

    }
}
