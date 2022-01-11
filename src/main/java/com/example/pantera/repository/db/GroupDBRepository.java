package com.example.pantera.repository.db;

import com.example.pantera.domain.Group;
import com.example.pantera.domain.User;
import com.example.pantera.domain.validators.ValidateException;
import com.example.pantera.repository.Repository;
import com.example.pantera.domain.Connection;

import java.sql.*;
import java.util.*;

public class GroupDBRepository implements Repository<Long, Group> {
    private String url;
    private Connection connection;

    public GroupDBRepository(String url) {
        this.url = url;
    }

    public GroupDBRepository(Connection connection) {
        this.connection = connection;
    }

    @Override
    public Group findOne(Long id) {
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
