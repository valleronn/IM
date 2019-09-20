package com.nc.controller;

import com.nc.model.users.User;
import javafx.collections.ObservableList;

/**
 * Represents UserStatusListener interface
 */
public interface UserStatusListener {
    void online(String user);
    void offline(String user);
    void invite(String fromUser, ObservableList<User> groupChatContacts);
    void removeFromChat(String chatName);
    void banned();
    void unbanned();
}
