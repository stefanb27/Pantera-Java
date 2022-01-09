package com.example.pantera.controller;

import com.example.pantera.Main;
import com.example.pantera.domain.*;
import com.example.pantera.domain.validators.FriendshipValidator;
import com.example.pantera.domain.validators.UserValidator;
import com.example.pantera.events.FriendshipChangeEvent;
import com.example.pantera.repository.db.FriendshipDBRepository;
import com.example.pantera.repository.db.MessageDBRepository;
import com.example.pantera.repository.db.UserDBRepository;
import com.example.pantera.service.ControllerService;
import com.example.pantera.service.FriendshipService;
import com.example.pantera.service.MessageService;
import com.example.pantera.service.UserService;
import com.example.pantera.utils.Observer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class InboxController implements Observer<FriendshipChangeEvent> {
    Connection connection = new Connection();
    UserDBRepository userDBRepository = new UserDBRepository(connection);
    FriendshipDBRepository friendshipDBRepository = new FriendshipDBRepository(connection);
    UserService userService = new UserService(userDBRepository, friendshipDBRepository, new UserValidator());
    FriendshipService friendshipService = new FriendshipService(userDBRepository, friendshipDBRepository, new FriendshipValidator());
    MessageDBRepository messageDBRepository = new MessageDBRepository(connection);
    MessageService messageService = new MessageService(userDBRepository, friendshipDBRepository, messageDBRepository);
    ControllerService controllerService = new ControllerService(userDBRepository, friendshipDBRepository, messageDBRepository, connection);
    MenuButtonsController menuButtonsController;
    //Page userPage;

    private Stage dialogStage;
    private Page user;
    private List<Long> sendToUser = new ArrayList<>();
    private final ObservableList<User> usersModel = FXCollections.observableArrayList();
    private final ObservableList<Message> messagesModel = FXCollections.observableArrayList();

    @FXML
    private Text notif;
    @FXML
    private Text prof;
    @FXML
    private Text home;
    @FXML
    private Text search;

    @FXML
    private ImageView toGroup;
    @FXML
    private TextField nameOfUser;
    @FXML
    private ImageView searchButton;
    @FXML
    private ImageView homeButton;
    @FXML
    private ImageView profileButton;
    @FXML
    private ImageView notificationButton;
    @FXML
    private Button sendButton;
    @FXML
    private TextField sendTextField;
    @FXML
    private ListView<User> listView;
    @FXML
    private VBox chatBox;
    @FXML
    private ScrollPane scrollPane;


    @FXML
    private void initialize() {}

    @FXML
    public void setService(Stage dialogStage, Page user) {
        this.dialogStage = dialogStage;
        this.user = user;
        this.menuButtonsController = new MenuButtonsController(dialogStage, user);
        this.sendButton.setDisable(true);
        List<User> model = (List<User>) user.getFriends();
        System.out.println(user.getFriends().size());
        //model.add(new Group("fsd", "Fsdds", "fsd", "fsdf", new ArrayList<>()));
        usersModel.setAll(model);
        //usersModel.add(new Group("das", "das", "das", "das", new ArrayList<>()));

        listView.setItems(usersModel);
        messageService.addObserver(this);
        chatBox.setPadding(new Insets(0, 5, 0, 5));
    }

    private void uploadData(Long newUser) {
        chatBox.getChildren().clear();
        List<Message> messages = controllerService.getConversation(user.getId(), newUser);
//        List<Message> messages = new ArrayList<>();
//        for(Message ms : m){ if(ms.getFrom().equals(newUser) || ms.getTo().equals(newUser)) messages.add(ms);}
        for (Message message : messages) {
            String messageUpper = message.getMessage();
            if(messageUpper.length() != 0) {
                messageUpper = messageUpper.substring(0, 1).toUpperCase(Locale.ROOT) + messageUpper.substring(1);
                int lenInit = 0;
            }
            Label label = new Label(messageUpper);
            //label.setMinWidth(Region.USE_PREF_SIZE);
            //label.setStyle("-fx-text-fill: #ffffff;-fx-border-radius: 15; -fx-border-color: #8284AD; -fx-background-color: #2F2E46; -fx-background-radius: 20;");
            label.setPadding(new Insets(2, 5, 2, 5));        //label.getStylesheets().add("cssStyle/textField.css");
            label.setId("send");
            //sendTextField.getStylesheets().add("cssStyle/textField.css");
            label.getStylesheets().add("cssStyle/textField.css");
            HBox hBox = new HBox();

            HBox hboxReply = new HBox();


            ImageView replyButton = new ImageView("X:\\pantera\\src\\main\\resources\\images\\reply.png");
            replyButton.setFitHeight(15);
            replyButton.setFitWidth(15);

            if(message.getFrom().equals(user.getId())){
                hBox.getChildren().add(replyButton);
                hBox.getChildren().add(label);
                hBox.setAlignment(Pos.BASELINE_RIGHT);
            } else {
                hBox.getChildren().add(label);
                hBox.getChildren().add(replyButton);
                hBox.setAlignment(Pos.BASELINE_LEFT);
            }
            //hBox.setStyle("");
            //hBox.setMinWidth(Region.USE_PREF_SIZE);
            chatBox.getChildren().add(hBox);
            chatBox.setSpacing(10);
        }
        //to continue
        //inca un for pentru grupuri aici
        /*
        for(){

        }
         */
        scrollPane.setVvalue(1.0);
        messagesModel.setAll(messages);
    }

    public void handleMouseClicked() {
        chatBox.getChildren().clear();
        User newUser = listView.getSelectionModel().getSelectedItem();
        nameOfUser.setText(newUser.getFirstName() + " " + newUser.getLastName());

        sendToUser.clear();
        sendToUser.add(newUser.getId());
        this.sendButton.setDisable(false);
        uploadData(newUser.getId());
    }

    public void handleHover(){
        uploadData(listView.getSelectionModel().getSelectedItem().getId());
    }

    @Override
    public void update(FriendshipChangeEvent friendshipChangeEvent) {
        uploadData(listView.getSelectionModel().getSelectedItem().getId()); //(sendToUser.get(0));
    }

    //aici trimit mesajul prin send button
    public void handleSendButton() {
        if (sendTextField.getText() != null) {
            messageService.sendMessage(user.getId(), sendToUser, sendTextField.getText());
            sendTextField.clear();
        }
        user.addMessage(new Message(user.getId(), sendTextField.getText(), LocalDateTime.now()));
    }

    public boolean isAGroup(){

        return true;
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

    public void handleProfileButton() {
        menuButtonsController.moveToProfileButton();
    }

    public void handleToGroup(){
        menuButtonsController.moveToGroupController();
    }

    public void handleToInbox(){
        menuButtonsController.moveToInboxController();
    }

    class UpdateTask extends TimerTask{
        @Override
        public void run() {
            for(Long id : sendToUser){
                uploadData(id);
            }
        }
    }

    public void handleDragN(){ notif.setVisible(true);}
    public void handleDragP(){ prof.setVisible(true);}
    public void handleDragS(){ search.setVisible(true);}
    public void handleDragH(){ home.setVisible(true);}
    public void handleDragNE(){ notif.setVisible(false);}
    public void handleDragPE(){ prof.setVisible(false);}
    public void handleDragSE(){ search.setVisible(false);}
    public void handleDragHE(){ home.setVisible(false);}
}
