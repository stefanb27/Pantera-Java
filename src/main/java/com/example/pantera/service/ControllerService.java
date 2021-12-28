package com.example.pantera.service;

import com.example.pantera.domain.*;
import com.example.pantera.repository.Repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    public User checkLogIn(String email, String password) {
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
                User user = new User(firstName, lastName, email, password);
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
        String sql = "select friendships.id1, users.first_name, users.last_name, friendships.date " +
                "from users inner join friendships on friendships.id2 = ?" +
                "where users.id = friendships.id1 and friendships.status = ?";
        try (java.sql.Connection  con = connection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, user.getId());
            ps.setString(2, "pending");
            ResultSet resultSet = ps.executeQuery();
            while (resultSet.next()) {
                Long id = resultSet.getLong(1);
                String firstName = resultSet.getString(2);
                String lastName = resultSet.getString(3);
                LocalDateTime date = resultSet.getTimestamp(4).toLocalDateTime();
                NotificationsWrapper anUser = new NotificationsWrapper(id, firstName, lastName, date);
                anUser.setId(id);
                users.add(anUser);
            }
            ps.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
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
            ps.setString(2, searchText);
            ps.setString(3, searchText);
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
}
