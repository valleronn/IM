package com.nc.model.users;

import java.util.Date;
import com.nc.controller.Server;

public class Admin extends User {

    Server server;
    BanList banList;

    public Admin(String login, String password, Date regDate, Server server, BanList banList) {
        super(login, password, regDate);
        this.server = server;
        this.banList = server.getBanList();
    }

    public void remove(String login) {
        User user = server.getUser(login);
        server.getUsers().remove(user);
    }

    public void addToBanList(User user) {
        banList.addBan(user);
    }

    public void removeFromBanlist(User user) {
        banList.removeBan(user);
    }
}
