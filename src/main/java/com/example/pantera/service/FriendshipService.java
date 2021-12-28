package com.example.pantera.service;

import com.example.pantera.events.ChangeEventType;
import com.example.pantera.events.FriendshipChangeEvent;
import com.example.pantera.domain.Entity;
import com.example.pantera.domain.Friendship;
import com.example.pantera.domain.Tuple;
import com.example.pantera.domain.User;
import com.example.pantera.domain.validators.ValidateException;
import com.example.pantera.domain.validators.Validator;
import com.example.pantera.repository.Repository;
import com.example.pantera.utils.Observable;
import com.example.pantera.utils.Observer;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.*;
import java.util.stream.Collectors;

public class FriendshipService extends Observable<FriendshipChangeEvent> {

    private final Repository<Long, User> repositoryUser;
    private final Repository<Tuple<Long, Long>, Friendship> repositoryFriends;
    private final Validator<Friendship> friendshipValidator;

    /**
     * service constructor
     * @param repositoryUser store all the users data
     * @param repositoryFriends store all the friendships data
     */
    public FriendshipService(Repository<Long, User> repositoryUser, Repository<Tuple<Long,Long>, Friendship> repositoryFriends, Validator<Friendship> friendshipValidator){
        this.repositoryUser = repositoryUser;
        this.repositoryFriends = repositoryFriends;
        this.friendshipValidator = friendshipValidator;
    }

    /**
     * add the id2 corresponding user as friend for user with id1 and vice versa
     * @param id1 Long id for user1
     * @param id2 Long id for user2
     * @throws ValidateException if an id is not found in repository, or if the friendship already exists
     */
    public void addFriendship(Long id1, Long id2) throws ValidateException{
        Friendship f = new Friendship();
        Tuple t = new Tuple(id1, id2); f.setId(t); f.setDateTime(LocalDateTime.now());
        if(repositoryUser.findOne(id1) == null) throw new ValidateException("First id does not exist");
        if(repositoryUser.findOne(id2) == null) throw new ValidateException("Second id does not exist");
        if (id1.equals(id2)) throw new ValidateException("The audacity :(");
        Entity friendSave = repositoryFriends.save(f);
        if(friendSave == null){
            notifyObservers(new FriendshipChangeEvent(ChangeEventType.ADD, (Friendship) friendSave));
        }
//        User user1 = repositoryUser.findOne(id1);
//        User user2 = repositoryUser.findOne(id2);
//        user2.addFriend(user2); user1.addFriend(user1);
    }

    /**
     * delete friendship between 2 users
     * @param id1 Long id for user1
     * @param id2 Long id for user2
     * @throws ValidateException if an id is not found in repository, or if the friendship does not exist
     */
    public void deleteFriendship(Long id1, Long id2) throws ValidateException{
        repositoryUser.findOne(id1);
        repositoryUser.findOne(id2);
        Tuple t = new Tuple(id1, id2);
//        if(repositoryUser.findOne(id1) == null) throw new ValidateException("First id does not exist");
//        if(repositoryUser.findOne(id2) == null) throw new ValidateException("Second id does not exist");
        Entity friendSave = repositoryFriends.delete(t);
        if(friendSave != null){
            notifyObservers(new FriendshipChangeEvent(ChangeEventType.DELETE, (Friendship) friendSave));
        }
        if(friendSave == null) throw new ValidateException("Ids are not in any friendship");
//        repositoryUser.findOne(id1).removeFriend(repositoryUser.findOne(id2));
//        repositoryUser.findOne(id2).removeFriend(repositoryUser.findOne(id1));
    }

    /**
     * @return an Iterable list with all the friendships
     */
    public Iterable<Friendship> getFriendships() {  return repositoryFriends.findAll(); }

    /**
     * reject a friendship
     * @param id1 user1
     * @param id2 user2
     */
    public void rejectFriendship(Long id1, Long id2){
        repositoryUser.findOne(id1);
        repositoryUser.findOne(id2);
        Tuple<Long, Long> t = new Tuple<>(id1, id2);
        Friendship friendship = repositoryFriends.findOne(t);
        if (friendship.getStatus().equals("pending")) {
            friendship.setStatus("rejected");
            repositoryFriends.update(friendship);
            updateDataFriendships();
        } else {
            throw new ValidateException("Invalid friend request");
        }
    }

    /**
     * approve a friendship
     * @param id1 first user
     * @param id2 second user
     */
    public void approveFriendship(Long id1, Long id2){
        repositoryUser.findOne(id1);
        repositoryUser.findOne(id2);
        Tuple<Long, Long> t = new Tuple<>(id1, id2);
        Friendship friendship = repositoryFriends.findOne(t);
        if (friendship.getStatus().equals("pending")) {
            friendship.setStatus("approved");
            Entity friendSave = repositoryFriends.update(friendship);
            if(friendSave != null){
                notifyObservers(new FriendshipChangeEvent(ChangeEventType.DELETE, (Friendship) friendSave));
            }
            //updateDataFriendships();
        } else {
            throw new ValidateException("Invalid friend request");
        }
    }

