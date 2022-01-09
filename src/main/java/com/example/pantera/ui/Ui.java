package com.example.pantera.ui;

import com.example.pantera.domain.Entity;
import com.example.pantera.domain.Message;
import com.example.pantera.domain.Tuple;
import com.example.pantera.domain.User;
import com.example.pantera.domain.validators.ValidateException;
import com.example.pantera.service.FriendshipService;
import com.example.pantera.service.MessageService;
import com.example.pantera.service.UserService;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.*;

public class Ui {
    UserService userService;
    FriendshipService friendshipService;
    MessageService messageService;

    /**
     * Ui constructor
     *
     * @param userService       service for user
     * @param friendshipService service for friendships
     */
    public Ui(UserService userService, FriendshipService friendshipService, MessageService messageService) {
        this.userService = userService;
        this.friendshipService = friendshipService;
        this.messageService = messageService;
    }

    /**
     * util method for reading user fields from input
     *
     * @return a user
     * @throws ValidateException if id is not numerical or names are wrong introduced
     */
    public User readUser() throws ValidateException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Provide first name: ");
        String firstName = scanner.nextLine();
        System.out.println("Provide last name: ");
        String lastName = scanner.nextLine();
        System.out.println("Provide the Id: ");
        String Id = scanner.nextLine();
        System.out.println("Provide email: ");
        String email = scanner.nextLine();
        System.out.println("Provide password: ");
        String password = scanner.nextLine();

