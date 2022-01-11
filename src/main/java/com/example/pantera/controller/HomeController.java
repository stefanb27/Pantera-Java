package com.example.pantera.controller;

import com.example.pantera.domain.Event;
import com.example.pantera.domain.Page;
import com.example.pantera.domain.Tuple;
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
import javafx.scene.shape.Box;
import javafx.stage.Stage;
import com.example.pantera.domain.Connection;
import com.example.pantera.domain.validators.FriendshipValidator;
import com.example.pantera.domain.validators.UserValidator;
import com.example.pantera.repository.db.FriendshipDBRepository;
import com.example.pantera.repository.db.UserDBRepository;
import com.example.pantera.service.FriendshipService;
import com.example.pantera.service.UserService;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Calendar;
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
    private Button goingEvent;

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
    private TextField days;

    @FXML
    private TextField hours;

    @FXML
    private TextField numberDays;

    @FXML
    private TextField numberHours;

    @FXML
    private void initialize() {
    }

    @FXML
    public void setService(Stage dialogStage, Page user) {
        this.dialogStage = dialogStage;
        this.user = user;
        this.menuButtonsController = new MenuButtonsController(dialogStage, user);
        loadEvents();
        logo.setVisible(true);
        menuEvent.setVisible(true);
    }

    public void loadEvents() {
        List<Event> allEvents = eventServiceDB.getAllEvents();
        for (Event event : allEvents) {
            VBox eventBox = new VBox();
            vBox.setSpacing(10);
            vBox.setAlignment(Pos.CENTER);
            //eventBox.setPadding(new Insets(5, 5, 5, 5));
            HBox nameBox = new HBox();
            nameBox.setAlignment(Pos.CENTER);
            nameBox.setPadding(new Insets(5, 0, 0, 0));
            HBox dateBox = new HBox(); //dateBox.setAlignment(Pos.CENTER);
            HBox createdByEventBox = new HBox();
            createdByEventBox.setAlignment(Pos.CENTER);
            createdByEventBox.setPadding(new Insets(0, 0, 5, 0));
            HBox buttonGoing = new HBox();

            if (eventServiceDB.isGoing(user.getId(), event.getId())) {
                goingEvent = new Button("Not interested");
                goingEvent.setOnAction(actionEvent -> handleGoingYes(actionEvent.getSource(), event.getId(), (Object) dateBox, (Object) buttonGoing));

            } else {
                goingEvent = new Button("Going");
                goingEvent.setOnAction(actionEvent -> handleGoingNot(actionEvent.getSource(), event.getId(), (Object) dateBox, (Object) buttonGoing));
            }

            //eventBox.setOnMouseEntered(action -> handler(action.getSource()));
            goingEvent.setAlignment(Pos.TOP_CENTER);
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

            Tuple<Long, Long> result = getTimeLeft(event.getDateTime(), event.getHours());

            nameLabel.getStylesheets().add("cssStyle/textTitle.css");
            dateLabel.getStylesheets().add("cssStyle/text.css");
            createdLabel.getStylesheets().add("cssStyle/text.css");
//
            nameBox.getChildren().add(nameLabel);
            createdByEventBox.getChildren().add(createdLabel);

            dateBox.setSpacing(25);
            dateBox.setAlignment(Pos.CENTER);
            buttonGoing.setSpacing(52);
            numberDays = new TextField(String.valueOf(result.getLeft())); numberDays.setMaxWidth(40); numberDays.setEditable(false);
            numberHours = new TextField(String.valueOf(result.getRight())); numberHours.setMaxWidth(40); numberHours.setEditable(false);
            numberDays.setStyle("-fx-background-color: transparent; -fx-text-fill: #ffffff; -fx-font-weight: bold; -fx-font-size: 14;");
            numberHours.setStyle("-fx-background-color: transparent; -fx-text-fill: #ffffff; -fx-font-weight: bold; -fx-font-size: 14;");
            days = new TextField("DAYS"); days.setMaxWidth(90); days.setEditable(false);
            hours = new TextField("HOURS"); hours.setMaxWidth(90); hours.setEditable(false);
            days.setStyle("-fx-background-color: transparent; -fx-text-fill: #0DF6E3; -fx-font-weight: bold; -fx-font-size: 18;");
            hours.setStyle("-fx-background-color: transparent; -fx-text-fill: #0DF6E3; -fx-font-weight: bold; -fx-font-size: 18;");
            dateBox.getChildren().addAll(days, dateLabel, hours);

            buttonGoing.getChildren().addAll(numberDays, goingEvent, numberHours);
            if (!eventServiceDB.isGoing(user.getId(), event.getId())) { numberDays.setVisible(false); numberHours.setVisible(false); days.setVisible(false); hours.setVisible(false); }

            buttonGoing.setAlignment(Pos.CENTER);

            eventBox.getChildren().add(nameBox);
            eventBox.getChildren().add(dateBox);
            eventBox.getChildren().add(buttonGoing);
            eventBox.getChildren().add(createdByEventBox);

            eventBox.setStyle("-fx-border-color: #0DF6E3;" +
                    "-fx-border-radius: 20;" +
                    "-fx-background-color: #2F2E46;" +
                    "-fx-background-radius: 20");
            eventBox.setSpacing(5);
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

    public void handleInboxButton() {
        menuButtonsController.moveToInboxController();
    }


    public void handleSaveEvent() {
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

    public void handleGoingNot(Object goingEvent, Long idEvent, Object hbox1, Object hbox2) {
        eventServiceDB.saveUserEvent(user.getId(), idEvent);
        Button button = (Button) goingEvent;
        HBox hBox11 = (HBox) hbox1; HBox hBox22 = (HBox) hbox2;
        //numberDays.setVisible(true); days.setVisible(true); hours.setVisible(true);
        button.setText("Not interested");
        handlerShow(hBox11, hBox22);
        button.setOnAction(event -> handleGoingYes(event.getSource(), idEvent, hBox11, hBox22));
    }

    public void handleGoingYes(Object goingEvent, Long idEvent, Object hbox1, Object hbox2) {
        eventServiceDB.deletUserEvent(user.getId(), idEvent);
        Button button = (Button) goingEvent;
        HBox hBox11 = (HBox) hbox1; HBox hBox22 = (HBox) hbox2;
        //numberDays.setVisible(false); numberHours.setVisible(false); days.setVisible(false); hours.setVisible(false);
        button.setText("Going");
        handlerHide(hBox11, hBox22);
        button.setOnAction(event -> handleGoingNot(event.getSource(), idEvent, hBox11, hBox22));
    }

    public Tuple<Long, Long> getTimeLeft(String date, String hours) {
        int year = Integer.parseInt(date.substring(0, 4));
        int dayOfMonth = Integer.parseInt(date.substring(8, 10));
        int month = Integer.parseInt(date.substring(5, 7));
        int hour = Integer.parseInt(hours.substring(0, 2));

        Calendar thatDay = Calendar.getInstance();

        thatDay.set(Calendar.HOUR_OF_DAY, hour);/*here Add ur Time */
        thatDay.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        thatDay.set(Calendar.MONTH, month); // 0-11 so 1 less
        thatDay.set(Calendar.YEAR, year);

        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, LocalDateTime.now().getHour());/*here Add ur Time */
        today.set(Calendar.DAY_OF_MONTH, LocalDateTime.now().getDayOfMonth());
        today.set(Calendar.MONTH, LocalDateTime.now().getMonthValue());
        today.set(Calendar.YEAR, LocalDateTime.now().getYear());
        long diff = thatDay.getTimeInMillis() - today.getTimeInMillis();

        System.out.println ("Days: " + diff / 1000 / 60 / 60 / 24);
        long days = diff / 1000 / 60 / 60 / 24;
        long h = 24 - hour;
        return new Tuple<>(days, h);
    }

    public void handlerHide(HBox hBox1, HBox hBox2){
        hBox1.getChildren().get(0).setVisible(false);
        hBox1.getChildren().get(2).setVisible(false);
        hBox2.getChildren().get(0).setVisible(false);
        hBox2.getChildren().get(2).setVisible(false);
    }

    public void handlerShow(HBox hBox1, HBox hBox2){
        hBox1.getChildren().get(0).setVisible(true);
        hBox1.getChildren().get(2).setVisible(true);
        hBox2.getChildren().get(0).setVisible(true);
        hBox2.getChildren().get(2).setVisible(true);
    }
}