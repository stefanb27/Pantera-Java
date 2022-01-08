package com.example.pantera.domain;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Message extends Entity<Tuple<Long, Long>> {
    private Message reply = null;
    private Long from;
    private List<Long> to = new ArrayList<>();
    private String message;
    private LocalDateTime date;

    public Message(Long from, String message, LocalDateTime date) {
        this.from = from;
        this.message = message;
        this.date = date;
        //this.reply = reply;
    }

    public Long getFrom() {
        return from;
    }

    public void setFrom(Long from) {
        this.from = from;
    }

    public List<Long> getTo() {
        return to;
    }

    public Message getReply() { return reply; }

    public void setTo(List<Long> to) {
        this.to = to;
    }

    public void addUserMessage(Long user) {
        this.to.add(user);
    }

    public String getMessage() {
        return message;
    }

    public void setReply(Message message) {
        this.reply = message;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "Message{" +
                "message='" + message + '\'' +
                ", date=" + date +
                '}';
    }
}
