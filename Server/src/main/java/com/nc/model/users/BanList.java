package com.nc.model.users;

import java.util.HashSet;
import java.util.Set;

public class BanList {

    private Set<User> banList;

    public BanList() {
        this.banList = new HashSet<>();
    }

    public void addBan(User user) {
        banList.add(user);
    }

    public Set<User> getBanList () {
        return banList;
    }

    public void removeBan(User user) {
        banList.remove(user);
    }

}
