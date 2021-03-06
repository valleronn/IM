package com.nc.model.users;

import java.util.Date;
import java.util.List;

import javafx.collections.ObservableList;

public class Admin extends User {
    private BanList banList;

    public Admin(String login, String password, Date regDate, BanList banList) {
        super(login, password, regDate);
        this.banList = banList;
    }

    public BanList getBanList() {
        return banList;
    }

    public void removeUserFromGroupChat(String login, List<ChatRoom> chatRooms) {
        for(ChatRoom chatRoom: chatRooms) {
            ObservableList<User> usersInChat = chatRoom.getUsers();
            usersInChat.removeIf(user -> (user.getLogin().equals(login)));
        }
    }

    public void addToBanList(User user) {
        banList.addBan(user);
    }

    public void removeFromBanList(User user) {
        banList.removeBan(user);
    }
}
