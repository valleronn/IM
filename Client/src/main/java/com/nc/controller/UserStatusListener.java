package com.nc.controller;

import com.nc.model.users.User;

/**
 * Represents UserStatusListener interface
 */
public interface UserStatusListener {
    void online(User user);
    void offline(User user);
}
