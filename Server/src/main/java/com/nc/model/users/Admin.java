package com.nc.model.users;

import java.util.Date;

public class Admin extends User {

    public Admin() {
        super();
    }

    public Admin(String login, String password, Date regDate) {
        super(login, password, regDate);
    }

    public void changeProfile(int userId) {

    }

    public void remove(int userId) {

    }

    public void addToBanList(int userId) {
        
    }
}
