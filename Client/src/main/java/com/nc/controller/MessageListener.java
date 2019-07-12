package com.nc.controller;

public interface MessageListener {
    void onMessage(String fromLogin, String msgBody);
}
