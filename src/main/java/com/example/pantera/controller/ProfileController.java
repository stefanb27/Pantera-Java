package com.example.pantera.controller;

import com.example.pantera.domain.Message;
import com.example.pantera.domain.Page;
import com.example.pantera.events.FriendshipChangeEvent;
import com.example.pantera.printers.PdfPrinter;
import com.example.pantera.service.ControllerService;
import com.example.pantera.utils.Observer;
import com.example.pantera.utils.ProfileCell;
import com.example.pantera.utils.SearchCell;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Font;
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
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class ProfileController implements Observer<FriendshipChangeEvent> {
    Connection connection = new Connection();
    UserDBRepository userDBRepository = new UserDBRepository(connection);
    FriendshipDBRepository friendshipDBRepository = new FriendshipDBRepository(connection);
    UserService userService = new UserService(userDBRepository, friendshipDBRepository, new UserValidator());
    FriendshipService friendshipService = new FriendshipService(userDBRepository, friendshipDBRepository, new FriendshipValidator());
    MessageDBRepository messageDBRepository = new MessageDBRepository(connection);
    MessageService messageService = new MessageService(userDBRepository, friendshipDBRepository, messageDBRepository);
    MenuButtonsController menuButtonsController;
    ControllerService controllerService = new ControllerService(userDBRepository, friendshipDBRepository, messageDBRepository, connection);
    PdfPrinter pdfPrinter = new PdfPrinter();

    private final ObservableList<User> myFriendsModel = FXCollections.observableArrayList();

    private Stage dialogStage;
    private Page user;

    @FXML
    private ImageView searchButton;
    @FXML
    private ImageView homeButton;
    @FXML
    private ImageView notificationsButton;
    @FXML
    private Button statisticsButton;
    @FXML
    private Button friendStatisticsButton;
    @FXML
    private DatePicker datePicker;
    @FXML
    private ImageView inboxButton;
    @FXML
    private Label firstName;
    @FXML
    private Label lastName;
    @FXML
    private ListView<User> listView;

    @FXML
    private void initialize() {
    }

    @FXML
    public void setService(Stage dialogStage, Page user) {
        this.dialogStage = dialogStage;
        this.user = user;
        this.menuButtonsController = new MenuButtonsController(dialogStage, user);
        firstName.setText(user.getFirstName());
        lastName.setText(user.getLastName());
        friendshipService.addObserver(this);
        this.datePicker.setValue(LocalDate.now());
        uploadData();
    }

    public void uploadData() {
        Iterable<User> all = friendshipService.getAllFriends(user.getId());
        List<User> messageTaskList = StreamSupport.stream(all.spliterator(), false)
                .collect(Collectors.toList());
        listView.setCellFactory(param -> new ProfileCell(user, friendshipService));
        myFriendsModel.setAll(messageTaskList);
        listView.setItems(myFriendsModel);
    }

    public void handleStatisticsButton() {
        List<User> users = controllerService.getMyFriendsInGivenDate(this.user, datePicker.getValue());
        List<Message> privateMessages = controllerService.getConversationsDate(user.getId(), datePicker.getValue());
        List<Message> groupMessages = controllerService.getGroupConversationsDate(user.getId(), datePicker.getValue());
        pdfPrinter.printStatisticsForLoggedUser(user, users, privateMessages, groupMessages, datePicker.getValue());
    }

    public void handleFriendStatisticsButton() {
        User user1 = listView.getSelectionModel().getSelectedItem();
        List<Message> messages = controllerService.getConversationsFriendDate(user.getId(), user1.getId(), datePicker.getValue());
        if (user1 != null)
            pdfPrinter.printStatisticsForFriendsMessages(user, user1, messages, datePicker.getValue());
        else {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Select an user!", ButtonType.CANCEL);
            alert.showAndWait();
            if (alert.getResult() == ButtonType.CANCEL) {
                alert.close();
            }
        }
    }

    @Override
    public void update(FriendshipChangeEvent friendshipChangeEvent) {
        uploadData();
    }

    public void handleNotificationsButton() {
        menuButtonsController.moveToNotificationsButton();
    }

    public void handleSearchButton() {
        menuButtonsController.moveToSearchButton();
    }

    public void handleHomeButton() {
        menuButtonsController.moveToHomeButton();
    }

    public void handleInboxButton() { menuButtonsController.moveToInboxController();}

}