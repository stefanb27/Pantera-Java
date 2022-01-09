package com.example.pantera.service;

import com.example.pantera.domain.*;
import com.example.pantera.repository.Repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ControllerService {
    private Repository<Long, User> userRepository;
    private Repository<Tuple<Long, Long>, Friendship> friendshipRepository;
    private Repository<Tuple<Long, Long>, Message> messageRepository;
    private Connection connection;

    public ControllerService (Repository<Long, User> userRepository, Repository<Tuple<Long, Long>, Friendship> friendshipRepository, Repository<Tuple<Long, Long>, Message> messageRepository, Connection connection) {
        this.userRepository = userRepository;
        this.friendshipRepository = friendshipRepository;
        this.messageRepository = messageRepository;
        this.connection = connection;
    }

    public Page checkLogIn(String email, String password) {
        String sql = "select * from users where email = ? and password = ?";
        try (java.sql.Connection  con = connection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.setString(2, password);
            ResultSet resultSet = ps.executeQuery();
            while (resultSet.next()) {
                Long id = resultSet.getLong(1);
                String firstName = resultSet.getString(2);
                String lastName = resultSet.getString(3);
                Page user = new Page(firstName, lastName, email, password);
                user.setId(id);
                return user;
            }
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<NotificationsWrapper> notificationsFilter(User user) {
        List<NotificationsWrapper> users = new ArrayList<>();
        String sql = "select friendships.id1, users.first_name, users.last_name, friendships.status, friendships.date " +
                "from users inner join friendships on friendships.id2 = ? " +
                "where users.id = friendships.id1 and friendships.status = ?";
        try (java.sql.Connection  con = connection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, user.getId());
            ps.setString(2, "pending");
            ResultSet resultSet = ps.executeQuery();
            while (resultSet.next()) {
                Long id = resultSet.getLong(1);
                String firstName = resultSet.getString(2);
                String lastName = resultSet.getString(4);
                String status = resultSet.getString(3);
                LocalDateTime date = resultSet.getTimestamp(5).toLocalDateTime();
                NotificationsWrapper anUser = new NotificationsWrapper(id, firstName, status, lastName, date);
                anUser.setId(id);
                users.add(anUser);
            }
            ps.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        String sql1 = "select friendships.id1, users.first_name, users.last_name, friendships.status, friendships.date " +
                "from users inner join friendships on friendships.id2 = ? " +
                "where users.id = friendships.id1 and friendships.status = ?";
        try (java.sql.Connection  con = connection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql1)) {
            ps.setLong(1, user.getId());
            ps.setString(2, "approved");
            ResultSet resultSet = ps.executeQuery();
            while (resultSet.next()) {
                Long id1 = resultSet.getLong(1);
                String firstName = resultSet.getString(2);
                String lastName = resultSet.getString(4);
                String status = resultSet.getString(3);
                LocalDateTime date = resultSet.getTimestamp(5).toLocalDateTime();
                NotificationsWrapper anUser = new NotificationsWrapper(id1, firstName, status, lastName, date);
                anUser.setId(id1);
                users.add(anUser);
            }
            ps.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        String sql2 = "select friendships.id2, users.first_name, users.last_name, friendships.status, friendships.date " +
                "from users inner join friendships on friendships.id1 = ? " +
                "where users.id = friendships.id2 and friendships.status = ?";
        try (java.sql.Connection  con = connection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql2)) {
            ps.setLong(1, user.getId());
            ps.setString(2, "approved");
            ResultSet resultSet = ps.executeQuery();
            while (resultSet.next()) {
                Long id1 = resultSet.getLong(1);
                String firstName = resultSet.getString(2);
                String lastName = resultSet.getString(4);
                String status = resultSet.getString(3);
                LocalDateTime date = resultSet.getTimestamp(5).toLocalDateTime();
                NotificationsWrapper anUser = new NotificationsWrapper(id1, firstName, status, lastName, date);
                anUser.setId(id1);
                users.add(anUser);
            }
            ps.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users.stream().sorted(Comparator.comparing(NotificationsWrapper::getDate).reversed()).collect(Collectors.toList());
    }

    public List<User> searchListFilter(User user) {
        List<User> users = new ArrayList<>();
        String sql = "select * from users where id != ?";
        try (java.sql.Connection  con = connection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, user.getId());
            ResultSet resultSet = ps.executeQuery();
            while (resultSet.next()) {
                Long id = resultSet.getLong(1);
                String firstName = resultSet.getString(2);
                String lastName = resultSet.getString(3);
                String email = resultSet.getString(4);
                String password = resultSet.getString(5);
                User anUser = new User(firstName, lastName, email, password);
                anUser.setId(id);
                users.add(anUser);
            }
            ps.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    public List<User> searchBoxFilter(User user, String searchText) {
        List<User> users = new ArrayList<>();
        String sql = "select * from users where id != ? and first_name like ? or last_name like ?";
        try (java.sql.Connection  con = connection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, user.getId());
            ps.setString(2, '%' + searchText + '%');
            ps.setString(3, '%' + searchText + '%');
            ResultSet resultSet = ps.executeQuery();
            while (resultSet.next()) {
                Long id = resultSet.getLong(1);
                String firstName = resultSet.getString(2);
                String lastName = resultSet.getString(3);
                String email = resultSet.getString(4);
                String password = resultSet.getString(5);
                User anUser = new User(firstName, lastName, email, password);
                anUser.setId(id);
                users.add(anUser);
            }
            ps.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    public List<Message> getConversation(Long user1, Long user2) {
        List<Message> messages = new ArrayList<>();
        String sql = "select messages.touser, conversations.fromuser, conversations.message, conversations.date from messages " +
                "inner join conversations on messages.idm = conversations.id " +
                "where messages.touser = ? and conversations.fromuser = ? or " +
                "messages.touser = ? and conversations.fromuser = ?";
        try (java.sql.Connection  con = connection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, user1);
            ps.setLong(2, user2);
            ps.setLong(3, user2);
            ps.setLong(4, user1);
            ResultSet resultSet = ps.executeQuery();
            int nr = 0;
            while (resultSet.next()) {
                if (nr == 0) {
                    // TODO: 03.01.2022
                }
                Long touser = resultSet.getLong(1);
                Long fromuser = resultSet.getLong(2);
                String message = resultSet.getString(3);
                LocalDateTime date = resultSet.getTimestamp(4).toLocalDateTime();
                Message message1 = new Message(fromuser, message, date);
                List<Long> to = new ArrayList<>();
                to.add(touser);
                message1.setTo(to);
                messages.add(message1);
            }
            ps.execute();
            return messages;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean findPendingFriendship(User user1, User user2) {
        String sql = "select * from friendships " +
                "where status = ? and id1 = ? and id2 = ?";
        try (java.sql.Connection  con = connection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, "pending");
            ps.setLong(2, user1.getId());
            ps.setLong(3, user2.getId());
            ResultSet resultSet = ps.executeQuery();
            int nr = 0;
            if (resultSet.next()) {
                return true;
            }
            ps.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean findApprovedFriendship(User user1, User user2) {
        String sql = "select * from friendships " +
                "where status = ? and id1 = ? and id2 = ? or id1 = ? and id2 = ?";
        try (java.sql.Connection  con = connection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, "approved");
            ps.setLong(2, user1.getId());
            ps.setLong(3, user2.getId());
            ps.setLong(4, user2.getId());
            ps.setLong(5, user1.getId());
            ResultSet resultSet = ps.executeQuery();
            int nr = 0;
            if (resultSet.next()) {
                return true;
            }
            ps.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<User> findFriends(User user) {
        List<User> friends = new ArrayList<>();
        String sql = "select * from friendships " +
                "where status = ? and id1 = ? or id2 = ?";
        try (java.sql.Connection  con = connection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, "approved");
            ps.setLong(2, user.getId());
            ps.setLong(3, user.getId());
            ResultSet resultSet = ps.executeQuery();
            while (resultSet.next()) {
                Long id1 = resultSet.getLong("id1");
                Long id2 = resultSet.getLong("id2");
                //LocalDateTime dateTime = resultSet.getTimestamp("date").toLocalDateTime();
                //String status = resultSet.getString("status");

                if(!id1.equals(user.getId())) {
                    friends.add(userRepository.findOne(id1));
                }
                else{
                    friends.add(userRepository.findOne(id2));
                }
            }
            return friends;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return friends;
    }

    public List<Long> findRequestSent(User user){
        List<Long> requestSent = new ArrayList<>();
        String sql = "select * from friendships " +
                "where status = ? and id1 = ?";
        try (java.sql.Connection  con = connection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, "pending");
            ps.setLong(2, user.getId());
            ResultSet resultSet = ps.executeQuery();
            while (resultSet.next()) {
                Long id2 = resultSet.getLong("id2");
                requestSent.add(id2);
            }
            return requestSent;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return requestSent;
    }

    public List<Long> findRequestReceived(User user){
        List<Long> requestReceived = new ArrayList<>();
        String sql = "select * from friendships " +
                "where status = ? and id2 = ?";
        try (java.sql.Connection  con = connection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, "pending");
            ps.setLong(2, user.getId());
            ResultSet resultSet = ps.executeQuery();
            while (resultSet.next()) {
                Long id1 = resultSet.getLong("id1");
                requestReceived.add(id1);
            }
            return requestReceived;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return requestReceived;
    }


    public List<Message> findMessagesReceived(User user){
        List<Message> messagesReceived = new ArrayList<>();
        String sql = "select c.fromuser, c.message, c.date, m.touser from conversations c inner join messages m\n" +
                "on c.id = m.idm where touser = ?";
        try (java.sql.Connection  con = connection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, user.getId());
            ResultSet resultSet = ps.executeQuery();
            while (resultSet.next()) {
                Long fromUser = resultSet.getLong("fromuser");
                String message = resultSet.getString("message");
                LocalDateTime date = resultSet.getTimestamp("date").toLocalDateTime();
                Message mess = new Message(fromUser, message, date);
                messagesReceived.add(mess);
            }
            return messagesReceived;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return messagesReceived;
    }

    public List<Message> findAllMessForAnUser(User user){
        List<Message> messages = new ArrayList<>();
        String sql = "select c.fromuser, c.message, c.date, m.touser from conversations c inner join messages m on c.id = m.idm where touser = ? or fromuser = ?";
        try (java.sql.Connection  con = connection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, user.getId());
            ps.setLong(2, user.getId());
            ResultSet resultSet = ps.executeQuery();
            while (resultSet.next()) {
                Long fromUser = resultSet.getLong("fromuser");
                String message = resultSet.getString("message");
                LocalDateTime date = resultSet.getTimestamp("date").toLocalDateTime();
                Message mess = new Message(fromUser, message, date);
                messages.add(mess);
            }
            return messages;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return messages;
    }
//    public List<Long> findRequestReceived(){
//
//    }
//

//    public List<User> myFriendsFilter(User user, String searchText) {
//        List<User> users = new ArrayList<>();
//        String sql = "select * from users where id != ? and first_name like ? or last_name like ?";
//        try (java.sql.Connection  con = connection.getConnection();
//             PreparedStatement ps = con.prepareStatement(sql)) {
//            ps.setLong(1, user.getId());
//            ps.setString(2, '%' + searchText + '%');
//            ps.setString(3, '%' + searchText + '%');
//            ResultSet resultSet = ps.executeQuery();
//            while (resultSet.next()) {
//                Long id = resultSet.getLong(1);
//                String firstName = resultSet.getString(2);
//                String lastName = resultSet.getString(3);
//                String email = resultSet.getString(4);
//                String password = resultSet.getString(5);
//                User anUser = new User(firstName, lastName, email, password);
//                anUser.setId(id);
//                users.add(anUser);
//            }
//            ps.execute();
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return users;
//    }
}
