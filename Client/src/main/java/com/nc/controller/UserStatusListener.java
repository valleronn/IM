package com.nc.controller;

import com.nc.model.users.ChatRoom;
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
    void setUserContacts(ObservableList<User> myContacts);
    void setBanList(ObservableList<User> banList);
    void setUserChatRooms(ObservableList<ChatRoom> myChats);
    void setGroupChatContacts(String chatName, ObservableList<User> groupChatContacts);
}
