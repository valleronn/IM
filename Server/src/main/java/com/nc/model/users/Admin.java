package com.nc.model.users;

import java.util.Date;
import com.nc.controller.Server;
import javafx.collections.ObservableList;

public class Admin extends User {

    Server server;
    BanList banList;

    public Admin(String login, String password, Date regDate, Server server, BanList banList) {
        super(login, password, regDate);
        this.server = server;
        this.banList = server.getBanList();
    }

    public void removeUserFromGroupChat(String login) {
        for(ChatRoom chatRoom: server.getChatRooms()) {
            ObservableList<User> usersInChat = chatRoom.getUsers();
            usersInChat.removeIf(user -> (user.getLogin().equals(login)));
        }
    }

    public void addToBanList(String login) {
        User user = server.getUser(login);
        banList.addBan(user);
    }

    public void removeFromBanList(String login) {
        User user = server.getUser(login);
        banList.removeBan(user);
    }
}
