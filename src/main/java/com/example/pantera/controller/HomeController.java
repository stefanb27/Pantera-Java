package com.example.pantera.controller;

import com.example.pantera.domain.NiceEvent;
import com.example.pantera.domain.Page;
import com.example.pantera.domain.Tuple;
import com.example.pantera.events.FriendshipChangeEvent;
import com.example.pantera.repository.db.EventDBRepository;
import com.example.pantera.repository.paging.PagingRepository;
import com.example.pantera.service.EventsService;
import com.example.pantera.utils.Observer;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
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
import javafx.util.Callback;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class HomeController implements Observer<FriendshipChangeEvent> {
    Connection connection = new Connection();
    UserDBRepository userDBRepository = new UserDBRepository(connection);
    FriendshipDBRepository friendshipDBRepository = new FriendshipDBRepository(connection);
    UserService userService = new UserService(userDBRepository, friendshipDBRepository, new UserValidator());
    FriendshipService friendshipService = new FriendshipService(userDBRepository, friendshipDBRepository, new FriendshipValidator());
    MenuButtonsController menuButtonsController;
    EventDBRepository eventDBRepository = new EventDBRepository(connection);
    EventsService eventsService = new EventsService();
    PagingRepository<Long, NiceEvent> pagingRepository = new EventDBRepository(connection);

    private Stage dialogStage;
    private Page user;

    @FXML
    private ScrollPane feed = new ScrollPane();

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
    private Pagination pageView;

    @FXML
    private void initialize() {
        Tooltip.install(inboxButton, new Tooltip("Inbox"));
        Tooltip.install(notificationsButton, new Tooltip("Notifications"));
        Tooltip.install(searchButton, new Tooltip("Search"));
        Tooltip.install(profileButton, new Tooltip("Profile"));
        eventsService.setPageSize(2);
        eventsService.addObserver(this);
//        eventsService.getAll().forEach(System.out::println);
//        System.out.println("Elements on page 1:");
//        eventsService.getMessagesOnPage(0).stream().forEach(System.out::println);
//        System.out.println("Elements on page 2:");
//        eventsService.getNextMessages().stream().forEach(System.out::println);
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

    public VBox helper() {
        VBox vBox = new VBox();
        List<NiceEvent> pageList = (List<NiceEvent>) eventsService.getAll();
        System.out.println(Math.ceil(pageList.size() / 2.0));
        pageView.setPageCount((int) Math.ceil(pageList.size() / 2.0));
        for (NiceEvent niceEvent : eventsService.getMessagesOnPage(pageView.getCurrentPageIndex())) {
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

            if (eventDBRepository.isGoing(user.getId(), niceEvent.getId())) {
                goingEvent = new Button("Not interested");
                goingEvent.setOnAction(actionEvent -> handleGoingYes(actionEvent.getSource(), niceEvent.getId(), (Object) dateBox, (Object) buttonGoing));

            } else {
                goingEvent = new Button("Going");
                goingEvent.setOnAction(actionEvent -> handleGoingNot(actionEvent.getSource(), niceEvent.getId(), (Object) dateBox, (Object) buttonGoing));
            }

            //eventBox.setOnMouseEntered(action -> handler(action.getSource()));
            goingEvent.setAlignment(Pos.TOP_CENTER);
            goingEvent.setPrefWidth(100);
            goingEvent.getStylesheets().add("cssStyle/buttonLOGIN.css");

            eventBox.getChildren().clear();
            nameBox.getChildren().clear();
            dateBox.getChildren().clear();
            createdByEventBox.getChildren().clear();

            String nameOfEvent = niceEvent.getNameEvent();
            nameOfEvent = nameOfEvent.substring(0, 1).toUpperCase(Locale.ROOT) + nameOfEvent.substring(1);
            Label nameLabel = new Label(nameOfEvent);
            Label dateLabel = new Label(niceEvent.getDateTime() + " " + niceEvent.getHours());
            Label createdLabel = new Label("Created by " + niceEvent.getCreatedBy());

            Tuple<Long, Long> result = getTimeLeft(niceEvent.getDateTime(), niceEvent.getHours());

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
            if (!eventDBRepository.isGoing(user.getId(), niceEvent.getId())) { numberDays.setVisible(false); numberHours.setVisible(false); days.setVisible(false); hours.setVisible(false); }

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
        return vBox;
    }

    public void loadEvents() {
        pageView.setPageFactory(new Callback<Integer, Node>() {
            @Override
            public Node call(Integer param) {
                return helper();
            }
        });
    }

    @Override
    public void update(FriendshipChangeEvent friendshipChangeEvent) {
        loadEvents();
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
        if (!nameEventText.equals("") && !dateEventText.equals("")) {
            String date = dateEventText.split(" ")[0];
            String hours = dateEventText.split(" ")[1];
            NiceEvent newNiceEvent = new NiceEvent(nameEventText, creator, date, hours);
            eventsService.saveEvent(newNiceEvent);
            handleArrowEvent();
        } else {
            nameEvent.setPromptText("Choose a name");
            nameEvent.setStyle("-fx-prompt-text-fill: #ffffff;");
            dateEvent.setPromptText(" and a date!");
            dateEvent.setStyle("-fx-prompt-text-fill: #ffffff;");
            nameEvent.clear();
            dateEvent.clear();
        }

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
        arrowEvent.setVisible(false);
        //menuButtonsController.moveToHomeButton();
    }

    public void handleGoingNot(Object goingEvent, Long idEvent, Object hbox1, Object hbox2) {
        eventDBRepository.saveUserEvent(user.getId(), idEvent);
        Button button = (Button) goingEvent;
        HBox hBox11 = (HBox) hbox1; HBox hBox22 = (HBox) hbox2;
        //numberDays.setVisible(true); days.setVisible(true); hours.setVisible(true);
        button.setText("Not interested");
        handlerShow(hBox11, hBox22);
        button.setOnAction(event -> handleGoingYes(event.getSource(), idEvent, hBox11, hBox22));
    }

    public void handleGoingYes(Object goingEvent, Long idEvent, Object hbox1, Object hbox2) {
        eventDBRepository.deletUserEvent(user.getId(), idEvent);
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