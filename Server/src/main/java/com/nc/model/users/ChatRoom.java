package com.nc.model.users;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.Objects;

public class ChatRoom extends User {
    private String chatName;
    private ObservableList<User> users = FXCollections.observableArrayList();

    public ChatRoom() {
        super();
    }

    public String getChatName() {
        return chatName;
    }

    public void setChatName(String chatName) {
        this.chatName = chatName;
        super.setLogin(chatName);
    }

    public boolean containsUser(String login) {
        for(User user: users) {
            if (user.getLogin().equals(login)) {
                return true;
            }
        }
        return false;
    }

    public void addUser(User user) {
        users.add(user);
    }

    public ObservableList<User> getUsers() {
        return users;
    }

    public void setUsers(ObservableList<User> users) {
        this.users = users;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ChatRoom)) return false;
        ChatRoom chatRoom = (ChatRoom) o;
        return Objects.equals(chatName, chatRoom.chatName);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((chatName == null) ? 0 : chatName.hashCode());
        result = prime * result + ((users == null) ? 0 : users.hashCode());
        return result;
    }
}
