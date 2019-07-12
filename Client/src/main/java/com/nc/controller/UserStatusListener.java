package com.nc.controller;

import com.nc.model.users.User;

public interface UserStatusListener {
    void online(User user);
    void offline(User user);
}
