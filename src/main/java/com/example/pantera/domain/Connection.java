package com.example.pantera.domain;

import java.sql.DriverManager;
import java.sql.SQLException;

public class Connection {
    java.sql.Connection connection = null;
    public java.sql.Connection getConnection() {
        try {
            connection = DriverManager.getConnection("jdbc:postgresql://localhost:9292/lab5", "postgres", "fnscarh9");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }
}