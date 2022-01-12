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
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
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
    private final ObservableList<Entity> usersModel = FXCollections.observableArrayList();
    private final ObservableList<Message> messagesModel = FXCollections.observableArrayList();
    private Group actualGroup;
    private Long reply;

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
    private ImageView notificationsButton;
    @FXML
    private Button sendButton;
    @FXML
    private TextField sendTextField;
    @FXML
    private ListView<Entity> listView;
    @FXML
    private VBox chatBox;
    @FXML
    private ScrollPane scrollPane;

    @FXML
    private void initialize() {
        Tooltip.install(homeButton, new Tooltip("Inbox"));
        Tooltip.install(notificationsButton, new Tooltip("Notifications"));
        Tooltip.install(searchButton, new Tooltip("Search"));
        Tooltip.install(profileButton, new Tooltip("Profile"));
    }

    @FXML
    public void setService(Stage dialogStage, Page user) {
        this.dialogStage = dialogStage;
        this.user = user;
        this.menuButtonsController = new MenuButtonsController(dialogStage, user);
        this.sendButton.setDisable(true);


        List<Group> groups = controllerService.getGroups(user);
        List<User> users = (List<User>) friendshipService.getAllFriends(user.getId());
        usersModel.addAll(groups);
        usersModel.addAll(users);

        listView.setItems(usersModel);
        messageService.addObserver(this);
        chatBox.setPadding(new Insets(0, 5, 0, 5));
    }

    void handleReplyEvent(Message message) {
        if (this.reply == null) {
            this.reply = message.getId().getRight();
            sendTextField.setPromptText("Replying to " +
                    userService.findUser(message.getFrom()) + "...");
        } else {
            this.reply = null;
            sendTextField.setPromptText("");
        }
    }

    private void uploadData(Entity entity) {
        chatBox.getChildren().clear();
        List<Message> messages = new ArrayList<>();
        try {
            User newUser = (User) entity;
            messages = controllerService.getConversation(user.getId(), newUser.getId());
        } catch (ClassCastException classCastException) {
            Group group = (Group) entity;
            messages = controllerService.getGroupConversation(user.getId(), group.getId());
        }

        for (Message message : messages) {
            String messageUpper = message.getMessage();
            if(messageUpper.length() != 0) {
                messageUpper = messageUpper.substring(0, 1).toUpperCase(Locale.ROOT) + messageUpper.substring(1);
                int lenInit = 0;
            }
            Label label = new Label(messageUpper);
            label.setPadding(new Insets(2, 5, 2, 5));
            label.setId("send");
            label.getStylesheets().add("cssStyle/textField.css");
            label.setStyle("-fx-border-color: #8284AD; -fx-text-fill: #ffffff; -fx-background-color: #2F2E46;");
            HBox hBox = new HBox();

            HBox hboxReply = new HBox();
            ImageView imageView = new ImageView("X:\\pantera\\src\\main\\resources\\images\\reply.png");
            imageView.setFitHeight(15);
            imageView.setFitWidth(15);

            Button replyButton = new Button();
            replyButton.setGraphic(imageView);
            replyButton.setBackground(null);
            replyButton.setOnAction(event -> handleReplyEvent(message));

            if(message.getFrom().equals(user.getId())){
                hBox.getChildren().add(replyButton);
                hBox.getChildren().add(label);
                hBox.setAlignment(Pos.BASELINE_RIGHT);
            } else {
                hBox.getChildren().add(label);
                hBox.getChildren().add(replyButton);
                hBox.setAlignment(Pos.BASELINE_LEFT);
            }

            VBox vBox = new VBox();
            Label replyLabel = new Label();
            Message reply = controllerService.findReply(message.getReply());

            if (message.getReply() != 0 && reply != null) {
                Label messageReplied = new Label(reply.getMessage());
                replyLabel.setText("Replied to: " + userDBRepository.findOne(reply.getFrom()).getFirstName());
                replyLabel.setStyle("-fx-text-fill: #797484;"); //797484
                messageReplied.setStyle("-fx-text-fill: #B9B6BE;" + //B9B6BE
                        "-fx-border-color: #2F2E46; -fx-border-radius: 20; -fx-background-color: #2F2E46;" +
                        "-fx-background-radius: 20;");
                messageReplied.setPadding(new Insets(2, 5, 2, 5));

                if(!reply.getFrom().equals(user.getId())) {
                    replyLabel.setAlignment(Pos.TOP_RIGHT);
                    messageReplied.setAlignment(Pos.TOP_RIGHT);
                    vBox.setAlignment(Pos.TOP_RIGHT);
                }else {
                    replyLabel.setAlignment(Pos.TOP_LEFT);
                    messageReplied.setAlignment(Pos.TOP_LEFT);
                    vBox.setAlignment(Pos.TOP_LEFT);
                }
                vBox.getChildren().add(replyLabel);
                vBox.getChildren().add(messageReplied);
                vBox.getChildren().add(hBox);
                chatBox.getChildren().add(vBox);
            } else {
                chatBox.getChildren().add(hBox);
            }
            //chatBox.setSpacing(10);
        }
        chatBox.heightProperty().addListener(observable -> scrollPane.setVvalue(1D));
        messagesModel.setAll(messages);
    }

    public void handleMouseClicked() {
        chatBox.getChildren().clear();
        Entity entity = listView.getSelectionModel().getSelectedItem();
        sendTextField.setPromptText("");
        try {
            User newUser = (User) entity;
            nameOfUser.setText(newUser.getFirstName() + " " + newUser.getLastName());
            sendToUser.clear();
            sendToUser.add(newUser.getId());
            this.actualGroup = null;
            uploadData(entity);
        } catch(ClassCastException classCastException) {
            Group group = (Group) entity;
            nameOfUser.setText(group.getName());
            System.out.println("de aici!");
            group.getGroupGuys().forEach(System.out::println);
            sendToUser.clear();
            sendToUser.add(group.getGroupGuys().get(1).getId());
//            for (User user1 : group.getGroupGuys()) {
//                sendToUser.add(user1.getId());
//            }
            uploadData(entity);
            this.actualGroup = group;
        }
        this.sendButton.setDisable(false);
    }

//    public void handleHover(){
//        uploadData(listView.getSelectionModel().getSelectedItem().getId());
//    }

    @Override
    public void update(FriendshipChangeEvent friendshipChangeEvent) {
        uploadData(listView.getSelectionModel().getSelectedItem()); //(sendToUser.get(0));
    }

    public void handleSendButton() {
        System.out.println(this.reply);
        if (sendTextField.getText() != null) {
            if (actualGroup == null) {
                messageService.sendMessage(user.getId(), sendToUser, sendTextField.getText(), this.reply, null);
            } else {
                messageService.sendMessage(user.getId(), sendToUser, sendTextField.getText(), this.reply, actualGroup.getId());
            }
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
                //uploadData(id);
            }
        }
    }


//    public void handleDragN(){ notif.setVisible(true);}
//    public void handleDragP(){ prof.setVisible(true);}
//    public void handleDragS(){ search.setVisible(true);}
//    public void handleDragH(){ home.setVisible(true);}
//    public void handleDragNE(){ notif.setVisible(false);}
//    public void handleDragPE(){ prof.setVisible(false);}
//    public void handleDragSE(){ search.setVisible(false);}
//    public void handleDragHE(){ home.setVisible(false);}
}
