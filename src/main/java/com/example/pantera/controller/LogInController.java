package com.example.pantera.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
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

import java.io.IOException;

public class LogInController {
    Connection connection = new Connection();
    UserDBRepository userDBRepository = new UserDBRepository(connection);
    FriendshipDBRepository friendshipDBRepository = new FriendshipDBRepository(connection);
    UserService userService = new UserService(userDBRepository, friendshipDBRepository, new UserValidator());
    FriendshipService friendshipService = new FriendshipService(userDBRepository, friendshipDBRepository, new FriendshipValidator());
    MessageDBRepository messageDBRepository = new MessageDBRepository(connection);
    MessageService messageService = new MessageService(userDBRepository, friendshipDBRepository, messageDBRepository);

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

    @FXML
    private void initialize() {
        usernameText.setText("god@gmail.com");
        passwordText.setText("123");
    }

    public void onLoginButtonClick() {
        String email = usernameText.getText().toString();
        User user = userService.checkEmail(email);
        if (user == null || (!user.getEmail().equals(email) && !passwordText.getText().toString().equals(user.getPassword()))) {
            usernameText.setText("");
            passwordText.setText("");
            usernameText.setPromptText("Invalid login credentials");
        } else {
            runUser(user);
        }
    }

    public void runUser(User user){
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/views/home.fxml"));

        try {
            BorderPane root = (BorderPane) loader.load();
            Stage dialogStage = new Stage();
            dialogStage.setTitle("User Interface");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            //dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(root);
            dialogStage.setScene(scene);

            HomeController userViewController = loader.getController();
            userViewController.setService(dialogStage, user);
            dialogStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onSignUpButtonClick() {

    }

    public void onForgotPasswordClick() {

    }
}
