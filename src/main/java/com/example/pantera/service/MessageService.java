package com.example.pantera.service;

import com.example.pantera.domain.Friendship;
import com.example.pantera.domain.Message;
import com.example.pantera.domain.Tuple;
import com.example.pantera.domain.User;
import com.example.pantera.domain.validators.ValidateException;
import com.example.pantera.events.ChangeEventType;
import com.example.pantera.events.FriendshipChangeEvent;
import com.example.pantera.repository.Repository;
import com.example.pantera.utils.Observable;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class MessageService extends Observable<FriendshipChangeEvent> {
    private Repository<Long, User> userRepository;
    private Repository<Tuple<Long, Long>, Friendship> friendshipRepository;
    private Repository<Tuple<Long, Long>, Message> messageRepository;

    public MessageService(Repository<Long, User> userRepository, Repository<Tuple<Long, Long>, Friendship> friendshipRepository, Repository<Tuple<Long, Long>, Message> messageRepository) {
        this.userRepository = userRepository;
        this.friendshipRepository = friendshipRepository;
        this.messageRepository = messageRepository;
    }

    /**
     * helper method
     * @param id friendship id
     * @return the friendship or null if exception is thrown
     */
    public Friendship findFriendship(Tuple<Long, Long> id) {
        try {
            return friendshipRepository.findOne(id);
        } catch (ValidateException e) {
            return null;
        }
    }

    /**
     * send a message to a list of users
     * @param from the sender
     * @param toUsers the receivers
     * @param string the message
     * @throws ValidateException if there are no friends in the list
     */
    public void sendMessage(Long from, List<Long> toUsers, String string, Long reply, Long group) {
        User fromUser = userRepository.findOne(from);
        List<Long> toUsersWithoutDuplicates = new ArrayList<>(new HashSet<>(toUsers));
        toUsersWithoutDuplicates.forEach(x -> userRepository.findOne(x));
        toUsersWithoutDuplicates.removeIf(x -> findFriendship(new Tuple(from, x)) == null);
        if (toUsersWithoutDuplicates.size() == 0) {
            throw new ValidateException("You are not friends");
        }
        Message message = new Message(from, string, LocalDateTime.now());
        message.setTo(toUsersWithoutDuplicates);
        message.setGroup(group);
        message.setReply(reply);
        Message message1 = messageRepository.save(message);
        if(message1 != null){
            notifyObservers(new FriendshipChangeEvent(ChangeEventType.ADD, message));
        }
    }

    /**
     * show a conversation between two users
     * @param id1 user1
     * @param id2 user2
     * @return list of messages
     */
    public List<Message> showConversations(Long id1, Long id2){
        List<Message> result = new ArrayList<>();
        List<Message> rez = (List<Message>) messageRepository.findAll();
        rez.stream().filter(m -> (m.getFrom().equals(id1) && m.getTo().contains(id2))
                        || (m.getFrom().equals(id2) && m.getTo().contains(id1)))
                .forEach(result::add);
        return result;
    }

    /**
     * get all messages
     * @return list of messages
     */
    public List<Message> getAllMessages(){
        return (List<Message>) messageRepository.findAll();
    }

}
