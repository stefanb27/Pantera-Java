package com.example.pantera.domain;

import java.util.List;

public class Group extends User{
    List<User> groupGuys;
    public Group(String firstName, String lastName, String email, String password, List<User> groupGuys) {
        super(firstName, lastName, email, password);
        this.groupGuys = groupGuys;
    }

    public List<User> getGroupGuys(){
        return groupGuys;
    }


}
