package com.example.pantera.repository.db;

import com.example.pantera.domain.Group;
import com.example.pantera.domain.User;
import com.example.pantera.domain.validators.FriendshipValidator;
import com.example.pantera.domain.validators.UserValidator;
import com.example.pantera.domain.validators.ValidateException;
import com.example.pantera.repository.Repository;
import com.example.pantera.domain.Connection;
import com.example.pantera.service.FriendshipService;
import com.example.pantera.service.UserService;

import java.sql.*;
import java.util.*;

public class GroupDBRepository implements Repository<Long, Group> {
    private String url;
    Connection connection = new Connection();
    UserDBRepository userDBRepository = new UserDBRepository(connection);
    FriendshipDBRepository friendshipDBRepository = new FriendshipDBRepository(connection);
    UserService userService = new UserService(userDBRepository, friendshipDBRepository, new UserValidator());
    FriendshipService friendshipService = new FriendshipService(userDBRepository, friendshipDBRepository, new FriendshipValidator());
    MessageDBRepository messageDBRepository = new MessageDBRepository(connection);


    public GroupDBRepository(String url) {
        this.url = url;
    }

    public GroupDBRepository(Connection connection) {
        this.connection = connection;
    }

    @Override
    public Group findOne(Long id) {
        String sql = "select * from groups where groups.id = ?";
        try (java.sql.Connection  con = connection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, id);
            ResultSet resultSet = ps.executeQuery();
            while (resultSet.next()) {
                String name = resultSet.getString(2);
                String[] helper =  resultSet.getString(3).split(";");
                List<Long> longs = new ArrayList<>();
                for (int i = 0; i < helper.length; i++) {
                    long user1 = Integer.parseInt(helper[i]);
                    longs.add(user1);
                }
                List<User> users = new ArrayList<>();
                for(Long user1 : longs) {
                    User user2 = userDBRepository.findOne(user1);
                    users.add(user2);
                }
                Group group = new Group(users, name);
                return group;
            }
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Iterable<Group> findAll() {
        return null;
    }

    @Override
    public Group save(Group entity) {
        if (entity==null)
            throw new IllegalArgumentException("entity cannot be null");
        String sql = "insert into groups (name, users) values (?, ?)";
        try (java.sql.Connection  con = connection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, entity.getName());
            String helper = "";
            for (User user : entity.getGroupGuys()) {
                helper += user.getId() + ";";
            }
            ps.setString(2, helper);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Group delete(Long id) {
        return null;
    }

    @Override
    public Group update(Group entity) {
        return null;
    }
}
