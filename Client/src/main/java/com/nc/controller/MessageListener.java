package com.nc.controller;

/**
 * Represents MessageListener interface
 */
public interface MessageListener {
    void onMessage(String fromLogin, String toLogin, String msgBody);
}
