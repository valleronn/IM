package com.nc.controller;

/**
 * Represents UserStatusListener interface
 */
public interface UserStatusListener {
    void online(String user);
    void offline(String user);
    void invite(String fromUser);
}
