package com.nc.model.users;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class ChatRoom extends User {
    private String chatName;
    private Set<User> users;

    public ChatRoom() {
        super();
        users = new HashSet<>();
    }

    public String getChatName() {
        return chatName;
    }

    public void setChatName(String chatName) {
        this.chatName = chatName;
        super.setLogin(chatName);
    }

    public Set<User> getUsers() {
        return users;
    }

    public void setUsers(Set<User> users) {
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
