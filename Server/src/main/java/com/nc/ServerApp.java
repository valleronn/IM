package com.nc;

import com.nc.controller.Server;

/**
 * Server main class on Messenger application.
 */
public class ServerApp {
    public static void main(String[] args) {
        int port = 4444;
        Server server = new Server(port);
        server.start();
    }
}
