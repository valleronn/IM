package com.nc.model.users;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class User {
    private String login;
    private String fullName;
    private String password;
    private Date regDate;
    private boolean active;
    private List<String> messageList = new LinkedList<>();

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
