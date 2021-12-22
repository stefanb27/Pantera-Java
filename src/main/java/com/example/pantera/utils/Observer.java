package com.example.pantera.utils;

import com.example.pantera.events.Event;

public interface Observer<E extends Event> {
    void update(E e);
}