package com.example.pantera;

import com.example.pantera.domain.Connection;
import com.example.pantera.domain.validators.FriendshipValidator;
import com.example.pantera.domain.validators.UserValidator;
import com.example.pantera.repository.db.FriendshipDBRepository;
import com.example.pantera.repository.db.MessageDBRepository;
import com.example.pantera.repository.db.UserDBRepository;
import com.example.pantera.service.FriendshipService;
import com.example.pantera.service.MessageService;
import com.example.pantera.service.UserService;
import com.example.pantera.ui.Ui;

import java.time.LocalDateTime;

public class Main {
    public static void main(String[] args) {
//        Connection connection = new Connection();
//        UserDBRepository userDBRepository = new UserDBRepository(connection);
//
//        FriendshipDBRepository friendshipDBRepository = new FriendshipDBRepository(connection);
//        UserService userService = new UserService(userDBRepository, friendshipDBRepository, new UserValidator());
//        FriendshipService friendshipService = new FriendshipService(userDBRepository, friendshipDBRepository, new FriendshipValidator());
//        MessageDBRepository messageDBRepository = new MessageDBRepository(connection);
//        MessageService messageService = new MessageService(userDBRepository, friendshipDBRepository, messageDBRepository);
//        Ui ui = new Ui(userService, friendshipService, messageService);
//        ui.run();

    }
}