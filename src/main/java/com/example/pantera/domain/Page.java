package com.example.pantera.domain;

import com.example.pantera.events.Event;

import java.util.ArrayList;
import java.util.List;

public class Page extends User{

    public Page(String firstName, String lastName, String email, String password) {
        super(firstName, lastName, email, password);
    }

    List<Message> messages;
    List<User> friends;
    List<NotificationsWrapper> requestsReceived;
    List<Long> requestsSent;
    List<NiceEvent> events;

    public List<NiceEvent> getEvents(){
        return events;
    }

    public void setEvents(List<NiceEvent> events){
        this.events = events;
    }

    @Override
    public List<User> getFriends() {
        return friends;
    }

    public List<NotificationsWrapper> getRequestsReceived() {
        return requestsReceived;
    }

    public List<Long> getRequestsSent() {
        return requestsSent;
    }

    public  void addFriend(User user){ this.friends.add(user); }

    public void setFriends(List<User> friends) {
        this.friends = friends;
    }

    public void setRequestsReceived(List<NotificationsWrapper> requests) {
        this.requestsReceived = requests;
    }

    public void setRequestsSent(List<Long> requests) {
        this.requestsSent = requests;
    }

    public void addRequest(Long id){
        requestsSent.add(id);
    }

    public void addMessage(Message message){
        //cool
        messages.add(message);
    }

//    public void setMessages(List<Message> messages) {
//        this.messages = messages;
//    }

//    public List<Message> getMessages(Long idUser) {
//        List<Message> messageList = new ArrayList<>();
//        for(Message message : messages){
//            if(message.getFrom().equals(idUser) || message.getTo().equals(idUser)){
//                messageList.add(message);
//            }
//        }
//        return messageList;
//    }
}
