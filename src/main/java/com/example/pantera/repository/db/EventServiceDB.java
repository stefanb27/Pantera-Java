package com.example.pantera.repository.db;

import com.example.pantera.domain.*;
import com.example.pantera.repository.Repository;
import javafx.scene.shape.StrokeLineCap;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class EventServiceDB {

    private Connection connection;
    public EventServiceDB (Connection connection) {
        this.connection = connection;
    }

    public Event save(Event event) {
        String sql = "insert into events (nameevent, createdby, dateevent, hour) values (?, ?, ?, ?)";
        try (java.sql.Connection con = connection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, event.getNameEvent());
            ps.setString(2, event.getCreatedBy()); //aici am numele grupului
            ps.setString(3, event.getDateTime());
            ps.setString(4, event.getHours());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Event> getAllEvents(){
        List<Event> events = new ArrayList<>();
        String sql = "select * from events";
        try (java.sql.Connection  con = connection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ResultSet resultSet = ps.executeQuery();
            while (resultSet.next()) {
                String nameEvent = resultSet.getString(2);
                String createdBy = resultSet.getString(3);
                String dateTime = resultSet.getString(4);
                String hours = resultSet.getString(5);
                Event event = new Event(nameEvent, createdBy, dateTime, hours);
                events.add(event);
            }
            return events;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return events;
    }

}