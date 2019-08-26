package com.nc;

import com.nc.controller.ConfigReader;
import com.nc.controller.Server;
import org.apache.log4j.Logger;

/**
 * Server main class on Messenger application.
 */
public class ServerApp {

    private final static Logger LOGGER = Logger.getLogger(Server.class);


    public static void main(String[] args) {

        ConfigReader configReader = new ConfigReader();
        int port = configReader.getPort();

        Server server = new Server(port);
        server.start();
        LOGGER.info("Server has been started successful. Server port - " + port);
    }
}
