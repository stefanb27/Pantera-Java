package com.example.pantera.repository.db;

import com.example.pantera.domain.Friendship;
import com.example.pantera.domain.Tuple;
import com.example.pantera.domain.validators.ValidateException;
import com.example.pantera.repository.Repository;
import com.example.pantera.domain.Connection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

public class FriendshipDBRepository implements Repository<Tuple<Long, Long>, Friendship> {
    private String url;
    private String username;
    private String password;
    private Connection connection;

    public FriendshipDBRepository(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    public FriendshipDBRepository(Connection connection) {
        this.connection = connection;
    }

    @Override
    public Friendship findOne(Tuple<Long, Long> id) {
        if(id == null)
            throw new IllegalArgumentException("Id cannot be null.");
        Friendship friendship = null;
        try (java.sql.Connection con = connection.getConnection();
             PreparedStatement statement = con.prepareStatement("SELECT * from friendships");
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Long id1 = resultSet.getLong("id1");
                Long id2 = resultSet.getLong("id2");
                LocalDateTime dateTime = resultSet.getTimestamp("date").toLocalDateTime();
                String status = resultSet.getString("status");

                if((id1.equals(id.getLeft()) && id2.equals(id.getRight())) ||
                        ((id1.equals(id.getRight())) && id2.equals(id.getLeft()))) {
                    friendship = new Friendship();
                    Tuple t = new Tuple(id1, id2);
                    friendship.setId(t);
                    friendship.setDateTime(dateTime);
                    friendship.setStatus(status);
                    return friendship;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        throw new ValidateException("Id cannot be found.");
    }

    @Override
    public Iterable<Friendship> findAll() {
        List<Friendship> friendships = new ArrayList<>();
        try (java.sql.Connection con = connection.getConnection();
             PreparedStatement statement = con.prepareStatement("SELECT * from friendships");
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Long id1 = resultSet.getLong("id1");
                Long id2 = resultSet.getLong("id2");
                LocalDateTime dateTime = resultSet.getTimestamp("date").toLocalDateTime();
                String status = resultSet.getString("status");

                Friendship friendship = new Friendship();
                Tuple t = new Tuple(id1, id2);
                friendship.setId(t);
                friendship.setDateTime(dateTime);
                friendship.setStatus(status);
                friendships.add(friendship);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return friendships;
    }

    @Override
    public Friendship save(Friendship entity) {
        if (entity==null)
            throw new IllegalArgumentException("entity cannot be null");
        try {
            Friendship friendship = findOne(entity.getId());
        }
        catch (ValidateException err) {
            String sql = "insert into friendships (id1, id2, date, status) values (?, ?, ?, ?)";
            try (java.sql.Connection con = connection.getConnection();
                 PreparedStatement ps = con.prepareStatement(sql)) {

                ps.setLong(1, entity.getId().getLeft());
                ps.setLong(2, entity.getId().getRight());
                ps.setTimestamp(3, Timestamp.valueOf(entity.getDateTime()));
                ps.setString(4,"pending");
                ps.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return null;
        }
        throw new ValidateException("Duplicate id found.");
    }

    @Override
    public Friendship delete(Tuple<Long, Long> id) {
        String sql = "delete from friendships where id1 = ? and id2 = ? or id2 = ? and id1 = ? ";
        Friendship friendship = findOne(id);
        try (java.sql.Connection con = connection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, id.getLeft());
            ps.setLong(2, id.getRight());
            ps.setLong(3, id.getLeft());
            ps.setLong(4, id.getRight());
            ps.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return friendship;
    }

    @Override
    public Friendship update(Friendship entity) {
        String sql = "update friendships set status = ? where id1 = ? and id2 = ?";
        try (java.sql.Connection con = connection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(2, entity.getId().getLeft());
            ps.setLong(3, entity.getId().getRight());
            ps.setString(1, entity.getStatus());
            ps.executeUpdate();
            return entity;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }
}