    /**
     * get friends of given user
     * @param idUser the user
     * @return a tuple list that contains: friend's name and friendship date
     */
    public List<Tuple> friendsOfUser(Long idUser) {
        List<Tuple> result = new ArrayList<>();
        List<Friendship> rez = (List<Friendship>) getFriendships();
        result = rez.stream().filter(x -> Objects.equals(x.getId().getLeft(), idUser) || Objects.equals(x.getId().getRight(), idUser))
                .filter(x -> x.getStatus().equals("approved"))
                .map(x -> {
                    if (!Objects.equals(x.getId().getLeft(), idUser)) {;
                        Tuple t = new Tuple(repositoryUser.findOne(x.getId().getLeft()), x.getDateTime());
                        return t;
                    }
                    else {
                        Tuple t = new Tuple(repositoryUser.findOne(x.getId().getRight()), x.getDateTime());
                        return t;
                    }
                })
                .collect(Collectors.toCollection(ArrayList::new));
        return result;
    }

    /**
     * get friends of given user and month
     * @param idUser user
     * @param month the month
     * @return a tuple list that contains: friend's name and friendship date
     */
    public List<Tuple> friendsOfUserMonth(Long idUser, Month month) {
        List<Tuple> result = new ArrayList<>();
        List<Friendship> rez = (List<Friendship>) getFriendships();
        result = rez.stream().filter(x -> Objects.equals(x.getId().getLeft(), idUser) || Objects.equals(x.getId().getRight(), idUser))
                .filter(x -> Objects.equals(x.getDateTime().getMonth(), month))
                .map(x -> {
                    if (!Objects.equals(x.getId().getLeft(), idUser)) {;
                        return new Tuple(repositoryUser.findOne(x.getId().getLeft()), x.getDateTime());
                    }
                    else {
                        return new Tuple(repositoryUser.findOne(x.getId().getRight()), x.getDateTime());
                    }
                })
                .collect(Collectors.toCollection(ArrayList::new));
        return result;
    }

    /**
     * general getter for given status
     * @param id the user
     * @param status the status
     * @return list of all ids given the status
     */
    public List<Long> generalGetter(Long id, String status) {
        List<Friendship> all = new ArrayList<>();
        Iterable<Friendship> iterable = repositoryFriends.findAll();
        iterable.forEach(all::add);
        List<Long> result = new ArrayList<>();
        all.stream()
                .filter(x -> x.getStatus().equals(status))
                .map(x -> x.getId().getRight())
                .forEach(result::add);
        return result;
    }

    /**
     * get a list of all approved requests
     * @param id the user
     * @return list of all approved requests
     */
    public List<Long> getAllApproved(Long id){
        return generalGetter(id, "approved");
    }

    /**
     * get list of all requests
     * @param id given user
     * @return list of user ids
     */
    public List<Friendship> getAllRequest(Long id){
        List<Friendship> all = new ArrayList<>();
        Iterable<Friendship> iterable = repositoryFriends.findAll();
        iterable.forEach(all::add);
        List<Friendship> result = new ArrayList<>();
        all.stream()
                .filter(x -> x.getStatus().equals("pending"))
                .map(x -> {
                    if (x.getId().getRight().equals(id)) {
                        return x;
                    }
                    return null;
                })
                .forEach(result::add);
        return result.stream().filter(Objects::nonNull).toList();
    }

    /**
     * helper method for findFriendship from repo
     * @param id the friendship
     * @return the friendship or null if there is an exception
     */
    public Friendship findFriendship(Tuple<Long, Long> id) {
        try {
            return repositoryFriends.findOne(id);
        } catch (ValidateException e) {
            return null;
        }
    }

    /**
     * add the friendship in the database
     */
    public void updateDataFriendships() {
        List<Friendship> all = new ArrayList<>();
        Iterable<Friendship> iterable = repositoryFriends.findAll();
        iterable.forEach(all::add);
        for (Friendship friendship : all) {
            if (friendship.getStatus().equals("approved")) {
                addFriendship(friendship.getId().getLeft(), friendship.getId().getRight());
            }
        }
    }

    /**
     * transforms a list of ids in a list of users
     * @param longList list of ids
     * @return list of users
     */
    public List<User> getUsersMatched(List<Long> longList){
        List<User> userList = new ArrayList<>();
        longList.stream()
                .map(repositoryUser::findOne)
                .forEach(userList::add);
        return userList;
    }

    public Iterable<User> getAllFriends(Long id){
        List<User> l = new ArrayList<>();
        List<Tuple> friendOfAnUser = friendsOfUser(id);
        friendOfAnUser.forEach(u -> l.add((User) u.getLeft()));
        System.out.println(l);
        return l;
    }

    // useless for the project?
    /**
     * add a friendship as pending
     * @param id1 first id
     * @param id2 second id
     * @throws ValidateException
     *          if they are already friends or a request was already sent
     */
    public void addFriendshipPending(Long id1, Long id2){
        repositoryUser.findOne(id1);
        repositoryUser.findOne(id2);
        Tuple t = new Tuple<>(id1, id2);
        Tuple reverseT = new Tuple<>(id2, id1);
        Friendship friendship = new Friendship();
        friendship.setId(t);
        friendship.setDateTime(LocalDateTime.now());
    }

    /**
     * get a list of all pending requests
     * @param id the user
     * @return list of all pending ids
     */
    public List<Long> getAllPendings(Long id) {
        return generalGetter(id, "pending");
    }

    /**
     * get a list of all rejected requests
     * @param id the user
     * @return list of all rejected requests
     */
    public List<Long> getAllRejected(Long id){
        return generalGetter(id, "rejected");
    }
}
