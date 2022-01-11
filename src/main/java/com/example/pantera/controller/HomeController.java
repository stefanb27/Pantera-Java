package com.example.pantera.controller;

import com.example.pantera.domain.Event;
import com.example.pantera.domain.Page;
import com.example.pantera.repository.db.EventServiceDB;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import com.example.pantera.domain.Connection;
import com.example.pantera.domain.validators.FriendshipValidator;
import com.example.pantera.domain.validators.UserValidator;
import com.example.pantera.repository.db.FriendshipDBRepository;
import com.example.pantera.repository.db.UserDBRepository;
import com.example.pantera.service.FriendshipService;
import com.example.pantera.service.UserService;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HomeController {
    Connection connection = new Connection();
    UserDBRepository userDBRepository = new UserDBRepository(connection);
    FriendshipDBRepository friendshipDBRepository = new FriendshipDBRepository(connection);
    UserService userService = new UserService(userDBRepository, friendshipDBRepository, new UserValidator());
    FriendshipService friendshipService = new FriendshipService(userDBRepository, friendshipDBRepository, new FriendshipValidator());
    MenuButtonsController menuButtonsController;
    EventServiceDB eventServiceDB = new EventServiceDB(connection);

    private Stage dialogStage;
    private Page user;

    @FXML
    private ScrollPane feed = new ScrollPane();

    @FXML
    private VBox vBox;

    @FXML
    private ImageView logo;

    @FXML
    private ImageView menuEvent;

    @FXML
    private ImageView arrowEvent;

    @FXML
    private TextField nameEvent;

    @FXML
    private TextField dateEvent;

    @FXML
    private Button createEvent;

    @FXML
    private ImageView inboxButton;
    @FXML
    private ImageView homeButton;
    @FXML
    private ImageView searchButton;
    @FXML
    private ImageView profileButton;
    @FXML
    private ImageView notificationsButton;

    @FXML
    private void initialize() {}

    @FXML
    public void setService(Stage dialogStage, Page user) {
        this.dialogStage = dialogStage;
        this.user = user;
        this.menuButtonsController = new MenuButtonsController(dialogStage, user);
        loadEvents();
        logo.setVisible(true); menuEvent.setVisible(true);
    }

    public void loadEvents(){
        List<Event> allEvents = eventServiceDB.getAllEvents();
        for(Event event : allEvents){
            VBox eventBox = new VBox();
            vBox.setSpacing(10);
            vBox.setAlignment(Pos.CENTER);
            //eventBox.setPadding(new Insets(5, 5, 5, 5));
            HBox nameBox = new HBox(); nameBox.setAlignment(Pos.CENTER); nameBox.setPadding(new Insets(5, 0, 0, 0));
            HBox dateBox = new HBox(); dateBox.setAlignment(Pos.CENTER);
            HBox createdByEventBox = new HBox(); createdByEventBox.setAlignment(Pos.CENTER); createdByEventBox.setPadding(new Insets(0, 0, 5, 0));
            HBox buttonGoing = new HBox();
            Button goingEvent = new Button("Going"); goingEvent.setAlignment(Pos.TOP_CENTER);

            buttonGoing.getChildren().add(goingEvent);
            buttonGoing.setAlignment(Pos.CENTER);

            //goingEvent.setOnAction(Event -> handleInboxButton());

            goingEvent.setPrefWidth(100);
            goingEvent.getStylesheets().add("cssStyle/buttonLOGIN.css");
            eventBox.getChildren().clear();
            nameBox.getChildren().clear();
            dateBox.getChildren().clear();
            createdByEventBox.getChildren().clear();

            String nameOfEvent = event.getNameEvent();
            nameOfEvent = nameOfEvent.substring(0, 1).toUpperCase(Locale.ROOT) + nameOfEvent.substring(1);
            Label nameLabel = new Label(nameOfEvent);


            Label dateLabel = new Label(event.getDateTime() + " " + event.getHours());
            Label createdLabel = new Label("Created by " + event.getCreatedBy());

            nameLabel.getStylesheets().add("cssStyle/textTitle.css");
            dateLabel.getStylesheets().add("cssStyle/text.css");
            createdLabel.getStylesheets().add("cssStyle/text.css");
//            nameLabel.setStyle("-fx-fill: #ffffff; -fx-font-size: 18; -fx-font: Dubai;");
//            dateLabel.setStyle("-fx-fill: #ffffff; -fx-font-size: 13; -fx-font: Dubai;");
//            createdLabel.setStyle("-fx-fill: #ffffff; -fx-font-size: 13; -fx-font: Dubai;");

            nameBox.getChildren().add(nameLabel);
            dateBox.getChildren().add(dateLabel);
            createdByEventBox.getChildren().add(createdLabel);

            eventBox.getChildren().add(nameBox);
            eventBox.getChildren().add(dateBox);
            eventBox.getChildren().add(buttonGoing);
            eventBox.getChildren().add(createdByEventBox);
            eventBox.getStylesheets().add("cssStyle/hBoxEvent.css");
            eventBox.setStyle("-fx-border-color: #0DF6E3;" +
                    "-fx-border-radius: 20;" +
                    "-fx-background-color: #2F2E46;" +
                    "-fx-background-radius: 20");
            eventBox.setSpacing(20);
            vBox.getChildren().add(eventBox);
        }
    }

    public void handleNotificationsButton() {
        menuButtonsController.moveToNotificationsButton();
    }

    public void handleSearchButton() {
        menuButtonsController.moveToSearchButton();
    }

    public void handleProfileButton() {
        menuButtonsController.moveToProfileButton();
    }

    public void handleInboxButton() { menuButtonsController.moveToInboxController();}


    public void handleSaveEvent(){
        String nameEventText = nameEvent.getText();
        String creator = user.getFirstName();
        String dateEventText = dateEvent.getText();
        System.out.println(dateEvent.getText().length());
        String date = dateEventText.split(" ")[0];
        System.out.println(date);
        String hours = dateEventText.split(" ")[1];
        System.out.println(hours);

        Event newEvent = new Event(nameEventText, creator, date, hours);
        eventServiceDB.save(newEvent);
//        handleArrowEvent();
        //2022-01-09 09:00
    }

    public void handleMenuEvent() {
        logo.setVisible(false);
        menuEvent.setVisible(false);
        nameEvent.setVisible(true);
        dateEvent.setVisible(true);
        createEvent.setVisible(true);
        arrowEvent.setVisible(true);
    }

    public void handleArrowEvent() {
        logo.setVisible(true);
        nameEvent.setVisible(false);
        dateEvent.setVisible(false);
        createEvent.setVisible(false);
        menuEvent.setVisible(true);
        menuButtonsController.moveToHomeButton();
    }
}