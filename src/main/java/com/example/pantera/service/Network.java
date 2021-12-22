package com.example.pantera.service;

import com.example.pantera.domain.Friendship;

import java.util.*;

public class Network {
    private Map<Long, ArrayList<Long>> socialNetwork = new HashMap<>(); //adjNodes
    Iterable<Friendship> friendsList;

    /**
     * Network constructor
     * @param friendsList list of friendships
     */
    public Network(Iterable<Friendship> friendsList){
        this.friendsList = friendsList;
        createSocialNetwork();
    }

    /**
     * based on friendship list creates the graph network
     */
    void createSocialNetwork(){
        for (Friendship friendship : friendsList) {
            addUser(friendship.getId().getLeft());
            addUser(friendship.getId().getRight());
        }

        for(Friendship friendship : friendsList){
            addEdge(friendship.getId().getLeft(), friendship.getId().getRight());
        }
    }

    /**
     * add an user to the map, if it does not exist
     * @param idUser Long id for user
     */
    public void addUser(Long idUser){
        socialNetwork.putIfAbsent(idUser, new ArrayList<>());
    }

    /**
     * add in map the connections between users
     * @param idUser1 Long id for user1
     * @param idUser2 Long id for user2
     */
    public void addEdge(Long idUser1, Long idUser2){
        socialNetwork.get(idUser1).add(idUser2);
        socialNetwork.get(idUser2).add(idUser1);
    }

    /**
     * @param idUser Long id for user1
     * @return a list of Longs representing all the ids of the user1's friends
     */
    List<Long> getAdjFriends(Long idUser){
        return socialNetwork.get(idUser);
    }

    /**
     * perform a dfs operation
     * @param root the node we start from
     * @param visited a list of long representing the nodes which are already visited
     */
    public void dfs(Long root, List<Long> visited){
        Stack<Long> stack = new Stack<>();
        stack.push(root);
        while(!stack.isEmpty()){
            Long node = stack.pop();
            if(!visited.contains(node)){
                visited.add(node);
                for(Long l : getAdjFriends(node)){ stack.push(l);} }
        }
    }

    /**
     * @return the number of components of the network
     */
    public int getNumComponents(){
        int number = 0;
        List<Long> visited = new ArrayList<>();
        for(Long id : socialNetwork.keySet()){
            if(!visited.contains(id)){
                dfs(id, visited);
                number += 1;
            }
        }
        return number;
    }

    /**
     * @return the biggest component of the network
     */
    public List<Long> biggestComponent(){
        List<Long> bestFriends = new ArrayList<>(); int maxNumber = 0;
        List<Long> visited = new ArrayList<>(); int numberOfVisited = 0;
        for(Long id : socialNetwork.keySet()) {
            if (!visited.contains(id)) {
                dfs(id, visited);
                int usersVisited = visited.size() - numberOfVisited;
                numberOfVisited = numberOfVisited + visited.size() - numberOfVisited;
                if (usersVisited > maxNumber) {
                    bestFriends.clear();
                    maxNumber = usersVisited;
                    bestFriends.addAll(visited.subList(visited.size() - usersVisited, visited.size()));
                }
            }
        }
        return bestFriends;
    }
}



//
//    public void removeUser(String first, String second, Long idUser){
//        User user = new User(first, second); user.setId(idUser);
//        socialNetwork.values().stream().forEach(e -> e.remove(user.getId()));
//        User userCopy = new User(first, second); user.setId(idUser);
//        socialNetwork.remove(userCopy);
//    }

//    public void removeEdge(Long idUser1, Long idUser2){
//        ArrayList<Long> user1List = socialNetwork.get(idUser1);
//        ArrayList<Long> user2List = socialNetwork.get(idUser2);
//        if(user1List != null) user1List.remove(idUser2);
//        if(user2List != null) user2List.remove(idUser1);
//    }