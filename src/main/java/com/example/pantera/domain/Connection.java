package com.example.pantera.domain;

import java.sql.DriverManager;
import java.sql.SQLException;

public class Connection {
    java.sql.Connection connection = null;
    public java.sql.Connection getConnection() {
        try {
            connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/SocialNetwork", "postgres", "easy27");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }
}