package com.nc.controller;

import com.nc.model.IOworker;
import com.nc.model.users.Admin;
import com.nc.model.users.BanList;
import com.nc.model.users.ChatRoom;
import com.nc.model.users.User;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import org.apache.log4j.Logger;

public class Server extends Thread {

    private final static Logger LOGGER = Logger.getLogger(Server.class);
    private static final File USERS_DATA = new File("users.bin");

    private int port;
    private User user;
    private Admin admin;
    private List<ClientListener> listenerList = new ArrayList<>();
    private Set<User> users = new HashSet<>();
    private Set<ChatRoom> chatRooms = new HashSet<>();
    private BanList banList = new BanList();

    public Server(int port) {
        this.port = port;
        try {
            IOworker.readBinary(users, USERS_DATA);
        } catch (IOException e) {
            LOGGER.error("Error reading file with users: ", e);
        }
        admin = new Admin("admin", "admin", new Date(), banList);
        users.add(admin);
    }

    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            while (true) {
                System.out.println("About to accept client connection");
                Socket clientSocket = serverSocket.accept();
                System.out.println("Accepted connection from " + clientSocket);
                ClientListener clientListener = new ClientListener(this, clientSocket);
                listenerList.add(clientListener);
                clientListener.start();
            }
        } catch (IOException e) {
            LOGGER.error("Server start error: ", e);
        }
    }

    public List<ClientListener> getListenerList() {
        return listenerList;
    }

    public void removeListener(ClientListener listener) {
        listenerList.remove(listener);
    }

    public Set<User> getUsers() {
        return users;
    }

    public User getUser() {
        return user;
    }

    public  User getUser(String login) {
        User currentUser = null;
        for (User u : users) {
            if (u.getLogin().equals(login)) {
                currentUser = u;
                return currentUser;
            }
        }
        return currentUser;
    }

    public Set<ChatRoom> getChatRooms() {
        return chatRooms;
    }

    public boolean chatExists(String chatRoom) {
        boolean chatExists = false;
        for(ChatRoom room: chatRooms) {
            if (room.getChatName().equals(chatRoom)) {
                chatExists = true;
            }
        }
        return chatExists;
    }

    public ChatRoom getChatRoomByName(String chatName) {
        ChatRoom currChatRomm = new ChatRoom();
        for(ChatRoom chatRoom: chatRooms) {
            if (chatRoom.getChatName().equals(chatName)) {
                currChatRomm = chatRoom;
                return currChatRomm;
            }
        }
        return currChatRomm;
    }

    public boolean isAdmin(User user) {
        if (user instanceof Admin) {
            return true;
        } else {
            return false;
        }
    }
}
