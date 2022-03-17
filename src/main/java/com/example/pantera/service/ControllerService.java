package com.example.pantera.service;

import com.example.pantera.domain.*;
import com.example.pantera.repository.Repository;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
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

    public String hashPassword(String password) {
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        byte[] encodedhash = digest.digest(
                password.getBytes(StandardCharsets.UTF_8));
        StringBuilder hexString = new StringBuilder(2 * encodedhash.length);
        for (int i = 0; i < encodedhash.length; i++) {
            String hex = Integer.toHexString(0xff & encodedhash[i]);
            if(hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
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
        String sql = "select messages.id, messages.idm, messages.touser, conversations.fromuser, " +
                "conversations.message, conversations.date, messages.reply from messages " +
                "inner join conversations on messages.idm = conversations.id " +
                "where ((messages.touser = ? and conversations.fromuser = ?) or " +
                "(messages.touser = ? and conversations.fromuser = ?)) " +
                "and messages.groupcolumn = 0";
        try (java.sql.Connection  con = connection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, user1);
            ps.setLong(2, user2);
            ps.setLong(3, user2);
            ps.setLong(4, user1);
            ResultSet resultSet = ps.executeQuery();
            int nr = 0;
            while (resultSet.next()) {
                Long id = resultSet.getLong(1);
                Long idm = resultSet.getLong(2);
                Long touser = resultSet.getLong(3);
                Long fromuser = resultSet.getLong(4);
                String message = resultSet.getString(5);
                LocalDateTime date = resultSet.getTimestamp(6).toLocalDateTime();
                Long reply = resultSet.getLong(7);
                Message message1 = new Message(fromuser, message, date);
                List<Long> to = new ArrayList<>();
                to.add(touser);
                message1.setTo(to);
                message1.setId(new Tuple<>(id, idm));
                message1.setReply(reply);
                messages.add(message1);
            }
            ps.execute();
            return messages;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Message> getGroupConversation(Long user1, Long groupId) {
        List<Message> messages = new ArrayList<>();
        String sql = "select messages.id, messages.idm, messages.touser, conversations.fromuser, " +
                "conversations.message, conversations.date, messages.reply from messages " +
                "inner join conversations on messages.idm = conversations.id " +
                "where messages.groupcolumn = ? order by conversations.date";
        try (java.sql.Connection  con = connection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
//            ps.setLong(1, user1);
//            ps.setLong(2, user1);
            ps.setLong(1, groupId);
            ResultSet resultSet = ps.executeQuery();
            while (resultSet.next()) {
                Long id = resultSet.getLong(1);
                Long idm = resultSet.getLong(2);
                Long touser = resultSet.getLong(3);
                Long fromuser = resultSet.getLong(4);
                String message = resultSet.getString(5);
                LocalDateTime date = resultSet.getTimestamp(6).toLocalDateTime();
                Long reply = resultSet.getLong(7);
                Message message1 = new Message(fromuser, message, date);
                List<Long> to = new ArrayList<>();
                to.add(touser);
                message1.setTo(to);
                message1.setId(new Tuple<>(id, idm));
                message1.setReply(reply);
                messages.add(message1);
            }
            ps.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return messages;
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
    //////////////////////////////////////////////////////////////////////////////////////////////////////////

    public List<Message> findAllMessForAnUser(User user){
        List<Message> messages = new ArrayList<>();
        String sql = "select * from conversations c inner join messages m on c.id = m.idm where touser = ? or fromuser = ?";
        try (java.sql.Connection  con = connection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, user.getId());
            ps.setLong(2, user.getId());
            ResultSet resultSet = ps.executeQuery();
            while (resultSet.next()) {
                Long id = resultSet.getLong("id");
                Long idm = resultSet.getLong("idm");
                Long touser = resultSet.getLong("touser");
                Long fromuser = resultSet.getLong("fromuser");
                String message = resultSet.getString("message");
                LocalDateTime date = resultSet.getTimestamp("date").toLocalDateTime();
                Long reply = resultSet.getLong("reply");
                Message message1 = new Message(fromuser, message, date);
                List<Long> to = new ArrayList<>();
                to.add(touser);
                message1.setTo(to);
                message1.setId(new Tuple<>(id, idm));
                message1.setReply(reply);
                messages.add(message1);
            }
            return messages;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return messages;
    }

    public List<Group> getGroups(User user) {
        List<Long> longs = new ArrayList<>();
        List<Group> groups = new ArrayList<>();
        String sql = "select * from groups where users like ? " +
                "or users like ? or users like ?";
        try (java.sql.Connection  con = connection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, "%;" + user.getId() + ";%");
            ps.setString(2, "%" + user.getId() + ";%");
            ps.setString(3, "%;" + user.getId() + "%");
            ResultSet resultSet = ps.executeQuery();
            while (resultSet.next()) {
                Long id = resultSet.getLong(1);
                String name = resultSet.getString(2);
                String[] helper =  resultSet.getString(3).split(";");
                for (int i = 0; i < helper.length; i++) {
                    long user1 = Integer.parseInt(helper[i]);
                    longs.add(user1);
                }
                List<User> users = new ArrayList<>();
                for(Long user1 : longs) {
                    User user2 = userRepository.findOne(user1);
                    users.add(user2);
                }
                Group group = new Group(users, name);
                group.setId(id);
                groups.add(group);
                longs.clear();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return groups;
    }

    public Message findReply(Long idm) {
        String sql = "select * from conversations where id = ?";
        try (java.sql.Connection  con = connection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, idm);
            ResultSet resultSet = ps.executeQuery();
            while (resultSet.next()) {
                Long id = resultSet.getLong(1);
                Long fromUser = resultSet.getLong(2);
                String message = resultSet.getString(3);
                LocalDateTime dateTime = resultSet.getTimestamp(4).toLocalDateTime();
                return new Message(fromUser, message, dateTime);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<User> getMyFriendsInGivenDate(User user, LocalDate startDate, LocalDate endDate) {
        List<User> users = new ArrayList<>();
        String sql = "select * from friendships where (id1 = ? or id2 = ?) " +
                "and status = ? and date between ? and ?";
        try (java.sql.Connection  con = connection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, user.getId());
            ps.setLong(2, user.getId());
            ps.setString(3, "approved");
            ps.setDate(4, Date.valueOf(startDate));
            ps.setDate(5, Date.valueOf(endDate));
            ResultSet resultSet = ps.executeQuery();
            while (resultSet.next()) {
                Long id1 = resultSet.getLong(1);
                Long id2 = resultSet.getLong(2);
                Long finalId = id1;
                if (id1.equals(user.getId())) finalId = id2;
                users.add(userRepository.findOne(finalId));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    public List<Message> getConversationsDate(Long user1, LocalDate startDate, LocalDate endDate) {
        List<Message> messages = new ArrayList<>();
        String sql = "select messages.id, messages.idm, messages.touser, conversations.fromuser, " +
                "conversations.message, conversations.date, messages.reply from messages " +
                "inner join conversations on messages.idm = conversations.id " +
                "where messages.touser = ? " +
                "and messages.groupcolumn = 0 and conversations.date between ? and ?";
        try (java.sql.Connection  con = connection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, user1);
            ps.setDate(2, Date.valueOf(startDate));
            ps.setDate(3, Date.valueOf(endDate));
            ResultSet resultSet = ps.executeQuery();
            int nr = 0;
            while (resultSet.next()) {
                Long id = resultSet.getLong(1);
                Long idm = resultSet.getLong(2);
                Long touser = resultSet.getLong(3);
                Long fromuser = resultSet.getLong(4);
                String message = resultSet.getString(5);
                LocalDateTime date = resultSet.getTimestamp(6).toLocalDateTime();
                Long reply = resultSet.getLong(7);
                Message message1 = new Message(fromuser, message, date);
                List<Long> to = new ArrayList<>();
                to.add(touser);
                message1.setTo(to);
                message1.setId(new Tuple<>(id, idm));
                message1.setReply(reply);
                messages.add(message1);
            }
            ps.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return messages;
    }

    public List<Message> getGroupConversationsDate(Long user1, LocalDate startDate, LocalDate endDate) {
        List<Message> messages = new ArrayList<>();
        String sql = "select messages.id, messages.idm, messages.touser, messages.groupcolumn, conversations.fromuser, " +
                "conversations.message, conversations.date, messages.reply from messages " +
                "inner join conversations on messages.idm = conversations.id " +
                "where (messages.touser = ? or conversations.fromuser = ?) and " +
                "messages.groupcolumn != 0 and conversations.date between ? and ? " +
                "order by messages.groupcolumn, conversations.date";
        try (java.sql.Connection  con = connection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
//            ps.setLong(1, user1);
//            ps.setLong(2, user1);
            ps.setLong(1, user1);
            ps.setLong(2, user1);
            ps.setDate(3, Date.valueOf(startDate));
            ps.setDate(4, Date.valueOf(endDate));
            ResultSet resultSet = ps.executeQuery();
            while (resultSet.next()) {
                Long id = resultSet.getLong(1);
                Long idm = resultSet.getLong(2);
                Long touser = resultSet.getLong(3);
                Long group = resultSet.getLong(4);
                Long fromuser = resultSet.getLong(5);
                String message = resultSet.getString(6);
                LocalDateTime date = resultSet.getTimestamp(7).toLocalDateTime();
                Long reply = resultSet.getLong(8);
                Message message1 = new Message(fromuser, message, date);
                List<Long> to = new ArrayList<>();
                to.add(touser);
                message1.setTo(to);
                message1.setId(new Tuple<>(id, idm));
                message1.setReply(reply);
                message1.setGroup(group);
                messages.add(message1);
            }
            ps.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return messages;
    }

    public List<Message> getConversationsFriendDate(Long user1, Long user2, LocalDate startDate, LocalDate endDate) {
        List<Message> messages = new ArrayList<>();
        String sql = "select messages.id, messages.idm, messages.touser, conversations.fromuser, " +
                "conversations.message, conversations.date, messages.reply from messages " +
                "inner join conversations on messages.idm = conversations.id " +
                "where ((messages.touser = ? and conversations.fromuser = ?) " +
                "or (messages.touser = ? and conversations.fromuser = ?)) " +
                "and messages.groupcolumn = 0 and conversations.date between ? and ?";
        try (java.sql.Connection  con = connection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, user1);
            ps.setLong(2, user2);
            ps.setLong(3, user2);
            ps.setLong(4, user1);
            ps.setDate(5, Date.valueOf(startDate));
            ps.setDate(6, Date.valueOf(endDate));
            ResultSet resultSet = ps.executeQuery();
            int nr = 0;
            while (resultSet.next()) {
                Long id = resultSet.getLong(1);
                Long idm = resultSet.getLong(2);
                Long touser = resultSet.getLong(3);
                Long fromuser = resultSet.getLong(4);
                String message = resultSet.getString(5);
                LocalDateTime date = resultSet.getTimestamp(6).toLocalDateTime();
                Long reply = resultSet.getLong(7);
                Message message1 = new Message(fromuser, message, date);
                List<Long> to = new ArrayList<>();
                to.add(touser);
                message1.setTo(to);
                message1.setId(new Tuple<>(id, idm));
                message1.setReply(reply);
                messages.add(message1);
            }
            ps.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return messages;
    }
}