        User user;
        try {
            user = new User(firstName, lastName, email, password);
            user.setId(Long.parseLong(Id));
            return user;
        } catch (NumberFormatException e) {
            System.out.println("Id must be numerical");
        } catch (ValidateException e) {
            System.out.println(e.getMessage());
        }catch (NullPointerException e){
            System.out.println("Invalid data -> Try again");
        }
        return null;
    }

    /**
     * read and validate 2 ids
     *
     * @return a Tuple of these ids
     * @throws ValidateException id ids are not numerical
     */
    public Tuple<Long, Long> readIds() throws ValidateException {
        Scanner scanner = new Scanner(System.in);
        Long idUser1 = null, idUser2 = null;
        System.out.println("Provide the first id: ");
        String id1 = scanner.nextLine();
        try {
            idUser1 = Long.parseLong(id1);
        } catch (NumberFormatException e) {
            System.out.println("Id must be numerical");
        }
        System.out.println("Provide the second id: ");
        String id2 = scanner.nextLine();
        try {
            idUser2 = Long.parseLong(id2);
        } catch (NumberFormatException e) {
            System.out.println("Id must be numerical");
        }
        return new Tuple<>(idUser1, idUser2);
    }

    //private Boolean isUserIn(User user){ return userService.findUser(user.getId()) != null; }

    /**
     * read a user, validate it, and then add it to repository
     *
     * @throws ValidateException if the add operation could not be preformed
     */
    private User addUser() {
        User user = readUser();
        if(user == null){
            System.out.println("Invalid data -> Try again");
        }else{
            try {
                userService.addUser(user.getFirstName(), user.getLastName(), user.getId()
                        , user.getEmail(), user.getPassword());
                System.out.println("Sign up successful!");
            } catch (ValidateException e) {
                System.out.println("Welcome back " + user.getFirstName());
            }
        }
        return user;
    }

    /**
     * read a user, validate it, and then delete it from repository
     *
     * @throws ValidateException if delete operation could not be preformed
     */
    private void removeUser() {
        Long idNumber;
        Scanner scanner = new Scanner(System.in);
        System.out.println("Give id: ");
        String id = scanner.nextLine();
        try {
            idNumber = Long.parseLong(id);
            Entity deletedUser = userService.deleteUser(idNumber);
            System.out.println("User deleted successfully");
        } catch (NumberFormatException ex) {
            System.out.println("The id must be an Integer");
        } catch (ValidateException ex) {
            System.out.println(ex.getMessage());
        }
    }

    /**
     * read a user, validate it, and then update it
     *
     * @throws ValidateException if update operation could not be preformed
     */
    private void updateUser() {
        User user = readUser();
        try {
            //if (user != null)
            userService.updateUser(user.getFirstName(), user.getLastName(), user.getId());
            System.out.println("User updated successfully");
        } catch (ValidateException e) {
            System.out.println(e.getMessage());
        }
    }

    private void printUsers() {
        userService.getAllUsers().forEach(x -> System.out.println(x.getFirstName() + " " + x.getLastName()));
    }

    /**
     * read 2 ids and add the friendship
     *
     * @throws ValidateException if add friendship operation could not be preformed
     */
    private void addFriendship() throws ValidateException {
        Tuple<Long, Long> ids = readIds();
        try {
            friendshipService.addFriendshipPending(ids.getLeft(), ids.getRight());
            //System.out.println("Users are now friends");
        } catch (ValidateException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * read 2 ids and delete the friendship
     *
     * @throws ValidateException if delete friendship operation could not be preformed
     */
    private void removeFriendship() throws ValidateException {
        Tuple<Long, Long> ids = readIds();
        try {
            friendshipService.deleteFriendship(ids.getLeft(), ids.getRight());
            //System.out.println("Users are not friends anymore");
        } catch (ValidateException e) {
            System.out.println(e.getMessage());
        }
    }

    private void printFriendships() {
        for (User user1 : userService.getAllUsers()) {
            StringBuilder friends = new StringBuilder(user1.getFirstName() + " " + user1.getLastName() + "'s friends are: ");
            if (user1.getFriends() != null) {
                for (User user2 : user1.getFriends()) {
                    friends.append(user2.getFirstName()).append(" ").append(user2.getLastName()).append(", ");
                }
            }
            friends = new StringBuilder(friends.toString().replaceAll(",.$", ""));
            System.out.println(friends);
        }
    }

    /**
     * print number of components in the network
     */
    private void numberComponents() {
        int n = userService.numberOfNetworks();
        System.out.println("There are " + n + "communities");
    }

    /**
     * print the users of the biggest component in the network
     */
    private void biggestComponent() {
        List<Long> l = userService.biggestNetwork();
        System.out.println("Most sociable community consists of: ");
        l.forEach(x -> System.out.println(userService.findUser(x).getFirstName() + " " + userService.findUser(x).getLastName()));
    }

    private void printFriendsOfUser() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Provide the Id: ");
        String Id = scanner.nextLine();
        Long idUser = Long.parseLong(Id);
        try {
            System.out.println("All friends for user " + userService.findUser(Long.parseLong(Id)).getFirstName() + " are: ");
            List<Tuple> list = friendshipService.friendsOfUser(idUser);
            list.forEach(x -> System.out.println(x.getLeft() + " " + x.getRight()));
        } catch (ValidateException e) {
            e.getMessage();
        }
    }

    private void printFriendsOfUserMonth() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Provide the Id: ");
        String id = scanner.nextLine();
        System.out.println("Provide the Month: ");
        String string = scanner.nextLine();
        Month month = Month.of(Integer.parseInt(string));
        Long idUser = Long.parseLong(id);
        try {
            System.out.println("All friends for user " + userService.findUser(idUser).getFirstName() + " are: ");
            List<Tuple> list = friendshipService.friendsOfUserMonth(Long.parseLong(id), month);
            list.forEach(x -> System.out.println(x.getLeft() + " | " + x.getRight()));
        } catch (ValidateException e) {
            e.getMessage();
        }
    }

    private void startConversation(Long id) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Send to: ");
        String users = scanner.nextLine();
        String[] strings = users.split(";");
        List<Long> toUsers = new ArrayList<>();
        for (String string : strings) {
            showConversationUser(id, Long.parseLong(string));
            toUsers.add(Long.parseLong(string));
        }
        System.out.println("Type...");
        String message = scanner.nextLine();
        try {
            messageService.sendMessage(id, toUsers, message);
            System.out.println("Delivered at: " + LocalDateTime.now());
        } catch (ValidateException e) {
            System.out.println(e.getMessage());
        }
    }

    public void showConversationUser(Long id1, Long id2){
        List<Message> messages = messageService.showConversations(id1, id2);
        messages.forEach(m -> System.out.println(userService.findUser(m.getFrom()).getFirstName() + ": " + m.getMessage() +
                " reply at " + m.getTo() + " " +  m.getReply()));
    }

    private void showConversation() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Give first user: ");
        String id1 = scanner.nextLine();
        System.out.println("Give second user: ");
        String id2 = scanner.nextLine();
        List<Message> messages = messageService.showConversations(Long.parseLong(id1), Long.parseLong(id2));
        messages.forEach(m -> System.out.println(userService.findUser(Long.parseLong(id1)).getFirstName() + ": " + m.getMessage() + " " +
                m.getFrom() + " reply at " + userService.findUser(Long.parseLong(id2)).getFirstName() + "(" + m.getTo() + ") : " + m.getReply()));
    }

    private void showReplies() {
        List<Message> rez = messageService.getAllMessages();
        for (Message msg : rez) {
            if (msg.getReply() == null) {
                System.out.println("No conv in history " +
                        msg.getFrom() + " " + msg.getTo());
            } else {
                System.out.println("User with id " + msg.getFrom() + " responded: " + msg.getMessage() +
                        " at reply: " + msg.getReply() + " from " + msg.getReply().getFrom());
            }
        }
    }

    public void sendRequest(Long id){
        Scanner scanner = new Scanner(System.in);
        System.out.println("Give id of target user: ");
        String id2 = scanner.nextLine();
        try {
            friendshipService.addFriendshipPending(id, Long.parseLong(id2));
            System.out.println("Friend request sent");
        } catch (NumberFormatException nr) {
            System.out.println("Numerical format required");
        } catch (ValidateException e) {
            System.out.println(e.getMessage());
        }
    }

    public void approveFriendship(Long id) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Give id of target user: ");
        String id2 = scanner.nextLine();
        try {
            friendshipService.approveFriendship(Long.parseLong(id2), id);
            System.out.println("You are now friends");
        } catch (NumberFormatException nr) {
            System.out.println("Numerical format required");
        } catch (ValidateException e) {
            System.out.println(e.getMessage());
        }
    }

    public void rejectFriendship(Long id) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Give id of target user: ");
        String id2 = scanner.nextLine();
        try {
            friendshipService.rejectFriendship(Long.parseLong(id2), id);
            System.out.println("Removed friend request");
        } catch (NumberFormatException nr) {
            System.out.println("Numerical format required");
        } catch (ValidateException e) {
            System.out.println(e.getMessage());
        }
    }

    public void getStatusForUser(Long id) {
        System.out.println("Pendings : " + friendshipService.getUsersMatched(friendshipService.getAllPendings(id)));
        System.out.println("Rejected: " + friendshipService.getUsersMatched(friendshipService.getAllRejected(id)));
        System.out.println("Approved: " + friendshipService.getUsersMatched(friendshipService.getAllApproved(id)));
        //System.out.println("Requested: " + friendshipService.getUsersMatched(friendshipService.getAllRequest(id)));
    }

    /**
     * based on command executes the corresponding operation
     * @param command String
     */
    public void menuAdmin(String command) {
        switch (command) {
            case "1" -> removeUser();
            case "2" -> updateUser();
            case "3" -> removeFriendship();
            case "4" -> printUsers();
            case "5" -> printFriendships();
            case "6" -> numberComponents();
            case "7" -> biggestComponent();
            case "8" -> printFriendsOfUser();
            case "9" -> printFriendsOfUserMonth();
            case "10" -> showConversation();
            case "11" -> run();
            default -> System.out.println("Wrong command");
        }
    }

    public void menuUser(String command, Long id) {
        switch (command) {
            case "1" -> getStatusForUser(id);
            case "2" -> approveFriendship(id);
            case "3" -> rejectFriendship(id);
            case "4" -> sendRequest(id);
            case "5" -> startConversation(id);
            case "6" -> run();
            default -> System.out.println("Wrong command");
        }
    }

    public void runUser() {
        User user = addUser();
        String command;
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("---------MENU-USER----------");
            System.out.println("""
                    1. Get your status
                    2. Approve friendship
                    3. Reject friendship
                    4  Send request
                    5. Start a conversation
                    6. Exit""");
            System.out.println("---------MENU-USER----------");
            command = scanner.nextLine();
            if (Objects.equals(command, ".")) break;
            menuUser(command, user.getId());
        }
    }

    public void runAdmin() {
        String command;
        Scanner scanner = new Scanner(System.in);
        //showReplies();
        while (true) {
            System.out.println("----------------------MENU-------------------");
            System.out.println("""
                    1. Remove user
                    2. Update user
                    3. Remove friendship
                    4. Print users
                    5. Print friendships
                    6. Number of components
                    7. Biggest network
                    8. Friends of an user
                    9. Friends of an user in a given month
                    10. Show conversation between 2 users
                    11. Exit""");
            System.out.println("----------------------MENIU-------------------");
            command = scanner.nextLine();
            if (Objects.equals(command, ".")) break;
            menuAdmin(command);
        }
    }


    public void menu(String command){
        switch (command) {
            case "1" -> runUser();
            case "2" -> runAdmin();
            default -> System.out.println("Wrong command");
        }
    }

    public void run() {
        String command;
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("----------Social-App-----------");
            System.out.println("""
                    1. User [Log in / Sign up] 
                    2. Admin mode""");
            System.out.println("----------Social-App-----------");
            command = scanner.nextLine();
            if (Objects.equals(command, ".")) break;
            try {
                Long c = Long.parseLong(command);
                if (Long.parseLong(command) == 1L || Long.parseLong(command) == 2L) {
                    menu(command);
                }
            } catch (NumberFormatException nr) {
                System.out.println("Wrong command");
            }
        }
    }
}
