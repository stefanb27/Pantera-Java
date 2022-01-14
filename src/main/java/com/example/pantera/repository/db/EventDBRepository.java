package com.example.pantera.repository.db;

import com.example.pantera.domain.*;
import com.example.pantera.repository.paging.Page;
import com.example.pantera.repository.paging.Pageable;
import com.example.pantera.repository.paging.Paginator;
import com.example.pantera.repository.paging.PagingRepository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EventDBRepository implements PagingRepository<Long, NiceEvent> {

    private Connection connection;
    public EventDBRepository(Connection connection) {
        this.connection = connection;
    }

    @Override
    public NiceEvent save(NiceEvent niceEvent) {
        String sql = "insert into events (nameevent, createdby, dateevent, hour) values (?, ?, ?, ?)";
        try (java.sql.Connection con = connection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, niceEvent.getNameEvent());
            ps.setString(2, niceEvent.getCreatedBy()); //aici am numele grupului
            ps.setString(3, niceEvent.getDateTime());
            ps.setString(4, niceEvent.getHours());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<NiceEvent> getAllEvents(){
        List<NiceEvent> niceEvents = new ArrayList<>();
        String sql = "select * from events";
        try (java.sql.Connection  con = connection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ResultSet resultSet = ps.executeQuery();
            while (resultSet.next()) {
                Long idEvent = resultSet.getLong(1);
                String nameEvent = resultSet.getString(2);
                String createdBy = resultSet.getString(3);
                String dateTime = resultSet.getString(4);
                String hours = resultSet.getString(5);
                NiceEvent niceEvent = new NiceEvent(nameEvent, createdBy, dateTime, hours); niceEvent.setId(idEvent);
                niceEvents.add(niceEvent);
            }
            return niceEvents;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return niceEvents;
    }

    public void saveUserEvent(Long idUser, Long idEvent){
        String sql = "insert into eventuser (idevent, iduser) values (?, ?)";
        try (java.sql.Connection con = connection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, idEvent);
            ps.setLong(2, idUser);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deletUserEvent(Long idUser, Long idEvent){
        String sql = "delete from eventuser where idevent = ? and iduser = ?";
        try (java.sql.Connection con = connection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, idEvent);
            ps.setLong(2, idUser);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean isGoing(Long idUser, Long idEvent){
        String sql = "select * from eventuser where idevent = ? and iduser = ?";
        try (java.sql.Connection con = connection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, idEvent);
            ps.setLong(2, idUser);
            ResultSet resultSet = ps.executeQuery();
            return resultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public Iterable<NiceEvent> findAll() {
        List<NiceEvent> niceEvents = new ArrayList<>();
        String sql = "select * from events";
        try (java.sql.Connection  con = connection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ResultSet resultSet = ps.executeQuery();
            while (resultSet.next()) {
                Long idEvent = resultSet.getLong(1);
                String nameEvent = resultSet.getString(2);
                String createdBy = resultSet.getString(3);
                String dateTime = resultSet.getString(4);
                String hours = resultSet.getString(5);
                NiceEvent niceEvent = new NiceEvent(nameEvent, createdBy, dateTime, hours); niceEvent.setId(idEvent);
                niceEvents.add(niceEvent);
            }
            return niceEvents;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return niceEvents;
    }

    @Override
    public Page<NiceEvent> findAll(Pageable pageable) {
        List<NiceEvent> niceEvents = new ArrayList<>();
        String sql = "select * from events";
        try (java.sql.Connection  con = connection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ResultSet resultSet = ps.executeQuery();
            while (resultSet.next()) {
                Long idEvent = resultSet.getLong(1);
                String nameEvent = resultSet.getString(2);
                String createdBy = resultSet.getString(3);
                String dateTime = resultSet.getString(4);
                String hours = resultSet.getString(5);
                NiceEvent niceEvent = new NiceEvent(nameEvent, createdBy, dateTime, hours); niceEvent.setId(idEvent);
                niceEvents.add(niceEvent);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        Iterable<NiceEvent> events1 = niceEvents;
        Paginator<NiceEvent> paginator= new Paginator<>(pageable, events1);
        return paginator.paginate();
    }

    @Override
    public NiceEvent findOne(Long aLong) {
        return null;
    }

    @Override
    public NiceEvent delete(Long aLong) {
        return null;
    }

    @Override
    public NiceEvent update(NiceEvent entity) {
        return null;
    }
}