package com.example.pantera.service;

import com.example.pantera.controller.MenuButtonsController;
import com.example.pantera.domain.Connection;
import com.example.pantera.domain.NiceEvent;
import com.example.pantera.domain.validators.FriendshipValidator;
import com.example.pantera.domain.validators.UserValidator;
import com.example.pantera.events.ChangeEventType;
import com.example.pantera.events.FriendshipChangeEvent;
import com.example.pantera.repository.db.EventDBRepository;
import com.example.pantera.repository.db.FriendshipDBRepository;
import com.example.pantera.repository.db.UserDBRepository;
import com.example.pantera.repository.paging.Page;
import com.example.pantera.repository.paging.Pageable;
import com.example.pantera.repository.paging.PageableImplementation;
import com.example.pantera.utils.Observable;

import java.util.Set;
import java.util.stream.Collectors;

public class EventsService extends Observable<FriendshipChangeEvent> {
    Connection connection = new Connection();
    UserDBRepository userDBRepository = new UserDBRepository(connection);
    FriendshipDBRepository friendshipDBRepository = new FriendshipDBRepository(connection);
    UserService userService = new UserService(userDBRepository, friendshipDBRepository, new UserValidator());
    FriendshipService friendshipService = new FriendshipService(userDBRepository, friendshipDBRepository, new FriendshipValidator());
    MenuButtonsController menuButtonsController;
    EventDBRepository eventDBRepository = new EventDBRepository(connection);

    public NiceEvent saveEvent(NiceEvent niceEvent) {
        eventDBRepository.save(niceEvent);
        notifyObservers(new FriendshipChangeEvent(ChangeEventType.ADD, niceEvent));
        return null;
    }

    public Iterable<NiceEvent> getAll() {
        return eventDBRepository.findAll();
    }

    private int page = 0;
    private int size = 1;

    private Pageable pageable;

    public void setPageSize(int size) {
        this.size = size;
    }

//    public void setPageable(Pageable pageable) {
//        this.pageable = pageable;
//    }

    public Set<NiceEvent> getNextMessages() {
//        Pageable pageable = new PageableImplementation(this.page, this.size);
//        Page<MessageTask> studentPage = repo.findAll(pageable);
//        this.page++;
//        return studentPage.getContent().collect(Collectors.toSet());
        this.page++;
        return getMessagesOnPage(this.page);
    }

    public Set<NiceEvent> getMessagesOnPage(int page) {
        this.page=page;
        Pageable pageable = new PageableImplementation(page, this.size);
        Page<NiceEvent> studentPage = eventDBRepository.findAll(pageable);
        return studentPage.getContent().collect(Collectors.toSet());
    }
}
