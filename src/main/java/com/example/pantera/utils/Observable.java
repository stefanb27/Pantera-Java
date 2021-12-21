package com.example.pantera.utils;

import com.example.pantera.events.Event;
import com.example.pantera.events.FriendshipChangeEvent;

import java.util.ArrayList;
import java.util.List;

public class Observable<E extends Event> {
    List<Observer<FriendshipChangeEvent>> observers = new ArrayList<>();
    public void addObserver(Observer<FriendshipChangeEvent> e) {
        observers.add(e);
    }
    public void removeObserver(Observer<FriendshipChangeEvent> e) {
        observers.remove(e);
    }
    public void notifyObservers(FriendshipChangeEvent t) {
        observers.stream().forEach(x->x.update(t));
    }
}
