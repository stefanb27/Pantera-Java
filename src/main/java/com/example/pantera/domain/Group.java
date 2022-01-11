package com.example.pantera.domain;

import java.util.List;

public class Group extends Entity<Long>{
    List<User> groupGuys;
    String name;

    public Group(List<User> groupGuys, String name) {
        this.groupGuys = groupGuys;
        this.name = name;
    }

    public List<User> getGroupGuys(){
        return groupGuys;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }
}
