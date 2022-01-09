package com.example.pantera.repository.db;

import com.example.pantera.domain.Message;
import com.example.pantera.domain.Tuple;
import com.example.pantera.repository.Repository;
import com.example.pantera.domain.Connection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class MessageDBRepository implements Repository<Tuple<Long, Long>, Message> {
    private String url;
    private String username;
    private String password;
    private Connection connection;
    public MessageDBRepository(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    public MessageDBRepository(Connection connection) {
        this.connection = connection;
    }

    /**
     * find a reply
     * @param id user id
     * @param message the message
     * @return the message reply
     */
    public Message findReply(Long id, Message message){
        Message reply = null;
        try (java.sql.Connection con = connection.getConnection();
             PreparedStatement statement = con.prepareStatement("select * from conversations c\n" +
                     "inner join messages m on c.id = m.idm");
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                long idMessage = resultSet.getLong("id");
                Long id1 = resultSet.getLong("fromuser");
                Long id2 = resultSet.getLong("touser");
                String string = resultSet.getString("message");
                LocalDateTime dateTime = resultSet.getTimestamp("date").toLocalDateTime();
                if(id1.equals(message.getId().getRight()) && id2.equals(message.getId().getLeft()) &&
                        idMessage < id){
                    reply = new Message(id1, string, dateTime);
                }
            }
            return reply;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reply;
    }

    /**
     *  find a message between two friends
     * @param longLongTuple friendship id
     * @return the message
     */
    @Override
    public Message findOne(Tuple<Long, Long> longLongTuple) {
        Message messages = null;
        try (java.sql.Connection con = connection.getConnection();
             PreparedStatement statement = con.prepareStatement("select * from conversations c\n" +
                     "inner join messages m on c.id = m.idm");
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Long id1 = resultSet.getLong("fromuser");
                Long id2 = resultSet.getLong("touser");
                String string = resultSet.getString("message");
                LocalDateTime dateTime = resultSet.getTimestamp("date").toLocalDateTime();
                if(id1.equals(longLongTuple.getLeft()) && id2.equals(longLongTuple.getRight())){
                    messages = new Message(id2, string, dateTime);
                }
            }
            return messages;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return messages;
    }

    /**
     * find all messages
     * @return all messages
     */
    @Override
    public Iterable<Message> findAll() {
        List<Message> messages = new ArrayList<>();
        try (java.sql.Connection con = connection.getConnection();
             PreparedStatement statement = con.prepareStatement("select * from conversations c\n" +
                     "inner join messages m on c.id = m.idm");
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Long id = resultSet.getLong("id");
                Long id1 = resultSet.getLong("fromuser");
                Long id2 = resultSet.getLong("touser");
                String string = resultSet.getString("message");
                LocalDateTime dateTime = resultSet.getTimestamp("date").toLocalDateTime();

                Message message = new Message(id1, string, dateTime);
                Tuple t = new Tuple(id1, id2);
                message.setId(t);
                message.addUserMessage(id2);
                messages.add(message);
                Message reply = findReply(id, message);
                message.setReply(reply);
            }
            return messages;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return messages;
    }

    /**
     * save a message
     * @param entity
     *         entity must be not null
     * @return null
     */
    @Override
    public Message save(Message entity) {
        String sql = "insert into conversations (fromuser, message, date) values (?, ?, ?)";
        String sql2 = "select * from conversations where id=(select max(id) from conversations)";
        String sql3 = "insert into messages (idm, touser) values (?, ?)";
        try (java.sql.Connection con = connection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            PreparedStatement ps2 = con.prepareStatement(sql2);
            PreparedStatement ps3 = con.prepareStatement(sql3);
            ps.setLong(1, entity.getFrom());
            ps.setString(2, entity.getMessage());
            ps.setTimestamp(3, Timestamp.valueOf(entity.getDate()));
            ps.executeUpdate();
            ps2.executeQuery();
            ResultSet resultSet = ps2.executeQuery();
            if(resultSet.next()) {
                long idM = resultSet.getLong("id");
                for (int i = 0; i < entity.getTo().size(); i++) {
                    ps3.setLong(1, idM);
                    ps3.setLong(2, entity.getTo().get(i));
                    ps3.executeUpdate();
                }
            }
            return new Message(1L, "da", LocalDateTime.now());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Message delete(Tuple<Long, Long> longLongTuple) {
        return null;
    }

    @Override
    public Message update(Message entity) {
        return null;
    }
}
