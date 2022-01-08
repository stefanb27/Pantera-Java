package com.example.pantera.domain;

import java.util.List;

public class Page extends User{

    public Page(String firstName, String lastName, String email, String password) {
        super(firstName, lastName, email, password);
    }

    List<Message> messages;
    List<User> friends;
    List<Long> requestsReceived;
    List<Long> requestsSent;

    public List<Message> getMessages() {
        return messages;
    }

    @Override
    public List<User> getFriends() {
        return friends;
    }

    public List<Long> getRequestsReceived() {
        return requestsReceived;
    }

    public List<Long> getRequestsSent() {
        return requestsSent;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    public void setFriends(List<User> friends) {
        this.friends = friends;
    }

    public void setRequestsReceived(List<Long> requests) {
        this.requestsReceived = requests;
    }

    public void setRequestsSent(List<Long> requests) {
        this.requestsSent = requests;
    }

//    public void removeFriend(User friend){
//        this.friends.removeIf(f -> f.getId().equals(friend.getId()));
//    }

    public void addRequest(Long id){
        requestsSent.add(id);
    }

    public void addMessage(Message message){
        messages.add(message);
    }

}
