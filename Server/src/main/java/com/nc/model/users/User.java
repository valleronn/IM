package com.nc.model.users;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class User {
    private int id;
    private String nickName;
    private String fullName;
    private String password;
    private Date regDate;
    private boolean active;
    private List<String> messageList = new LinkedList<>();

    public User() {
    }

    public User(int id, String nickName, String fullName, String password, Date regDate) {
        this.id = id;
        this.nickName = nickName;
        this.fullName = fullName;
        this.password = password;
        this.regDate = regDate;
        this.active = false;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Date getRegDate() {
        return regDate;
    }

    public void setRegDate(Date regDate) {
        this.regDate = regDate;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public List<String> getMessageList() {
        return messageList;
    }

    public void setMessageList(List<String> messageList) {
        this.messageList = messageList;
    }

    @Override
    public String toString() {
        return getFullName();
    }
}
