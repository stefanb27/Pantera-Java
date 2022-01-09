package com.example.pantera.service;

import com.example.pantera.domain.*;
import com.example.pantera.domain.validators.ValidateException;
import com.example.pantera.repository.Repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class GroupServiceDB {
    private Repository<Long, User> userRepository;
    private Repository<Tuple<Long, Long>, Friendship> friendshipRepository;
    private Repository<Tuple<Long, Long>, Message> messageRepository;
    private Connection connection;

    public GroupServiceDB (Repository<Long, User> userRepository, Repository<Tuple<Long, Long>, Friendship> friendshipRepository, Repository<Tuple<Long, Long>, Message> messageRepository, Connection connection) {
        this.userRepository = userRepository;
        this.friendshipRepository = friendshipRepository;
        this.messageRepository = messageRepository;
        this.connection = connection;
    }

    public Group save(Group group) {
            String sql = "insert into groups (id, name) values (?, ?)";
            try (java.sql.Connection  con = connection.getConnection();
                 PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setLong(1, group.getId());
                ps.setString(2, group.getFirstName()); //aici am numele grupului
                ps.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return null;
    }

    public void saveGroupMessages(Group group, Long fromUser, Message message){
        String sql = "insert into grupConversations (idgroup, fromuser, message) values (?, ?, ?)";
        try (java.sql.Connection  con = connection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, group.getId());
            ps.setLong(2, fromUser); //aici am numele grupului
            ps.setString(3, message.getMessage());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Message> getMessagesForAGroup(Group group){
        List<Message> messages = new ArrayList<>();
        String sql = "select * from grupConversations where idgroup = ?";
        try (java.sql.Connection  con = connection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, group.getId());
            ResultSet resultSet = ps.executeQuery();
            while (resultSet.next()) {
                Long id = resultSet.getLong(1);
                Long fromUser = resultSet.getLong(2);
                String message = resultSet.getString(3);

                Message message1 = new Message(fromUser, message, LocalDateTime.now());
                messages.add(message1);
            }
            return messages;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return messages;
    }

    public Long getIdMax(){
        String sql = "SELECT MAX(Id) FROM groups";
        Long idMax = null;
        try (java.sql.Connection  con = connection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ResultSet resultSet = ps.executeQuery();
            if (resultSet.next()) {
                long id = resultSet.getLong(1);
                idMax = id + 1;
            }
            return idMax;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return idMax;
    }

}
