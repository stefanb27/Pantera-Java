package com.example.pantera.service;

import com.example.pantera.domain.Entity;
import com.example.pantera.domain.Friendship;
import com.example.pantera.domain.Tuple;
import com.example.pantera.domain.User;
import com.example.pantera.domain.validators.ValidateException;
import com.example.pantera.domain.validators.Validator;
import com.example.pantera.repository.Repository;

import java.util.Comparator;
import java.util.List;

public class UserService {
    private Repository<Long, User> userRepository;
    private Repository<Tuple<Long, Long>, Friendship> friendshipRepository;
    private Validator<User> userValidator;

    /**
     * Service constructor responsible for the user related operations
     * @param userRepository - user repository
     * @param friendshipRepository - friendship repository
     */
    public UserService(Repository<Long, User> userRepository, Repository<Tuple<Long,Long>, Friendship> friendshipRepository, Validator<User> userValidator){
        this.userRepository = userRepository;
        this.friendshipRepository = friendshipRepository;
        this.userValidator = userValidator;
    }

    /**
     * add an user to repository
     * @param firstName String
     * @param lastName String
     * @param id Long
     *@throws ValidateException if the id is already used
     */
    public void addUser(String firstName, String lastName, Long id, String email, String password) throws ValidateException{
        User user = new User(firstName, lastName, email, password);
        user.setId(id);
        userValidator.validate(user);
        userRepository.save(user);
    }

    /**
     * update a user
     * @param firstName String
     * @param lastName String
     * @param id Long
     * @throws ValidateException if the user id is not in database
     */
    public void updateUser(String firstName, String lastName, Long id) throws ValidateException{
        User user = new User(firstName, lastName, "", "");
        user.setId(id);
        userValidator.validate(user);
        userRepository.update(user);
    }

    /**
     * find an user in repository
     * @param id Long
     * @return a User Object corresponding the id
     * @throws ValidateException if the user id is not in database
     */
    public User findUser(Long id) throws ValidateException{
        //if(userRepository.findOne(id) == null) throw new ValidateException("Id not in database");
        return userRepository.findOne(id);
    }

    /**
     * delete an user from repository
     * @param id Long
     * @return the Entity deleted
     * @throws ValidateException if the id is invalid
     */
    public User deleteUser(Long id) throws ValidateException{
        User user = userRepository.findOne(id);
        userValidator.validate(user);
        return userRepository.delete(id);
    }

    /**
     * compute the biggest network using a Network object
     * @return a list of long elements (the ids of the users who are part of the network)
     *
     */
    public List<Long> biggestNetwork(){
        Network networkSocial = new Network(friendshipRepository.findAll());
        return networkSocial.biggestComponent();
    }

    /**
     * compute the number of conex networks based on friendships
     * @return an integer representing the value
     */
    public int numberOfNetworks() {
        Network networkSocial = new Network(friendshipRepository.findAll());
        return networkSocial.getNumComponents();
    }

    /**
     * @return all the users from repository
     */
    public Iterable<User> getAllUsers(){
        return userRepository.findAll();
    }

    public Long getMaxId(){
        List<User> all = (List<User>) getAllUsers(); Long maxim = 0L;
        return all.stream().map(Entity::getId).max(Comparator.naturalOrder()).get();
        //return all.stream().map(Entity::getId).collect(Collectors.toList()).stream().max(Comparator.naturalOrder()).get();
//        for(User user : all){
//            if(user.getId() > maxim) maxim = user.getId();
//        }
//        return maxim;
    }

    public User checkEmail(String email) {
        List<User> users = (List<User>) getAllUsers();
        return users.stream().filter(u ->
                u.getEmail().equals(email)).findAny().orElse(null);
    }
}