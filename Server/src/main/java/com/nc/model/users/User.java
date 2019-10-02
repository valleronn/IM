package com.nc.model.users;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.*;

public class User {
    private String login;
    private String fullName;
    private String password;
    private Date regDate;
    private boolean active;
    private boolean banned;
    private List<String> messageList = new LinkedList<>();
    private ObservableList<User> myContacts = FXCollections.observableArrayList();

    public User() {
    }

    public User(String login, String password, Date regDate) {
        this.login = login;
        this.password = password;
        this.regDate = regDate;
        this.active = false;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public List<String> getMessageList() {
        return messageList;
    }

    public ObservableList<User> getMyContacts() {
        return myContacts;
    }

    public String getFullName() {
        return login;
    }

    public Date getRegDate() {
        return regDate;
    }

    public boolean isActive() {
        return active;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setRegDate(Date regDate) {
        this.regDate = regDate;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isBanned() {
        return banned;
    }

    public void setBanned(boolean banned) {
        this.banned = banned;
    }

    public void setMessageList(List<String> messageList) {
        this.messageList = messageList;
    }

    @Override
    public String toString() {
        return getLogin();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        User user = (User) o;
        return Objects.equals(login, user.login);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((login == null) ? 0 : login.hashCode());
        result = prime * result + ((fullName == null) ? 0 : fullName.hashCode());
        result = prime * result + ((password == null) ? 0 : password.hashCode());
        result = prime * result + ((regDate == null) ? 0 : regDate.hashCode());
        return result;
    }

}
