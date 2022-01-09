package com.example.pantera.repository.db;

import com.example.pantera.domain.User;
import com.example.pantera.domain.validators.ValidateException;
import com.example.pantera.repository.Repository;
import com.example.pantera.domain.Connection;

import java.sql.*;
import java.util.*;

public class UserDBRepository implements Repository<Long, User> {
    private String url;
    private String username;
    private String password;
    private Connection connection;
    public UserDBRepository(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    public UserDBRepository(Connection connection) {
        this.connection = connection;
    }

    @Override
    public User findOne(Long id) {
        if (id==null)
            throw new IllegalArgumentException("id must be not null");

        User user1 = null;
        User user2 = null;
        String sqlUser = "select * from users where id = ?";
        String sql = "select u1.first_name, u1.last_name, u2.first_name, u2.last_name, f.id1, f.id2, u1.email, u2.email, u1.password, u2.password\n" +
                "from users u1 inner join friendships f on u1.id = f.id1\n" +
                "inner join users u2 on u2.id = f.id2\n" +
                "where f.id1 = ? or f.id2 = ?";

        try (java.sql.Connection con = connection.getConnection();
             PreparedStatement statement = con.prepareStatement(sql)){
            statement.setLong(1, id);
            statement.setLong(2, id);
            ResultSet resultSet = statement.executeQuery();

            PreparedStatement statement2 = con.prepareStatement(sqlUser);
            statement2.setLong(1, id);
            ResultSet resultSet2 = statement2.executeQuery();

            boolean ok = false;
            if (resultSet2.next()) {
                user1 = new User(resultSet2.getString(2), resultSet2.getString(3),
                        resultSet2.getString(4), resultSet2.getString(5));
                user1.setId(resultSet2.getLong(1));
                ok = true;
            }

            while (resultSet.next()) {
                String firstName1 = resultSet.getString(1);
                String lastName1 = resultSet.getString(2);
                String firstName2 = resultSet.getString(3);
                String lastName2 = resultSet.getString(4);
                Long id1 = resultSet.getLong(5);
                Long id2 = resultSet.getLong(6);
                String email1 = resultSet.getString(7);
                String email2 = resultSet.getString(8);
                String password1 = resultSet.getString(9);
                String password2 = resultSet.getString(10);
                if (id.equals(id2)) {
                    user2 = new User(firstName1, lastName1, email1, password1);
                    user2.setId(id1);
                } else if (id.equals(id1)) {
                    user2 = new User(firstName2, lastName2, email2, password2);
                    user2.setId(id2);
                }
                user1.addFriend(user2);
            }
            if (ok) {
                return user1;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        throw new ValidateException("Id not found");

//        if(id == null)
//            throw new IllegalArgumentException("id must not be null");
//        String sql ="select * from users where id=?";
//        try (Connection connection = DriverManager.getConnection(url, username, password);
//             PreparedStatement statement = connection.prepareStatement(sql)) {
//
//            statement.setLong(1,id);
//            ResultSet resultSet = statement.executeQuery();
//
//            while (resultSet.next()) {
//                Long id1 = resultSet.getLong("id");
//                String firstName = resultSet.getString("first_name");
//                String lastName = resultSet.getString("last_name");
//                User user = new User(firstName, lastName);
//                user.setId(id1);
//                return user;
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        throw new ValidateException("Id cannot be found.");
    }

    @Override
    public Iterable<User> findAll() {
        List<User> users = new ArrayList<>();
        try (java.sql.Connection  con = connection.getConnection();
             PreparedStatement statement = con.prepareStatement("SELECT * from users");
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Long id = resultSet.getLong("id");
                User user = findOne(id);
                users.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    @Override
    public User save(User entity) {
        if (entity==null)
            throw new IllegalArgumentException("entity cannot be null");
        try {
            User user = findOne(entity.getId());
        } catch (ValidateException err) {
            String sql = "insert into users (id, first_name, last_name, email, password) values (?, ?, ?, ?, ?)";
            try (java.sql.Connection  con = connection.getConnection();
                 PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setLong(1, entity.getId());
                ps.setString(2, entity.getFirstName());
                ps.setString(3, entity.getLastName());
                ps.setString(4, entity.getEmail());
                ps.setString(5, entity.getPassword());
                ps.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return null;
        }
        throw new ValidateException("Duplicate id found");
    }

    @Override
    public User delete(Long id) {
//        String sql = "delete from users where id = ?";
//        String sql2 = "delete from friendships where id1 = ? or id2 = ?";
//
//        User user = findOne(aLong);
//        try (Connection connection = DriverManager.getConnection(url, username, password);
//             PreparedStatement ps = connection.prepareStatement(sql)) {
//
//            PreparedStatement ps2 = connection.prepareStatement(sql2);
//            ps2.setLong(1, aLong);
//            ps2.setLong(2, aLong);
//
//            ps.setLong(1, aLong);
//            ps.executeUpdate();
//            ps2.executeUpdate();
//        } catch (SQLException throwables) {
//            throwables.printStackTrace();
//        }
//        return user;

        User user = findOne(id);
        String sql = "delete from friendships where id1 = ? or id2 = ?";
        try (java.sql.Connection  con = connection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, user.getId());
            ps.setLong(2, user.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        sql = "delete from users where id = ?";
        try (java.sql.Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user;
    }

    @Override
    public User update(User entity) {
        if(entity == null)
            throw new IllegalArgumentException("entity must be not null!");
        User user = findOne(entity.getId());
        String sql = "update users set first_name = ?, last_name = ? where id = ?";
        try (java.sql.Connection con = connection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(3, entity.getId());
            ps.setString(1, entity.getFirstName());
            ps.setString(2, entity.getLastName());
            ps.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    public List<User> getAll(){
        return (List<User>) findAll();
    }

}
